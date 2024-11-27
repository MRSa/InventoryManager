package jp.osdn.gokigen.gokigenassets.liveview.image


interface ILiveViewListener
{
    fun getImageByteArray() : ByteArray
    fun setCameraLiveImageView(target: IImageDataReceiver)
}
