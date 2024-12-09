package jp.osdn.gokigen.gokigenassets.camera.interfaces

interface ICameraShutter
{
    fun doShutter(id: Int = 0)
    fun doShutterOff()
}
