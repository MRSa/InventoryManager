package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper

interface IPixproCameraInitializer
{
    fun setCommunicationParameter(ip: String, port: Int, lvPort: Int, tcpDelay: Boolean)
    fun parseCommunicationParameter(data: ByteArray)
}
