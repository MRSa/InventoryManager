package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper

import android.util.Log

import java.lang.Exception


class SonyCameraDeviceProvider(private val ddUrl: String, private val friendlyName: String, private val modelName: String, udn: String, iconUrl: String) : ISonyCamera
{
    private val apiServices: MutableList<ISonyApiService> = ArrayList()
    init
    {
        Log.v(TAG, "Sony Device : $friendlyName($modelName) $ddUrl  $udn $iconUrl")
    }

    override fun hasApiService(serviceName: String): Boolean
    {
        try
        {
            for (apiService in apiServices)
            {
                if (serviceName == apiService.getName())
                {
                    return (true)
                }
            }
            Log.v(TAG, "no API Service : " + serviceName + "[" + apiServices.size + "]")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    override fun getApiServices(): List<ISonyApiService> { return apiServices }
    override fun getFriendlyName(): String { return friendlyName }
    override fun getModelName(): String { return modelName }
    override fun getDdUrl(): String { return ddUrl }

    fun addApiService(name: String, actionUrl: String)
    {
        Log.v(TAG, "API : $name  : $actionUrl")
        val service = SonyApiService(name, actionUrl)
        apiServices.add(service)
    }

    companion object
    {
        private val TAG = SonyCameraDeviceProvider::class.java.simpleName
    }
}
