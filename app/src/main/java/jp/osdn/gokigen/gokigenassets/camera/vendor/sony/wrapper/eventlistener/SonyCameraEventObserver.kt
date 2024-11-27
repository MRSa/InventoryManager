package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.eventlistener

import android.content.Context
import android.os.Handler
import android.util.Log
import org.json.JSONObject
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusHolder
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraChangeListener
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCameraApi
import java.lang.Exception
import java.lang.Thread.sleep


class SonyCameraEventObserver(context: Context, private val remoteApi: ISonyCameraApi, private val sonyStatus: ISonyStatusReceiver) : ISonyCameraEventObserver
{
    private val replyParser = ReplyJsonParser(Handler(context.mainLooper))
    private var isEventMonitoring = false
    private var isActive = false
    private var eventVersion = "1.3" // 初期値を "1.3" に更新

    override fun start(): Boolean
    {
        if (!isActive)
        {
            Log.w(TAG, "start() observer is not active.")
            return (false)
        }
        if (isEventMonitoring)
        {
            Log.w(TAG, "start() already starting.")
            return (false)
        }
        isEventMonitoring = true
        try
        {
            val thread = Thread {
                Log.d(TAG, "start() exec.")
                var firstCall = true
                MONITORLOOP@ while (isEventMonitoring)
                {
                    val longPolling = !firstCall
                    try
                    {
                        val replyJson = remoteApi.getEvent(eventVersion, longPolling) ?: JSONObject()
                        val errorCode = findErrorCode(replyJson)
                        if (errorCode != 0)
                        {
                            Log.d(TAG, "getEvent errorCode: $errorCode")
                        }
                        when (errorCode)
                        {
                            0 -> { }
                            1, 12, 14, 15 -> {
                                if (eventVersion == "1.3")
                                {
                                    // "1.3" でエラーが発生した時には "1.2" にダウングレードして再実行
                                    eventVersion = "1.2"
                                    Log.v(TAG, " === NOT SUPPORT EVENT v1.3, TRY v1.2")
                                    continue@MONITORLOOP
                                }
                                else if (eventVersion == "1.2")
                                {
                                    // "1.2" でエラーが発生した時には "1.1" にダウングレードして再実行
                                    eventVersion = "1.1"
                                    Log.v(TAG, " === NOT SUPPORT EVENT v1.2, TRY v1.1")
                                    continue@MONITORLOOP
                                }
                                else if (eventVersion == "1.1")
                                {
                                    // "1.1" でエラーが発生した時には "1.0" にダウングレードして再実行
                                    eventVersion = "1.0"
                                    Log.v(TAG, " === NOT SUPPORT EVENT v1.1, TRY v1.0")
                                    continue@MONITORLOOP
                                }
                                Log.v(TAG, " === NOT SUPPORT EVENT v1.0... ABORTED.")
                                replyParser.catchResponseError()
                                break@MONITORLOOP  // "1.0" でもエラーが発生した場合は、モニタ終了
                            }
                            2 -> { continue@MONITORLOOP }  // タイムアウト、即時再実行
                            40402 -> {
                                // 5秒待ち後、再実行
                                try
                                {
                                    sleep(5000)
                                }
                                catch (e: InterruptedException)
                                {
                                    // do nothing.
                                }
                                continue@MONITORLOOP
                            }
                            else -> {
                                // その他の応答、、エラー終了
                                Log.w(TAG, "SimpleCameraEventObserver: Unexpected error: $errorCode")
                                replyParser.catchResponseError()
                                break@MONITORLOOP  // モニタ終了
                            }
                        }
                        replyParser.parse(replyJson)
                        sonyStatus.updateStatus(replyJson)
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                    firstCall = false
                } // MONITORLOOP end.
                isEventMonitoring = false
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (true)
    }

    override fun stop()
    {
        isEventMonitoring = false
    }

    override fun release()
    {
        isEventMonitoring = false
        isActive = false
    }

    override fun setEventListener(listener: ICameraChangeListener)
    {
        try
        {
            replyParser.setEventChangeListener(listener)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun clearEventListener()
    {
        try
        {
            replyParser.clearEventChangeListener()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getCameraStatusHolder(): ICameraStatusHolder
    {
        return replyParser
    }

    override fun activate()
    {
        isActive = true
    }

    private fun findErrorCode(replyJson: JSONObject?): Int
    {
        var code = 0 // 0 means no error.
        try
        {
            if (replyJson?.has("error") == true)
            {
                val errorObj = replyJson.getJSONArray("error")
                code = errorObj.getInt(0)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            code = -1
        }
        return (code)
    }

    companion object
    {
        private val TAG = SonyCameraEventObserver::class.java.simpleName

        fun newInstance(context: Context, apiClient: ISonyCameraApi, sonyStatus: ISonyStatusReceiver): ISonyCameraEventObserver
        {
            return SonyCameraEventObserver(context, apiClient, sonyStatus)
        }
    }
}
