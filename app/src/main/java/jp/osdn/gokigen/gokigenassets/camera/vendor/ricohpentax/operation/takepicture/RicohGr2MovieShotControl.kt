package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation.takepicture

import android.util.Log
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient


/**
 *
 *
 */
class RicohGr2MovieShotControl(private val frameDisplayer: IAutoFocusFrameDisplay, executeUrl : String = "http://192.168.0.1")
{
    private val shootStartUrl = "$executeUrl/v1/camera/shoot/start"
    private val shootStopUrl = "$executeUrl/v1/camera/shoot/finish"
    private val timeoutMs = 6000
    private val isMovieRecording = false
    private val httpClient = SimpleHttpClient()

    /**
     *
     *
     */
    fun toggleMovie()
    {
        Log.v(TAG, "toggleMovie()")
        try
        {
            val thread = Thread {
                try
                {
                    val postData = ""
                    val result: String? = httpClient.httpPost(if (isMovieRecording) shootStopUrl else shootStartUrl, postData, timeoutMs)
                    if ((result == null)||(result.isEmpty()))
                    {
                        Log.v(TAG, "toggleMovie() reply is null.")
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
        private val TAG: String = RicohGr2MovieShotControl::class.java.getSimpleName()
    }
}
