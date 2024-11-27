package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.connection

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnection
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status.IThetaSessionIdNotifier
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CAMERA_CONNECT_RESPONSE_NG
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CAMERA_NOT_FOUND
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CONNECT_CONNECTED
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import org.json.JSONObject

class ThetaCameraConnectSequence(private val context: AppCompatActivity, private val cameraStatusReceiver: ICameraStatusReceiver, private val sessionIdNotifier: IThetaSessionIdNotifier, private val cameraConnection : ICameraConnection, private val executeUrl : String = "http://192.168.1.1") : Runnable
{
    private var useThetaV21: Boolean = false
    private val httpClient = SimpleHttpClient()

    override fun run()
    {
        // 使用する API Levelを決める
        useThetaV21 = decideApiLevel()
        try
        {
            Log.v(TAG, "Theta API v2.1 : $useThetaV21")
            if (useThetaV21)
            {
                // API Level V2.1を使用して通信する
                connectApiV21()
            }
            else
            {
                // API Level V2 を使用して通信する
                connectApiV2()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     *
     */
    private fun decideApiLevel(): Boolean
    {
        var apiLevelIsV21 = false
        try
        {
            val oscInfoUrl = "$executeUrl/osc/info"
            val timeoutMs = 3000
            val response: String = httpClient.httpGet(oscInfoUrl, timeoutMs)
            Log.v(TAG, " $oscInfoUrl $response")
            if (response.isNotEmpty())
            {
                val apiLevelArray = JSONObject(response).getJSONArray("apiLevel")
                val size = apiLevelArray.length()
                for (index in 0 until size)
                {
                    val api = apiLevelArray.getInt(index)
                    if (api == 1)  //if (api == 1 && useThetaV21)
                    {
                        apiLevelIsV21 = false
                        break
                    }
                    if (api == 2)  //if (api == 2 && useThetaV21)
                    {
                        apiLevelIsV21 = true
                        break
                    }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (apiLevelIsV21)
    }

    /**
     *
     */
    private fun connectApiV2()
    {
        val commandsExecuteUrl = "$executeUrl/osc/commands/execute"
        val startSessionData = "{\"name\":\"camera.startSession\",\"parameters\":{\"timeout\":0}}"
        val getStateUrl = "$executeUrl/osc/state"
        val timeoutMs = 2000
        try
        {
            val response: String? = httpClient.httpPost(commandsExecuteUrl, startSessionData, timeoutMs)
            Log.v(TAG, " $commandsExecuteUrl $startSessionData $response")
            val response2: String? = httpClient.httpPost(getStateUrl, "", timeoutMs)
            Log.v(TAG, " $getStateUrl $response2")
            if ((response2 != null) && (response2.isNotEmpty()))
            {
                try
                {
                    val jsonObject = JSONObject(response2)
                    val sessionId = jsonObject.getJSONObject("state").getString("sessionId")
                    sessionIdNotifier.receivedSessionId(sessionId)
                    onConnectNotify()
                    return
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            // 応答なし、を応答する。
            cameraConnection.alertConnectingFailed(context.getString(ID_STRING_CAMERA_CONNECT_RESPONSE_NG))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            cameraConnection.alertConnectingFailed(e.localizedMessage)
        }
    }

    private fun connectApiV21()
    {
        val commandsExecuteUrl = "$executeUrl/osc/commands/execute"
        val startSessionData = "{\"name\":\"camera.startSession\",\"parameters\":{\"timeout\":0}}"
        val getStateUrl = "$executeUrl/osc/state"
        val timeoutMs = 3000
        try
        {
            val responseS: String? = httpClient.httpPostWithHeader(
                commandsExecuteUrl,
                startSessionData,
                null,
                "application/json;charset=utf-8",
                timeoutMs
            )
            Log.v(TAG, " [ $commandsExecuteUrl ] $startSessionData ::: $responseS")
            val response: String? =
                httpClient.httpPostWithHeader(getStateUrl, "", null, null, timeoutMs)
            Log.v(TAG, " ($getStateUrl) $response")
            if ((response != null) && (response.isNotEmpty()))
            {
                var apiLevel = 1
                var sessionId: String? = null
                val jsonObject = JSONObject(response)
                try
                {
                    apiLevel = jsonObject.getJSONObject("state").getInt("_apiVersion")
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                if (apiLevel == 1)
                {
                    try
                    {
                        sessionId = jsonObject.getJSONObject("state").getString("sessionId")
                        sessionIdNotifier.receivedSessionId(sessionId)
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                }
                if (apiLevel != 2)
                {
                    // API Levelを 1 から 2 に変える
                    val setApiLevelData = "{\"name\":\"camera.setOptions\",\"parameters\":{\"sessionId\" : \"$sessionId\", \"options\":{ \"clientVersion\":2}}}"
                    val response3: String? = httpClient.httpPostWithHeader(
                        commandsExecuteUrl,
                        setApiLevelData,
                        null,
                        "application/json;charset=utf-8",
                        timeoutMs
                    )
                    Log.v(TAG, " $commandsExecuteUrl $setApiLevelData $response3")
                }
                onConnectNotify()
            }
            else
            {
                cameraConnection.alertConnectingFailed(context.getString(ID_STRING_CAMERA_NOT_FOUND))
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            cameraConnection.alertConnectingFailed(e.localizedMessage)
        }
    }

    private fun onConnectNotify()
    {
        try
        {
            val thread = Thread { // カメラとの接続確立を通知する
                cameraStatusReceiver.onStatusNotify(context.getString(ID_STRING_CONNECT_CONNECTED))
                cameraStatusReceiver.onCameraConnected()
                Log.v(TAG, "onConnectNotify()")
                cameraConnection.forceUpdateConnectionStatus(ICameraConnectionStatus.CameraConnectionStatus.CONNECTED)
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = ThetaCameraConnectSequence::class.java.simpleName
    }
}
