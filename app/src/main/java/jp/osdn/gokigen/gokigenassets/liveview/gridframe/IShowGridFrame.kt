package jp.osdn.gokigen.gokigenassets.liveview.gridframe

import android.graphics.Color

interface IShowGridFrame
{
    fun showGridFrame(isShowGrid : Boolean, color : Int = Color.argb(130, 235, 235, 235))
}
