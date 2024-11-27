package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.operation

import android.util.Log
import android.view.MotionEvent
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCameraApi
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IFocusingControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.operation.takepicture.AutoFocusControl
import java.lang.Exception

class SonyCameraFocusControl(private val frameDisplay: IAutoFocusFrameDisplay, indicator: IIndicatorControl) : IFocusingControl
{
    private val afControl = AutoFocusControl(frameDisplay, indicator)

    fun setCameraApi(sonyCameraApi: ISonyCameraApi)
    {
        afControl.setCameraApi(sonyCameraApi)
    }

    override fun driveAutoFocus(motionEvent: MotionEvent?): Boolean
    {
        Log.v(TAG, "driveAutoFocus()")
        if (motionEvent?.action != MotionEvent.ACTION_DOWN)
        {
            return (false)
        }
        try
        {
            val point = frameDisplay.getPointWithEvent(motionEvent)
            if ((point != null)&&(frameDisplay.isContainsPoint(point)))
            {
                afControl.lockAutoFocus(point)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return false
    }

    override fun unlockAutoFocus()
    {
        Log.v(TAG, "unlockAutoFocus()")
        try
        {
            afControl.unlockAutoFocus()
            frameDisplay.hideFocusFrame()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun halfPressShutter(isPressed: Boolean)
    {
        Log.v(TAG, "halfPressShutter() $isPressed")
        try
        {
            afControl.halfPressShutter(isPressed)
            if (!isPressed)
            {
                // フォーカスを外す
                frameDisplay.hideFocusFrame()
                afControl.unlockAutoFocus()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = SonyCameraFocusControl::class.java.simpleName
    }
}
