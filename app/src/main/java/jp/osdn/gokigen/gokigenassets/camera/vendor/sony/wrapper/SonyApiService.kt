package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper

internal class SonyApiService(private val name: String, private val actionUrl: String) : ISonyApiService
{
    override fun getName(): String
    {
        return name
    }

    override fun getActionUrl(): String
    {
        return actionUrl
    }
}
