package jp.osdn.gokigen.gokigenassets.camera.interfaces

import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider

interface ICameraShutterNotify
{
    fun doShutter(id: Int = 0, imageProvider: IImageProvider)
}