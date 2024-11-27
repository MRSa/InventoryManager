package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCameraApi
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICaptureControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.operation.takepicture.ContinuousShotControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.operation.takepicture.SingleShotControl
import java.lang.Exception


class SonyCameraCaptureControl(frameDisplayer: IAutoFocusFrameDisplay, indicator: IIndicatorControl) : ICaptureControl
{
    private var isStarted = false
    private val singleShotControl = SingleShotControl(frameDisplayer, indicator)
    private val continuousShotControl = ContinuousShotControl(frameDisplayer, indicator)
    private lateinit var cameraStatus : ICameraStatus

    fun setCameraApi(sonyCameraApi: ISonyCameraApi)
    {
        singleShotControl.setCameraApi(sonyCameraApi)
        continuousShotControl.setCameraApi(sonyCameraApi)
    }

    fun setCameraStatus(sonyCameraStatus: ICameraStatus)
    {
        cameraStatus = sonyCameraStatus
    }

    /**
     * 撮影する
     *
     */
    override fun doCapture(kind: Int)
    {
        Log.v(TAG, "doCapture() : $kind")
        try
        {
            if (::cameraStatus.isInitialized)
            {
                val status = cameraStatus.getStatus(ICameraStatus.DRIVE_MODE).lowercase()
                if (status.contains("cont"))  // 'Continuous' or 'Spd Priority Cont.'
                {
                    // 連写の場合...
                    continuousShotControl.continuousShot(isStarted)
                    isStarted = !isStarted
                    return
                }
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

    companion object
    {
        private val TAG = SonyCameraCaptureControl::class.java.simpleName
    }
}
