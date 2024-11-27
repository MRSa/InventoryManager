package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic

import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraChangeListener


interface IPanasonicCameraHolder
{
    fun detectedCamera(camera: IPanasonicCamera)
    fun prepare()
    fun startRecMode()
    fun startEventWatch(listener: ICameraChangeListener?)
}