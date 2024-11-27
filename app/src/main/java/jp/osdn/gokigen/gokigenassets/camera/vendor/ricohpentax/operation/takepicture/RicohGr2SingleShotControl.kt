package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation.takepicture

import android.util.Log
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient

/**
 *
 *
 *
 */
class RicohGr2SingleShotControl(private val frameDisplayer: IAutoFocusFrameDisplay, executeUrl : String = "http://192.168.0.1")
{
    private val shootUrl = "$executeUrl/v1/camera/shoot"
    private val timeoutMs = 6000
    private val httpClient = SimpleHttpClient()

    /**
     *
     *
     */
    fun singleShot(isCamera: Boolean, isDriveAutoFocus: Boolean) {
        Log.v(TAG, "singleShot()")
        try {
            val thread = Thread {
                try
                {
                    var postData = ""
                    if ((isCamera)&&(isDriveAutoFocus))
                    {
                        // RICOH GR II
                        postData = "af=camera"
                    }
                    else if ((!isCamera)&&(isDriveAutoFocus))
                    {
                        // PENTAX DSLR
                        postData = "af=on"
                    }
                    val result: String? = httpClient.httpPost(shootUrl, postData, timeoutMs)
                    if ((result == null)||(result.isEmpty()))
                    {
                        Log.v(TAG, "singleShot() reply is null.")
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
        private val TAG = RicohGr2SingleShotControl::class.java.simpleName
    }
}
