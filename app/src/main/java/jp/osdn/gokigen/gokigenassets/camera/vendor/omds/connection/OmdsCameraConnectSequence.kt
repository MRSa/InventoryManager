package jp.osdn.gokigen.gokigenassets.camera.vendor.omds.connection

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnection
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.IOmdsProtocolNotify
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.status.IOmdsCommunicationInfo
import jp.osdn.gokigen.constants.IStringResourceConstantConvert
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import java.lang.Exception
import java.util.HashMap

class OmdsCameraConnectSequence(private val context: AppCompatActivity, private val cameraStatusReceiver: ICameraStatusReceiver, private val cameraConnection : ICameraConnection, private val communicationInfo: IOmdsCommunicationInfo, private val useOpcProtocolNotify: IOmdsProtocolNotify, private val liveViewQuality : String, userAgent : String = "OlympusCameraKit", private val executeUrl : String = "http://192.168.0.10") : Runnable
{
    private val headerMap: MutableMap<String, String> = HashMap()
    private val http = SimpleHttpClient()

    override fun run()
    {
        try
        {
            val camInfoUrl = "$executeUrl/get_caminfo.cgi"
            val getCommandListUrl = "$executeUrl/get_commandlist.cgi"
            val getConnectModeUrl = "$executeUrl/get_connectmode.cgi"
            val switchCameraModeUrl = "$executeUrl/switch_cammode.cgi"
            val switchOpcCameraModeUrl = "$executeUrl/switch_cameramode.cgi"

            val response: String = http.httpGetWithHeader(getConnectModeUrl, headerMap, null, TIMEOUT_MS) ?: ""
            Log.v(TAG, " $getConnectModeUrl $response")
            if (response.isNotEmpty())
            {
                val response2: String = http.httpGetWithHeader(getCommandListUrl, headerMap, null, TIMEOUT_MS) ?: ""
                Log.v(TAG, " $getCommandListUrl (${response2.length})")
                communicationInfo.setOmdsCommandList(response2)

                val response3: String = http.httpGetWithHeader(camInfoUrl, headerMap, null, TIMEOUT_MS) ?: ""
                Log.v(TAG, " $camInfoUrl $response3")

                // 撮影モードに切り替え。
                val lvUrl = "$switchCameraModeUrl?mode=rec&lvqty=$liveViewQuality"
                val response4: String = http.httpGetWithHeader(lvUrl, headerMap, null, TIMEOUT_MS) ?: ""
                Log.v(TAG, " $lvUrl $response4")
                if (response4.isEmpty())
                {
                    // エラーになった場合は、OPCのコマンドを発行する
                    val response5: String = http.httpGetWithHeader("$switchOpcCameraModeUrl?mode=rec&lvqty=$liveViewQuality", headerMap, null, TIMEOUT_MS) ?: ""
                    Log.v(TAG, " $switchOpcCameraModeUrl?mode=rec $response5")
                    if (response5.length > 5)
                    {
                        Log.v(TAG, " -=-=-=-=-=- DETECTED OPC CAMERA -=-=-=-=-=-")
                        useOpcProtocolNotify.detectedOpcProtocol(true)

                        communicationInfo.startReceiveOpcEvent()
                    }
                }

                ////////////////  for TEST   ////////////////
                if (checkStatusDump)
                {
                    //val testUrl = "$executeUrl/get_proplist.cgi"  // プロパティ一覧 (OPC)
                    val testUrl = "$executeUrl/get_camprop.cgi?com=desc&propname=desclist"  // コマンド一覧
                    val testResponse: String = http.httpGetWithHeader(testUrl, headerMap, null, TIMEOUT_MS) ?: ""
                    Log.v(TAG, "     ------------------------------------------ ")
                    for (pos in 0..testResponse.length step 768)
                    {
                        val lastIndex = if ((pos + 768) > testResponse.length) { testResponse.length } else { pos + 768 }
                        Log.v(TAG, " $testUrl ($pos/${testResponse.length}) ${testResponse.substring(pos, lastIndex)}")
                    }
                    Log.v(TAG, "     ------------------------------------------ ")

                    //// カメラのステータス取得
                    val getCameraStatusUrl = "$executeUrl/get_activate.cgi"
                    val response5 : String = http.httpGetWithHeader(getCameraStatusUrl, headerMap, null, TIMEOUT_MS) ?: ""
                    Log.v(TAG, " $getCameraStatusUrl $response5")
                }
                ////////////////  for TEST   ////////////////

                onConnectNotify()
            }
            else
            {
                cameraConnection.alertConnectingFailed(context.getString(
                    IStringResourceConstantConvert.ID_STRING_CAMERA_NOT_FOUND))
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
                cameraStatusReceiver.onStatusNotify(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTED))
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

    init
    {
        headerMap["User-Agent"] = userAgent // "OlympusCameraKit" // "OI.Share"
        headerMap["X-Protocol"] = userAgent // "OlympusCameraKit" // "OI.Share"
    }

    companion object
    {
        private val TAG = OmdsCameraConnectSequence::class.java.simpleName
        private const val TIMEOUT_MS = 5000
        private const val checkStatusDump = false
    }
}
