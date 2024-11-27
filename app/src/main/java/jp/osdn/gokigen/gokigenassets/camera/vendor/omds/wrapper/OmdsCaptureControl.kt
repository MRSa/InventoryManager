package jp.osdn.gokigen.gokigenassets.camera.vendor.omds.wrapper

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICaptureControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.IOmdsProtocolNotify
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.operation.OmdsContinuousShotControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.operation.OmdsSingleShotControl
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import java.lang.Exception
import java.util.*

class OmdsCaptureControl(frameDisplay: IAutoFocusFrameDisplay, indicator: IIndicatorControl, val statusChecker : ICameraStatus): ICaptureControl, IOmdsProtocolNotify
{
    private var isStarted = false
    private val singleShotControl = OmdsSingleShotControl(frameDisplay, indicator)
    private val continuousShotControl = OmdsContinuousShotControl(frameDisplay, indicator)

    override fun doCapture(kind: Int)
    {
        Log.v(TAG, "doCapture() : $kind")
        try
        {
            val status = statusChecker.getStatus(ICameraStatus.CAPTURE_MODE).lowercase(Locale.getDefault())
            //Log.v(TAG, "OMDS Capture Mode : $status")
            if (!status.contains("normal"))
            {
                // 連写の場合...
                continuousShotControl.continuousShot(isStarted)
                isStarted = !isStarted
                return
            }

            // 単写の場合...
            singleShotControl.singleShot()
            return
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun detectedOpcProtocol(opcProtocol: Boolean)
    {
        singleShotControl.detectedOpcProtocol(opcProtocol)
        continuousShotControl.detectedOpcProtocol(opcProtocol)
    }

    companion object
    {
        private val TAG: String = OmdsCaptureControl::class.java.simpleName
    }

}
