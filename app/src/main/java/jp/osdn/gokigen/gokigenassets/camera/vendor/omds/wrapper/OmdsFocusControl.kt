package jp.osdn.gokigen.gokigenassets.camera.vendor.omds.wrapper

import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IFocusingControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.IOmdsProtocolNotify
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.operation.IOpcFocusLockResult
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.operation.OmdsAutoFocusControl
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import java.lang.Exception

class OmdsFocusControl(private val frameDisplay: IAutoFocusFrameDisplay, indicator: IIndicatorControl) : IFocusingControl, IOmdsProtocolNotify
{
    private val afControl = OmdsAutoFocusControl(frameDisplay, indicator)

    override fun driveAutoFocus(motionEvent: MotionEvent?): Boolean
    {
        Log.v(TAG, "driveAutoFocus()")
        if (motionEvent?.action != MotionEvent.ACTION_DOWN)
        {
            return false
        }
        try
        {
            val point: PointF = frameDisplay.getPointWithEvent(motionEvent) ?: PointF(0.5f, 0.5f)
            if (frameDisplay.isContainsPoint(point))
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

    override fun detectedOpcProtocol(opcProtocol: Boolean)
    {
        afControl.detectedOpcProtocol(opcProtocol)
    }

    fun getFocusLockResult() : IOpcFocusLockResult
    {
        return (afControl)
    }

    companion object
    {
        private val TAG = OmdsFocusControl::class.java.simpleName
    }
}
