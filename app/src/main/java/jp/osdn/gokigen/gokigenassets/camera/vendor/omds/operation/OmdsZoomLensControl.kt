package jp.osdn.gokigen.gokigenassets.camera.vendor.omds.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IZoomLensControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.IOmdsProtocolNotify
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import java.lang.Exception
import java.util.HashMap

class OmdsZoomLensControl(statusChecker: ICameraStatus, userAgent: String = "OlympusCameraKit", private val executeUrl : String = "http://192.168.0.10") : IZoomLensControl, IOmdsProtocolNotify
{
    private val headerMap: MutableMap<String, String> = HashMap()
    private val http = SimpleHttpClient()
    private var isZooming = false
    private var useOpcProtocol = false

    override fun canZoom(): Boolean
    {
        Log.v(TAG, "canZoom()")
        return true
    }

    override fun updateStatus()
    {
        Log.v(TAG, "updateStatus()")
    }

    override fun getMaximumFocalLength(): Float
    {
        Log.v(TAG, "getMaximumFocalLength()")
        return (0.0f)
    }

    override fun getMinimumFocalLength(): Float
    {
        Log.v(TAG, "getMinimumFocalLength()")
        return (0.0f)
    }

    override fun getCurrentFocalLength(): Float
    {
        Log.v(TAG, "getCurrentFocalLength()")
        return (0.0f)
    }

    override fun driveZoomLens(targetLength: Float)
    {
        Log.v(TAG, "driveZoomLens() : $targetLength")
    }

    override fun moveInitialZoomPosition()
    {
        Log.v(TAG, "moveInitialZoomPosition()")
    }

    override fun isDrivingZoomLens(): Boolean
    {
        Log.v(TAG, "isDrivingZoomLens()")
        return (false)
    }

    override fun driveZoomLens(isZoomIn: Boolean) {
        Log.v(TAG, "driveZoomLens() : $isZoomIn")
        try {
            val thread = Thread {
                try {
                    val command: String
                    command = if (isZooming)
                    {
                        if (useOpcProtocol)
                        {
                            "/exec_takemisc.cgi?com=newctrlzoom&ctrl=stop"
                        }
                        else
                        {
                            "/exec_takemisc.cgi?com=ctrlzoom&move=off"
                        }
                    }
                    else
                    {
                        if (isZoomIn)
                        {
                            if (useOpcProtocol)
                            {
                                "/exec_takemisc.cgi?com=newctrlzoom&ctrl=start&dir=tele"
                            }
                            else
                            {
                                "/exec_takemisc.cgi?com=ctrlzoom&move=telemove"
                            }
                        }
                        else
                        {
                            if (useOpcProtocol)
                            {
                                "/exec_takemisc.cgi?com=newctrlzoom&ctrl=start&dir=wide"
                            }
                            else
                            {
                                "/exec_takemisc.cgi?com=ctrlzoom&move=widemove"
                             }

                       }
                    }
                    val reply: String = http.httpGetWithHeader(
                        executeUrl + command,
                        headerMap,
                        null,
                        TIMEOUT_MS
                    ) ?: ""
                    isZooming = !isZooming
                    Log.v(TAG, "ZOOM : $isZooming cmd : $command  RET : $reply")
                    /*
                                  if (reply.contains("ok"))
                                  {
                                      isZooming = !isZooming;
                                  }
                                  else
                                  {
                                      Log.v(TAG, "driveZoomLens() reply is failure.");
                                  }
                    */
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()
        } catch (e: Exception) {
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
        private val TAG = OmdsZoomLensControl::class.java.simpleName
        private const val TIMEOUT_MS = 3000
    }

    init
    {
        headerMap["User-Agent"] = userAgent // "OlympusCameraKit" // "OI.Share"
        headerMap["X-Protocol"] = userAgent // "OlympusCameraKit" // "OI.Share"
    }
}
