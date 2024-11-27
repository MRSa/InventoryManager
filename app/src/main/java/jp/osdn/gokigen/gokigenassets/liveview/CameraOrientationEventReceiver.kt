package jp.osdn.gokigen.gokigenassets.liveview

import android.content.Context
import android.view.OrientationEventListener

class CameraOrientationEventReceiver(context: Context): OrientationEventListener(context)
{
    private var orientation : Int = -1

    override fun onOrientationChanged(p0: Int)
    {
        orientation = p0
    }

    fun isPositionIsClockWise() : Boolean
    {
        return (orientation <= 180)
    }
}
