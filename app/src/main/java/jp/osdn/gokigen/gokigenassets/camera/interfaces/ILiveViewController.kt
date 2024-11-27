package jp.osdn.gokigen.gokigenassets.camera.interfaces

interface ILiveViewController
{
    fun startLiveView(isCameraScreen: Boolean = false)
    fun stopLiveView()
}
