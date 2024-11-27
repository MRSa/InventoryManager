package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation.takepicture

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient

class SingleShotControl(private val frameDisplayer: IAutoFocusFrameDisplay)
{
    private lateinit var camera: IPanasonicCamera

    fun setCamera(panasonicCamera: IPanasonicCamera)
    {
        camera = panasonicCamera
    }

    fun singleShot()
    {
        Log.v(TAG, "singleShot()")
        if (!::camera.isInitialized)
        {
            Log.v(TAG, "IPanasonicCamera is not initialized...")
            return
        }
        try
        {
            val http = SimpleHttpClient()
            val thread = Thread {
                try
                {
                    val sessionId = camera.getCommunicationSessionId()
                    val urlToSend = "${camera.getCmdUrl()}cam.cgi?mode=camcmd&value=capture"
                    val reply = if (!sessionId.isNullOrEmpty())
                    {
                        val headerMap: MutableMap<String, String> = HashMap()
                        headerMap["X-SESSION_ID"] = sessionId
                        http.httpGetWithHeader(urlToSend, headerMap, null, TIMEOUT_MS) ?: ""
                    }
                    else
                    {
                        http.httpGet(urlToSend, TIMEOUT_MS)
                    }
                    if (!reply.contains("ok"))
                    {
                        Log.v(TAG, "Capture Failure... : $reply")
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                frameDisplayer.hideFocusFrame()
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = SingleShotControl::class.java.simpleName
        private const val TIMEOUT_MS = 3000
    }
}
