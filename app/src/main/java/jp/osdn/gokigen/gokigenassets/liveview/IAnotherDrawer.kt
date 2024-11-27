package jp.osdn.gokigen.gokigenassets.liveview

import android.graphics.Canvas
import android.graphics.RectF

interface IAnotherDrawer
{
    fun onDraw(canvas: Canvas?, imageRectF: RectF, rotationDegrees: Int)
}