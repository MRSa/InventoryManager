package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper

interface ISonyCameraHolder
{
    fun detectedCamera(camera: ISonyCamera)
    fun prepare()
    fun startRecMode()
    fun startEventWatch()
}
