package jp.osdn.gokigen.gokigenassets.liveview.bitmapconvert

import android.graphics.Bitmap

interface IPreviewImageConverter
{
    fun getModifiedBitmap(src: Bitmap): Bitmap
}
