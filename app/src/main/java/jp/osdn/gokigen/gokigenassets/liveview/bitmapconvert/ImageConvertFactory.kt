package jp.osdn.gokigen.gokigenassets.liveview.bitmapconvert

class ImageConvertFactory
{
    fun getImageConverter(id: Int): IPreviewImageConverter
    {
        return (ConvertNothing())
    }
}
