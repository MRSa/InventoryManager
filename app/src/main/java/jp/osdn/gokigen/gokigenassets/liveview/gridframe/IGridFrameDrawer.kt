package jp.osdn.gokigen.gokigenassets.liveview.gridframe

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF

interface IGridFrameDrawer
{
    fun drawFramingGrid(canvas: Canvas, rect: RectF, color : Int = Color.argb(130, 235, 235, 235), rotationDegrees: Int)
}
