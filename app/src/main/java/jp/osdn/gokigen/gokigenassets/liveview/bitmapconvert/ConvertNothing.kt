package jp.osdn.gokigen.gokigenassets.liveview.bitmapconvert

import android.graphics.Bitmap

class ConvertNothing : IPreviewImageConverter
{
    override fun getModifiedBitmap(src: Bitmap): Bitmap
    {
        return (src)
    }
}
