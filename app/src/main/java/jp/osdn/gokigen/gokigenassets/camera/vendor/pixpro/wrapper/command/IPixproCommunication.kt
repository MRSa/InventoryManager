package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command

interface IPixproCommunication
{
    fun connect(): Boolean
    fun disconnect()
}
