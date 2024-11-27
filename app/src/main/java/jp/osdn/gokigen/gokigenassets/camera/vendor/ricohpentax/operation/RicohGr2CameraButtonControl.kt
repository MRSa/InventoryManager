package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation.IRicohGr2ButtonControl.Companion.SPECIAL_GREEN_BUTTON
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient


/**
 *
 *
 */
class RicohGr2CameraButtonControl(executeUrl : String = "http://192.168.0.1") : IRicohGr2ButtonControl
{
    private val buttonControlUrl = "$executeUrl/_gr"
    private val greenButtonUrl = "$executeUrl/v1/params/camera"
    private val timeoutMs = 6000
    private val httpClient = SimpleHttpClient()

    companion object
    {
        private val TAG: String = RicohGr2CameraButtonControl::class.java.getSimpleName()
    }

    /**
     *
     *
     */
    override fun pushedButton(code: String, isLongPress: Boolean): Boolean
    {
        return pushButton(code, isLongPress)
    }

    /**
     *
     *
     */
    private fun pushButton(keyName: String, isLongPress: Boolean): Boolean
    {
        Log.v(TAG, "pushButton()")
        if (keyName == SPECIAL_GREEN_BUTTON)
        {
            // Greenボタンの処理を入れる
            return processGreenButton(isLongPress)
        }
        try
        {
            val thread = Thread {
                try
                {
                    var cmd = "cmd=$keyName"
                    if (isLongPress) {
                        // ボタン長押しの場合...
                        cmd = "$cmd 1"
                    }
                    val result: String? =
                        httpClient.httpPost(buttonControlUrl, cmd, timeoutMs)
                    if (result == null || result.isEmpty()) {
                        Log.v(TAG, "pushButton() reply is null. $cmd")
                    } else {
                        Log.v(TAG, "pushButton() $cmd result: $result")
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
        return true
    }

    private fun processGreenButton(isLongPress: Boolean): Boolean {
        Log.v(TAG, "processGreenButton()")
        try {
            val thread = Thread {
                try {
                    val cmd = ""
                    val result: String? =
                        httpClient.httpPut(greenButtonUrl, cmd, timeoutMs)
                    if (result == null || result.isEmpty()) {
                        Log.v(TAG, "processGreenButton() reply is null.")
                    } else {
                        Log.v(TAG, "processGreenButton() result: $result")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }
}
