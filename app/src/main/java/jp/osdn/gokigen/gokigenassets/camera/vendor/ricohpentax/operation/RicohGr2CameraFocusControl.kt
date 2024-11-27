package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation

import android.graphics.PointF
import android.view.MotionEvent
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IFocusingControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation.takepicture.RicohGr2AutoFocusControl
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay

/**
 *
 *
 */
class RicohGr2CameraFocusControl(private val frameDisplay: IAutoFocusFrameDisplay, indicator: IIndicatorControl) : IFocusingControl
{
    private val afControl = RicohGr2AutoFocusControl(frameDisplay, indicator)

    companion object
    {
        private val TAG: String = RicohGr2CameraFocusControl::class.java.getSimpleName()
    }

    fun setUseGR2Command(useGR2command: Boolean)
    {
        afControl.setUseGR2Command(useGR2command)
    }

    /**
     *
     *
     */
    override fun driveAutoFocus(motionEvent: MotionEvent?): Boolean {
        //Log.v(TAG, "driveAutoFocus()")
        if ((motionEvent == null)||(motionEvent.action != MotionEvent.ACTION_DOWN))
        {
            return false
        }
        try
        {
            val point: PointF? = frameDisplay.getPointWithEvent(motionEvent)
            if ((point != null)&&(frameDisplay.isContainsPoint(point)))
            {
                //Log.v(TAG, "driveAutoFocus() : [$point.x, $point.y]")
                afControl.lockAutoFocus(point)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return false
    }

    /**
     *
     *
     */
    override fun unlockAutoFocus()
    {
        afControl.unlockAutoFocus()
    }

    override fun halfPressShutter(isPressed: Boolean) {}

}
