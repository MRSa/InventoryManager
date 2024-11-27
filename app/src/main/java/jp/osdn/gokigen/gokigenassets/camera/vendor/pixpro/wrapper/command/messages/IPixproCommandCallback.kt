package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages

interface IPixproCommandCallback
{
    fun receivedMessage(id: Int, rx_body: ByteArray?)
}
