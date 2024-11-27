package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper

interface IPixproCamera
{
    fun isAvailable() : Boolean
    fun getIpAddress() : String
    fun getPortNumber() : Int
    fun getLiveViewPortNumber() : Int
    fun getTcpNoDelay() : Boolean

}