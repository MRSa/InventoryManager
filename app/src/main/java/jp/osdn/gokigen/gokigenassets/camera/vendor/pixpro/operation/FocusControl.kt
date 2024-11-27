package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.operation

import android.graphics.PointF
import android.util.Log
import android.view.MotionEvent
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IFocusingControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.IPixproCommandPublisher
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific.PixproExecuteFocus
import java.lang.Exception
import kotlin.math.roundToInt

class FocusControl(private val commandPublisher: IPixproCommandPublisher, private val frameDisplayer: IAutoFocusFrameDisplay?) : IFocusingControl, IPixproCommandCallback
{
    companion object
    {
        private val TAG = FocusControl::class.java.simpleName
    }

    override fun driveAutoFocus(motionEvent: MotionEvent?): Boolean
    {
        if ((motionEvent == null)||(motionEvent.action != MotionEvent.ACTION_DOWN))
        {
            return (false)
        }
        Log.v(TAG, "driveAutoFocus()")
        val thread = Thread {
            try
            {
                val point = frameDisplayer?.getPointWithEvent(motionEvent)
                if (point != null)
                {
                    // preFocusFrameRect = getPreFocusFrameRect(point);
                    // showFocusFrame(preFocusFrameRect, IAutoFocusFrameDisplay.FocusFrameStatus.Running, 1.0);
                    if (frameDisplayer?.isContainsPoint(point) == true)
                    {
                        lockAutoFocus(point)
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
        try
        {
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    override fun unlockAutoFocus()
    {
        try
        {
            Log.v(TAG, " Unlock AF ")
            //commandPublisher.enqueueCommand(new PtpIpCommandGeneric(this, FOCUS_UNLOCK, isDumpLog, 0, 0x9206));
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun halfPressShutter(isPressed: Boolean)
    {
        //unlockAutoFocus();
        //commandPublisher.enqueueCommand(new PtpIpCommandGeneric(this, FOCUS_MOVE, isDumpLog, 0, 0x90c1));
        lockAutoFocus(PointF(0.5f, 0.5f))
    }

    private fun lockAutoFocus(point: PointF)
    {
        // 現物実測で合わせる...
        val maxPointLimitWidth = 991684.0f //  942080.0f // 1000000.0f
        val maxPointLimitHeight = 959912.0f // 942080.0f // 1000000.0f
        try {
            val x = 0x00ffffff and (point.x * maxPointLimitWidth).roundToInt() +  4150 // 1 // 40960
            val y = 0x00ffffff and (point.y * maxPointLimitHeight).roundToInt() + 32528 // 1 // 40960
            Log.v(TAG, "Lock AF: [$x,$y]")
            commandPublisher.enqueueCommand(PixproExecuteFocus(this, x, y))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

/*
    private RectF getPreFocusFrameRect(@NonNull PointF point)
    {
        float imageWidth =  frameDisplayer.getContentSizeWidth();
        float imageHeight =  frameDisplayer.getContentSizeHeight();

        // Display a provisional focus frame at the touched point.
        float focusWidth = 0.125f;  // 0.125 is rough estimate.
        float focusHeight = 0.125f;
        if (imageWidth > imageHeight)
        {
            focusHeight *= (imageWidth / imageHeight);
        }
        else
        {
            focusHeight *= (imageHeight / imageWidth);
        }
        return (new RectF(point.x - focusWidth / 2.0f, point.y - focusHeight / 2.0f,
                point.x + focusWidth / 2.0f, point.y + focusHeight / 2.0f));
    }

    private void showFocusFrame(RectF rect, IAutoFocusFrameDisplay.FocusFrameStatus status, double duration)
    {
        frameDisplayer.showFocusFrame(rect, status, duration);
        indicator.onAfLockUpdate(IAutoFocusFrameDisplay.FocusFrameStatus.Focused == status);
    }

    private void hideFocusFrame()
    {
        frameDisplayer.hideFocusFrame();
        indicator.onAfLockUpdate(false);
    }
*/
    override fun receivedMessage(id: Int, rx_body: ByteArray?)
    {
        Log.v(TAG, " KodakFocusingControl::receivedMessage() : $id ")
    }

}
