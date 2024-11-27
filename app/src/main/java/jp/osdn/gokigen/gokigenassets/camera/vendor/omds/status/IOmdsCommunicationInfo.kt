package jp.osdn.gokigen.gokigenassets.camera.vendor.omds.status

interface IOmdsCommunicationInfo
{
    fun setOmdsCommandList(commandList: String)
    fun startReceiveOpcEvent()
}
