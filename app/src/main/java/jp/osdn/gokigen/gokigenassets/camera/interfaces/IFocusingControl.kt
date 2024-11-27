package jp.osdn.gokigen.gokigenassets.camera.interfaces

import android.view.MotionEvent

interface IFocusingControl
{
    fun driveAutoFocus(motionEvent: MotionEvent?): Boolean
    fun unlockAutoFocus()
    fun halfPressShutter(isPressed: Boolean)
}