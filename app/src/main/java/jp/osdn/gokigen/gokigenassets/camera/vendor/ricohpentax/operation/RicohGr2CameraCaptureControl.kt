package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICaptureControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation.takepicture.RicohGr2MovieShotControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation.takepicture.RicohGr2SingleShotControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay

/**
 *
 *
 */
class RicohGr2CameraCaptureControl(private val captureAfterAf: Boolean, frameDisplayer: IAutoFocusFrameDisplay, private val cameraStatus: ICameraStatus) : ICaptureControl
{
    private val singleShotControl = RicohGr2SingleShotControl(frameDisplayer)
    private val movieShotControl = RicohGr2MovieShotControl(frameDisplayer)
    private var useGR2command = false

    /**
     *
     *
     */
    companion object
    {
        private val TAG = RicohGr2CameraCaptureControl::class.java.simpleName
    }

    fun setUseGR2Command(useGR2command: Boolean)
    {
        this.useGR2command = useGR2command
    }

    /**
     *
     *
     */
    override fun doCapture(kind: Int)
    {
        try
        {
            Log.v(TAG, "doCapture : $kind")
            //val driveModeStatus = cameraStatus.getStatus((IRicohGr2CameraStatus.DRIVE_MODE))
            val status = cameraStatus.getStatus(IRicohGr2CameraStatus.TAKE_MODE)
            if (status.contains(IRicohGr2CameraStatus.TAKE_MODE_MOVIE))
            {
                movieShotControl.toggleMovie()
            }
            else
            {
                singleShotControl.singleShot(useGR2command, captureAfterAf)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
