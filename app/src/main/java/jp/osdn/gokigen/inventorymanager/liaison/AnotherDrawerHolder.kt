package jp.osdn.gokigen.inventorymanager.liaison

import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import jp.osdn.gokigen.gokigenassets.liveview.IAnotherDrawer
import java.util.ArrayList

class AnotherDrawerHolder : IAnotherDrawer
{
    private val drawers = ArrayList<IAnotherDrawer>()

    fun addAnotherDrawer(anotherDrawer: IAnotherDrawer)
    {
        try
        {
            Log.v(TAG, "add Drawer : ${drawers.size}")
            drawers.add(anotherDrawer)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onDraw(canvas: Canvas?, imageRectF: RectF, rotationDegrees: Int)
    {
        try
        {
            for (d in drawers)
            {
                d.onDraw(canvas, imageRectF, rotationDegrees)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = AnotherDrawerHolder::class.java.simpleName
    }
}