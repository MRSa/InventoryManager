package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper

import org.json.JSONArray
import org.json.JSONObject

interface ISonyCameraApi
{
    fun getAvailableApiList(): JSONObject?
    fun getApplicationInfo(): JSONObject?
    fun getShootMode(): JSONObject?

    fun setShootMode(shootMode: String): JSONObject?
    fun getAvailableShootMode(): JSONObject?
    fun getSupportedShootMode(): JSONObject?

    fun setTouchAFPosition(Xpos: Double, Ypos: Double): JSONObject?
    fun getTouchAFPosition(): JSONObject?

    fun cancelTouchAFPosition(): JSONObject?
    fun actHalfPressShutter(): JSONObject?
    fun cancelHalfPressShutter(): JSONObject?
    fun setFocusMode(focusMode: String?): JSONObject?
    fun getFocusMode(): JSONObject?
    fun getSupportedFocusMode(): JSONObject?
    fun getAvailableFocusMode(): JSONObject?

    fun startLiveview(): JSONObject?
    fun stopLiveview(): JSONObject?
    fun startRecMode(): JSONObject?
    fun actTakePicture(): JSONObject?
    fun awaitTakePicture(): JSONObject?
    fun startContShooting(): JSONObject?
    fun stopContShooting(): JSONObject?
    fun startMovieRec(): JSONObject?
    fun stopMovieRec(): JSONObject?
    fun actZoom(direction: String, movement: String): JSONObject?
    fun getEvent(version: String, longPollingFlag: Boolean): JSONObject?
    fun setCameraFunction(cameraFunction: String): JSONObject?
    fun getCameraMethodTypes(): JSONObject?
    fun getAvcontentMethodTypes(): JSONObject?
    fun getStorageInformation(): JSONObject?
    fun getSchemeList(): JSONObject?

    fun getSourceList(scheme: String?): JSONObject?
    fun getContentCountFlatAll(uri: String?): JSONObject?
    fun getContentList(params: JSONArray?): JSONObject?
    fun setStreamingContent(uri: String?): JSONObject?
    fun startStreaming(): JSONObject?
    fun stopStreaming(): JSONObject?
    fun getSonyApiServiceList(): List<String?>?

    fun callGenericSonyApiMethod(
        service: String,
        method: String,
        params: JSONArray,
        version: String,
        timeoutMs: Int = -1
    ): JSONObject?

    fun getDdUrl(): String?

    fun actEnableMethods(
        developerName: String?,
        developerID: String?,
        sg: String?,
        methods: String?
    ): JSONObject?

    fun isErrorReply(replyJson: JSONObject?): Boolean
}
