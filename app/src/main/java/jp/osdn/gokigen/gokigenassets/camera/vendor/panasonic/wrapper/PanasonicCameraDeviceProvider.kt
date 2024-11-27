package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.wrapper

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicApiService
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import java.util.*
import kotlin.collections.ArrayList

class PanasonicCameraDeviceProvider(private val ddUrl: String, private val friendlyName: String, private val modelName: String, private val udn: String, private val iconUrl: String) : IPanasonicCamera
{
    private var apiServices: List<IPanasonicApiService> = ArrayList()
    private val uniqueID: String = UUID.randomUUID().toString()
    private var sessionId : String? = null

    init
    {
        Log.v(TAG, "Panasonic Device : " + this.friendlyName + "(" + this.modelName + ") " + this.ddUrl + "  " + this.udn + " [" + this.iconUrl + "]")
        Log.v(TAG, "ANDROID DEVICE : $uniqueID")
    }

    override fun hasApiService(serviceName: String?): Boolean
    {
        try
        {
            for (apiService in apiServices)
            {
                if (serviceName == apiService.getName())
                {
                    return true
                }
            }
            Log.v(TAG, "no API Service : " + serviceName + "[" + apiServices.size + "]")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return false
    }

    override fun getApiServices(): List<IPanasonicApiService>
    {
        return apiServices
    }

    override fun getFriendlyName(): String
    {
        return friendlyName
    }

    override fun getModelName(): String
    {
        return modelName
    }

    override fun getddUrl(): String
    {
        return ddUrl
    }

    override fun getCmdUrl(): String
    {
        // コマンド送信先を応答する
        return ddUrl.substring(0, ddUrl.indexOf(":", 7)) + "/"
    }

    override fun getObjUrl(): String
    {
        // オブジェクト取得用の送信先を応答する
        return ddUrl.substring(0, ddUrl.indexOf("/", 7)) + "/"
    }

    override fun getPictureUrl(): String
    {
        // 画像取得先を応答する
        return ddUrl.substring(0, ddUrl.indexOf(":", 7)) + ":50001/"
    }

    override fun getClientDeviceUuId(): String
    {
        return uniqueID
    }

    override fun getCommunicationSessionId(): String?
    {
        return (sessionId)
    }

    override fun setCommunicationSessionId(sessionId: String?)
    {
        this.sessionId = sessionId
    }

    companion object
    {
        private val TAG = PanasonicCameraDeviceProvider::class.java.simpleName
    }
}
