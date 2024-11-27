package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic

interface IPanasonicCamera
{
    fun hasApiService(serviceName: String?): Boolean
    fun getApiServices(): List<IPanasonicApiService?>

    fun getFriendlyName(): String
    fun getModelName(): String
    fun getddUrl(): String
    fun getCmdUrl(): String
    fun getObjUrl(): String
    fun getPictureUrl(): String

    fun getClientDeviceUuId(): String

    fun getCommunicationSessionId() : String?
    fun setCommunicationSessionId(sessionId: String?)
}
