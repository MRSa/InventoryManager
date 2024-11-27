package jp.osdn.gokigen.gokigenassets.camera.console


import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class ConsolePanelGestureListener(private val positionArea : IDetectPositionReceiver) : GestureDetector.SimpleOnGestureListener(), ScaleGestureDetector.OnScaleGestureListener
{

    // ScaleGestureDetector.OnScaleGestureListener
    override fun onScale(detector: ScaleGestureDetector): Boolean
    {
        return (false)
    }

    // ScaleGestureDetector.OnScaleGestureListener
    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean
    {
        return (false)
    }

    // ScaleGestureDetector.OnScaleGestureListener
    override fun onScaleEnd(detector: ScaleGestureDetector)
    {
        //
    }

    override fun onLongPress(e: MotionEvent)
    {
        super.onLongPress(e)
        try
        {
            positionArea.onLongPress(e.x, e.y)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean
    {
        var ret = false
        super.onSingleTapUp(e)
        try
        {
            ret = positionArea.onSingleTapUp(e.x, e.y)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (ret)
    }

/*
    private fun onUp(event: MotionEvent) : Boolean
    {
        Log.v(ConsolePanelControl.TAG, "onTouch() UP : [" + event.x + "," + event.y + "] ")

        touchedX = -1.0f
        touchedY = -1.0f

        return (false)
    }

    private fun onMove(event: MotionEvent) : Boolean
    {
        Log.v(ConsolePanelControl.TAG, "onTouch() MOVE : [" + event.x + "," + event.y + "] ")


        return (false)
    }

    private fun onDown(event: MotionEvent) : Boolean
    {
        Log.v(ConsolePanelControl.TAG, "onTouch() DOWN : [" + event.x + "," + event.y + "] ")
        checkArea(event)

        return (false)
    }
*/

}