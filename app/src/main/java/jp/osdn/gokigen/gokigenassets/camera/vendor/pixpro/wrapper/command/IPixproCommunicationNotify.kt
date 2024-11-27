package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command

interface IPixproCommunicationNotify
{
    fun readyToCommunicate()
    fun detectDisconnect()
}
