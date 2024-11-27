package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICaptureControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation.takepicture.ContinuousShotControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation.takepicture.SingleShotControl
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay


class PanasonicCameraCaptureControl(frameDisplayer: IAutoFocusFrameDisplay, private val indicator: IIndicatorControl) : ICaptureControl
{
    private var isStarted = false
    private var singleShotControl = SingleShotControl(frameDisplayer)
    private var continuousShotControl = ContinuousShotControl(frameDisplayer)
    private lateinit var cameraStatus : ICameraStatus

    fun setCamera(panasonicCamera: IPanasonicCamera)
    {
        singleShotControl.setCamera(panasonicCamera)
        continuousShotControl.setCamera(panasonicCamera)
    }

    fun setCameraStatus(panasonicCameraStatus: ICameraStatus)
    {
        cameraStatus = panasonicCameraStatus
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
                if (cameraStatus.getStatus(ICameraStatus.FOCUS_STATUS).isNotEmpty())
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
        private val TAG = PanasonicCameraCaptureControl::class.java.simpleName
    }
}
