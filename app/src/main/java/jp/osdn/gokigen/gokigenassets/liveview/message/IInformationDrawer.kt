package jp.osdn.gokigen.gokigenassets.liveview.message

import android.graphics.Canvas
import android.graphics.RectF

interface IInformationDrawer
{
    fun drawInformationMessages(canvas : Canvas, rect : RectF)
    fun drawLevelGauge(canvas : Canvas, rotationDegrees : Int = 0)
}
