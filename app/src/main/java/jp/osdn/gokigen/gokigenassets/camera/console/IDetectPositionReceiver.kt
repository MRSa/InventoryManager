package jp.osdn.gokigen.gokigenassets.camera.console

import android.view.MotionEvent

interface IDetectPositionReceiver
{
    fun onLongPress(positionX: Float, positionY: Float)
    fun onSingleTapUp(positionX: Float, positionY: Float): Boolean
}