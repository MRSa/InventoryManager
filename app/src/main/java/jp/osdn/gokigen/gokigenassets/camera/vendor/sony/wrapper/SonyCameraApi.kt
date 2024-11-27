package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper

import android.util.Log
import org.json.JSONObject
import org.json.JSONArray
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import java.lang.Exception

class SonyCameraApi(private val sonyCamera: ISonyCamera) : ISonyCameraApi
{
    private var requestId = 1
    private val httpClient = SimpleHttpClient()
    private fun findActionListUrl(service: String): String?
    {
        val services = sonyCamera.getApiServices()
        for (apiService in services)
        {
            if (apiService.getName() == service)
            {
                return (apiService.getActionUrl())
            }
        }
        Log.v(TAG, "actionUrl not found. service : $service")
        return (null)
    }

    private fun id(): Int
    {
        requestId++
        if (requestId == 0)
        {
            requestId++
        }
        return requestId
    }

    private fun log(msg: String)
    {
        if (FULL_LOG)
        {
            Log.d(TAG, msg)
        }
    }

    private fun communicateJSON(service: String, method: String, params: JSONArray, version: String, timeoutMs: Int): JSONObject
    {
        try
        {
            val requestJson = JSONObject().put("method", method)
                .put("params", params)
                .put("id", id())
                .put("version", version)
            val url = findActionListUrl(service) + "/" + service
            log("Request:  $requestJson")
            val responseJson: String = httpClient.httpPost(url, requestJson.toString(), timeoutMs) ?: ""
            log("Response: $responseJson")
            return JSONObject(responseJson)
        }
        catch (e: Exception)
        {
            log("Exception : $method $version")
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getAvailableApiList(): JSONObject
    {
        try {
            return communicateJSON("camera", "getAvailableApiList", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getApplicationInfo(): JSONObject
    {
        try {
            return communicateJSON("camera", "getApplicationInfo", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getShootMode(): JSONObject
    {
        try {
            return communicateJSON("camera", "getShootMode", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun setShootMode(shootMode: String): JSONObject
    {
        try {
            return communicateJSON("camera", "getShootMode", JSONArray().put(shootMode), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getAvailableShootMode(): JSONObject
    {
        try {
            return communicateJSON("camera", "getAvailableShootMode", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getSupportedShootMode(): JSONObject
    {
        try {
            return communicateJSON("camera", "getSupportedShootMode", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun setTouchAFPosition(Xpos: Double, Ypos: Double): JSONObject
    {
        try {
            Log.v(TAG, "setTouchAFPosition ($Xpos, $Ypos)")
            return communicateJSON(
                "camera",
                "setTouchAFPosition",
                JSONArray().put(Xpos).put(Ypos),
                "1.0",
                -1
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getTouchAFPosition(): JSONObject
    {
        try {
            return communicateJSON("camera", "getTouchAFPosition", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun cancelTouchAFPosition(): JSONObject
    {
        try {
            return communicateJSON("camera", "cancelTouchAFPosition", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun actHalfPressShutter(): JSONObject
    {
        try {
            return communicateJSON("camera", "actHalfPressShutter", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun cancelHalfPressShutter(): JSONObject
    {
        try {
            return communicateJSON("camera", "cancelHalfPressShutter", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun setFocusMode(focusMode: String?): JSONObject
    {
        try {
            Log.v(TAG, "setFocusMode ($focusMode)")
            return communicateJSON("camera", "setFocusMode", JSONArray().put(focusMode), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getFocusMode(): JSONObject
    {
        try {
            return communicateJSON("camera", "getFocusMode", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getSupportedFocusMode(): JSONObject
    {
        try {
            return communicateJSON("camera", "getSupportedFocusMode", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getAvailableFocusMode(): JSONObject
    {
        try {
            return communicateJSON("camera", "getAvailableFocusMode", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun startLiveview(): JSONObject
    {
        try {
            return communicateJSON("camera", "startLiveview", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun stopLiveview(): JSONObject
    {
        try {
            return communicateJSON("camera", "stopLiveview", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun startRecMode(): JSONObject
    {
        try {
            return communicateJSON("camera", "startRecMode", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun actTakePicture(): JSONObject
    {
        try {
            return communicateJSON("camera", "actTakePicture", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun awaitTakePicture(): JSONObject
    {
        try {
            return communicateJSON("camera", "awaitTakePicture", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun startContShooting(): JSONObject
    {
        try
        {
            return communicateJSON("camera", "startContShooting", JSONArray(), "1.0", -1)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun stopContShooting(): JSONObject
    {
        try
        {
            return communicateJSON("camera", "stopContShooting", JSONArray(), "1.0", -1)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun startMovieRec(): JSONObject
    {
        try {
            return communicateJSON("camera", "startMovieRec", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun stopMovieRec(): JSONObject
    {
        try {
            return communicateJSON("camera", "stopMovieRec", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun actZoom(direction: String, movement: String): JSONObject
    {
        try {
            return communicateJSON(
                "camera",
                "actZoom",
                JSONArray().put(direction).put(movement),
                "1.0",
                -1
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getEvent(version: String, longPollingFlag: Boolean): JSONObject
    {
        try {
            val longPollingTimeout = if (longPollingFlag) 20000 else 8000 // msec
            return communicateJSON(
                "camera",
                "getEvent",
                JSONArray().put(longPollingFlag),
                version,
                longPollingTimeout
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun setCameraFunction(cameraFunction: String): JSONObject
    {
        try {
            return communicateJSON(
                "camera",
                "setCameraFunction",
                JSONArray().put(cameraFunction),
                "1.0",
                -1
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getCameraMethodTypes(): JSONObject
    {
        try {
            return communicateJSON("camera", "getCameraMethodTypes", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getAvcontentMethodTypes(): JSONObject
    {
        try {
            return communicateJSON("avContent", "getMethodTypes", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getStorageInformation(): JSONObject
    {
        try {
            return communicateJSON("camera", "getStorageInformation", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getSchemeList(): JSONObject
    {
        try {
            return communicateJSON("avContent", "getSchemeList", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getSourceList(scheme: String?): JSONObject
    {
        try {
            val params = JSONObject().put("scheme", scheme)
            return communicateJSON(
                "avContent",
                "getSourceList",
                JSONArray().put(0, params),
                "1.0",
                -1
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getContentCountFlatAll(uri: String?): JSONObject
    {
        try {
            val params = JSONObject()
            params.put("uri", uri)
            params.put("target", "all")
            params.put("view", "flat")
            return communicateJSON(
                "avContent",
                "getContentCount",
                JSONArray().put(0, params),
                "1.2",
                -1
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getContentList(params: JSONArray?): JSONObject
    {
        try {
            return communicateJSON("avContent", "getContentList", params!!, "1.3", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun setStreamingContent(uri: String?): JSONObject
    {
        try {
            val params = JSONObject().put("remotePlayType", "simpleStreaming").put("uri", uri)
            return communicateJSON(
                "avContent",
                "setStreamingContent",
                JSONArray().put(0, params),
                "1.0",
                -1
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun startStreaming(): JSONObject
    {
        try {
            return communicateJSON("avContent", "startStreaming", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun stopStreaming(): JSONObject
    {
        try {
            return communicateJSON("avContent", "stopStreaming", JSONArray(), "1.0", -1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun getSonyApiServiceList(): List<String?>? {
        try {
            val serviceList: MutableList<String?> = ArrayList()
            val services = sonyCamera.getApiServices()
            for (apiService in services) {
                serviceList.add(apiService.getName())
            }
            return serviceList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun callGenericSonyApiMethod(service: String, method: String, params: JSONArray, version: String, timeoutMs: Int): JSONObject
    {
        return communicateJSON(service, method, params, version, timeoutMs)
    }

    override fun getDdUrl(): String
    {
        return sonyCamera.getDdUrl()
    }

    override fun actEnableMethods(
        developerName: String?,
        developerID: String?,
        sg: String?,
        methods: String?
    ): JSONObject
    {
        try {
            val params =
                JSONObject().put("developerName", developerName).put("developerID", developerID)
                    .put("sg", sg).put("methods", methods)
            return communicateJSON(
                "accessControl",
                "actEnableMethods",
                JSONArray().put(0, params),
                "1.0",
                -1
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return JSONObject()
    }

    override fun isErrorReply(replyJson: JSONObject?): Boolean
    {
        return replyJson != null && replyJson.has("error")
    }

    companion object
    {
        private val TAG = SonyCameraApi::class.java.simpleName
        private const val FULL_LOG = false
    }
}
