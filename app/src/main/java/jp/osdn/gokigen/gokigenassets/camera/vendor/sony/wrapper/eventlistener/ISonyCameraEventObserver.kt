package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.eventlistener

import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraChangeListener
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusHolder

interface ISonyCameraEventObserver
{
    fun activate()
    fun start(): Boolean
    fun stop()
    fun release()

    fun setEventListener(listener: ICameraChangeListener)
    fun clearEventListener()

    fun getCameraStatusHolder(): ICameraStatusHolder?
}
