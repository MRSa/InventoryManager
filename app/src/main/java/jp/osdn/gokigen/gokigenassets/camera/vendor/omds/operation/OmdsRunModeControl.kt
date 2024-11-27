package jp.osdn.gokigen.gokigenassets.camera.vendor.omds.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraRunMode
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraRunModeCallback
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import java.lang.Exception
import java.util.HashMap


class OmdsRunModeControl(private val liveViewQuality : String = "0640x0480", userAgent: String = "OlympusCameraKit", private val executeUrl : String = "http://192.168.0.10") : ICameraRunMode
{
    private val headerMap: MutableMap<String, String> = HashMap()
    private val http = SimpleHttpClient()
    private var runMode = false
    private var lvQuality = liveViewQuality

    override fun changeRunMode(isRecording: Boolean, callback: ICameraRunModeCallback)
    {
        try
        {
            Log.v(TAG, " changeRunMode : $isRecording")
            val thread = Thread { // カメラとの接続確立を通知する
                var playModeUrl = "$executeUrl/switch_cammode.cgi"
                playModeUrl = if (isRecording)
                {
                    "$playModeUrl?mode=rec&lvqty=$lvQuality"
                }
                else
                {
                    "$playModeUrl?mode=play"
                }
                val response: String = http.httpGetWithHeader(playModeUrl, headerMap, null, TIMEOUT_MS) ?: ""
                Log.v(TAG, " $playModeUrl $response")
                try
                {
                    if (response.contains("ok"))
                    {
                        runMode = isRecording
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

    override fun isRecordingMode(): Boolean
    {
        getCurrentPlayMode()
        return (runMode)
    }

    fun setLiveviewQuality(quality: String?)
    {
        lvQuality = quality ?: liveViewQuality
    }

    private fun getCurrentPlayMode() : Boolean
    {
        val isRecordingMode = false
        try
        {
            val getConnectModeUrl = "$executeUrl/get_connectmode.cgi"
            val response: String = http.httpGetWithHeader(getConnectModeUrl, headerMap, null, TIMEOUT_MS) ?: ""
            Log.v(TAG, " $getConnectModeUrl $response")
            if (response.isNotEmpty())
            {
                Log.v(TAG, " RESPONSE : $response")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (isRecordingMode)
    }

    init
    {
        headerMap["User-Agent"] = userAgent // "OlympusCameraKit" // "OI.Share"
        headerMap["X-Protocol"] = userAgent // "OlympusCameraKit" // "OI.Share"
    }

    companion object
    {
        private val TAG = OmdsRunModeControl::class.java.simpleName
        private const val TIMEOUT_MS = 5000
    }
}
