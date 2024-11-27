package jp.osdn.gokigen.gokigenassets.liveview.focusframe

import android.graphics.Canvas

interface IFocusFrameDrawer
{
    fun drawFocusFrame(canvas: Canvas, imageWidth: Float, imageHeight: Float)
}