package jp.osdn.gokigen.gokigenassets.liveview.image


interface IImageDataReceiver
{
    fun onUpdateLiveView(data: ByteArray, metadata: Map<String, Any>?, degrees : Int = 0)
}
