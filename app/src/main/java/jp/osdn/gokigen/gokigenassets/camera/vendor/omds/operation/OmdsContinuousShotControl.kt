package jp.osdn.gokigen.gokigenassets.camera.vendor.omds.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.IOmdsProtocolNotify
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import java.lang.Exception
import java.util.HashMap


class OmdsContinuousShotControl(private val frameDisplayer: IAutoFocusFrameDisplay, indicator: IIndicatorControl, userAgent: String = "OlympusCameraKit", private val executeUrl : String = "http://192.168.0.10") : IOmdsProtocolNotify
{
    private val headerMap: MutableMap<String, String> = HashMap()
    private val http = SimpleHttpClient()
    private var useOpcProtocol = false

    fun continuousShot(isStop: Boolean)
    {
        Log.v(TAG, "continuousShot()")
        try
        {
            val thread = Thread {
                try
                {
                    val sendUrl = if (isStop) { if (useOpcProtocol) { executeUrl + CAPTURE_STOP_COMMAND_OPC } else { executeUrl + CAPTURE_STOP_COMMAND } } else { if (useOpcProtocol) { executeUrl + CAPTURE_COMMAND_OPC } else { executeUrl + CAPTURE_COMMAND } }
                    val reply: String = http.httpGetWithHeader(sendUrl, headerMap, null, TIMEOUT_MS)?.lowercase() ?: ""
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

    override fun detectedOpcProtocol(opcProtocol: Boolean)
    {
        Log.v(TAG, " --- detectedOpcProtocol($opcProtocol)")
        useOpcProtocol = opcProtocol
    }

    companion object
    {
        private val TAG: String = OmdsSingleShotControl::class.java.simpleName
        private const val TIMEOUT_MS = 3000
        private const val CAPTURE_COMMAND = "/exec_takemotion.cgi?com=starttake"
        private const val CAPTURE_COMMAND_OPC = "/exec_takemotion.cgi?com=newstarttake"
        private const val CAPTURE_STOP_COMMAND = "/exec_takemotion.cgi?com=stoptake"
        private const val CAPTURE_STOP_COMMAND_OPC = "/exec_takemotion.cgi?com=newstoptake"

    }

    init
    {
        headerMap["User-Agent"] = userAgent // "OlympusCameraKit" // "OI.Share"
        headerMap["X-Protocol"] = userAgent // "OlympusCameraKit" // "OI.Share"
    }
}
