package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.status

interface ICameraEventObserver
{
    fun receivedEvent(eventData: ByteArray?)
}
