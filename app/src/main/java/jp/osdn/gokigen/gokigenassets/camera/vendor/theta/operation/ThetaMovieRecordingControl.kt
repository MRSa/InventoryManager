package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ILiveViewController
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status.IThetaSessionIdProvider
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status.IThetaStatusHolder
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient

class ThetaMovieRecordingControl(private val sessionIdProvider: IThetaSessionIdProvider, private val vibrator: IVibrator, private val liveViewControl : ILiveViewController, private val statusHolder : IThetaStatusHolder, private val executeUrl : String = "http://192.168.1.1")
{
    private val httpClient = SimpleHttpClient()

    fun movieControl(useOSCv2 : Boolean)
    {
        try
        {
            if (statusHolder.captureStatus.contains("idle"))
            {
                startCapture(useOSCv2)
            }
            else
            {
                stopCapture(useOSCv2)
            }
            statusHolder.invalidate()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun startCapture(useOSCv2 : Boolean)
    {
        Log.v(TAG, "startCapture() (API v2.1 : $useOSCv2)")
        try
        {
            val thread = Thread {
                try
                {
                    val shootUrl = "$executeUrl/commands/execute"
                    val postData = if (useOSCv2) "{\"name\":\"camera.startCapture\",\"parameters\":{\"timeout\":0}}" else "{\"name\":\"camera._startCapture\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\"}}"

                    Log.v(TAG, " start Capture : $postData")
                    val result: String? = httpClient.httpPostWithHeader(shootUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                    if ((result != null)&&(result.isNotEmpty()))
                    {
                        Log.v(TAG, " startCapture() : $result")
                    }
                    else
                    {
                        Log.v(TAG, "startCapture() reply is null.  $postData")
                    }
                    // 撮影開始をバイブレータで知らせる
                    vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT)
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

    private fun stopCapture(useOSCv2 : Boolean)
    {
        Log.v(TAG, "stopCapture() (API v2.1 : $useOSCv2)")
        try
        {
            val thread = Thread {
                try
                {
                    val shootUrl = "$executeUrl/osc/commands/execute"
                    val postData = if (useOSCv2) "{\"name\":\"camera.stopCapture\",\"parameters\":{\"timeout\":0}}" else "{\"name\":\"camera._stopCapture\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\"}}"

                    Log.v(TAG, " stop Capture : $postData")

                    val result: String? = httpClient.httpPostWithHeader(shootUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                    if ((result != null)&&(result.isNotEmpty()))
                    {
                        Log.v(TAG, " stopCapture() : $result")
                        if (!useOSCv2)
                        {
                            // THETA V / THETA Z1 は、videoモードでライブビューができるので...
                            liveViewControl.stopLiveView()
                            waitMs() // ちょっと待つ...
                            liveViewControl.startLiveView()
                        }
                        // 撮影終了をバイブレータで知らせる
                        vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_LONG)
                    }
                    else
                    {
                        Log.v(TAG, "stopCapture() reply is null. $postData")
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

    /**
     *
     *
     */
    private fun waitMs(waitMs: Int = 300)
    {
        try
        {
            Thread.sleep(waitMs.toLong())
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = ThetaMovieRecordingControl::class.java.simpleName
        private const val timeoutMs = 4000
    }
}
