package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.liveview

import android.content.Context
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ILiveViewController
import org.json.JSONObject
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCameraApi
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageDataReceiver
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleLiveViewSlicer
import org.json.JSONArray
import java.lang.Exception


class SonyLiveViewControl(context: Context, private val informationReceiver: IInformationReceiver, private val imageDataReceiver: IImageDataReceiver, private val cameraApi: ISonyCameraApi) : ILiveViewController
{
    private var whileFetching = false

    override fun startLiveView(isCameraScreen: Boolean)
    {
        Log.v(TAG, "startLiveView() : $isCameraScreen")
        try
        {
            val thread = Thread {
                try
                {
                    val replyJson: JSONObject? = cameraApi.startLiveview()
                    if (!cameraApi.isErrorReply(replyJson))
                    {
                        try
                        {
                            val resultsObj = replyJson?.getJSONArray("result") ?: JSONArray()
                            if (1 <= resultsObj.length())
                            {
                                // Obtain liveview URL from the result.
                                val liveviewUrl = resultsObj.getString(0)
                                start(liveviewUrl)
                            }
                        }
                        catch (e: Exception)
                        {
                            e.printStackTrace()
                        }
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun stopLiveView()
    {
        Log.v(TAG, "stopLiveView()")
        try
        {
            val thread = Thread {
                try
                {
                    val resultsObj = cameraApi.stopLiveview()
                    if (resultsObj == null)
                    {
                        Log.v(TAG, "stopLiveview() reply is null.")
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun start(streamUrl: String?): Boolean
    {
        if (streamUrl == null)
        {
            Log.e(TAG, "start() streamUrl is null.")
            return false
        }
        if (whileFetching)
        {
            Log.v(TAG, "start() already starting.")
        }
        whileFetching = true

        // A thread for retrieving liveview data from server.
        try
        {
            val thread = Thread {
                Log.d(TAG, "Starting retrieving streaming data from server.")
                var slicer: SimpleLiveViewSlicer? = null
                var continuousNullDataReceived = 0
                try
                {
                    // Create Slicer to open the stream and parse it.
                    slicer = SimpleLiveViewSlicer()
                    slicer.open(streamUrl)
                    while (whileFetching)
                    {
                        val payload: SimpleLiveViewSlicer.Payload? = slicer.nextPayload()
                        if (payload == null)
                        {
                            //Log.v(TAG, "Liveview Payload is null.");
                            continuousNullDataReceived++
                            if (continuousNullDataReceived > FETCH_ERROR_MAX)
                            {
                                Log.d(TAG, " FETCH ERROR MAX OVER ")
                                break
                            }
                            continue
                        }
                        val jpegData = payload.getJpegData()
                        if (jpegData != null)
                        {
                            imageDataReceiver.onUpdateLiveView(jpegData, null)
                        }
                        continuousNullDataReceived = 0
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                finally
                {
                    try
                    {
                        slicer?.close()
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                    if (!whileFetching && continuousNullDataReceived > FETCH_ERROR_MAX)
                    {
                        // 再度ライブビューのスタートをやってみる。
                        whileFetching = false
                        //continuousNullDataReceived = 0;
                        start(streamUrl)
                    }
                }
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (true)
    }

    companion object
    {
        private const val FETCH_ERROR_MAX = 30
        private val TAG = SonyLiveViewControl::class.java.simpleName
    }
}
