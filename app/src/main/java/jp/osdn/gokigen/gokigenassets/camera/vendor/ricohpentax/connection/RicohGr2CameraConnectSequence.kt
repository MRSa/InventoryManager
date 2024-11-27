package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.connection

import android.app.Activity
import android.util.Log
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnection
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_RICOH_GR2_LCD_SLEEP
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_USE_GR2_SPECIAL_COMMAND
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CAMERA_NOT_FOUND
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CONNECT_CONNECTED
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient

class RicohGr2CameraConnectSequence(private val context: Activity, private val cameraStatusReceiver: ICameraStatusReceiver, private val cameraConnection: ICameraConnection, private val gr2CommandNotify: IUseGR2CommandNotify, private val executeUrl : String = "http://192.168.0.1") : Runnable
{

    companion object
    {
        private val TAG = RicohGr2CameraConnectSequence::class.java.simpleName
        private const val TIMEOUT_MS = 5000
    }
    private val httpClient = SimpleHttpClient()

    override fun run() {
        val areYouThereUrl = "$executeUrl/v1/ping"
        val grCommandUrl = "$executeUrl/_gr"
        try {
            val response: String = httpClient.httpGet(areYouThereUrl, TIMEOUT_MS)
            Log.v(TAG, "$areYouThereUrl $response")
            if (response.isNotEmpty())
            {
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)

                // 接続時、レンズロックOFF + GR2 コマンド有効/無効の確認
                run {
                    val postData = "cmd=acclock off"
                    val response0: String? = httpClient.httpPost(grCommandUrl, postData, TIMEOUT_MS)
                    Log.v(TAG, "$grCommandUrl $response0")


                    // GR2 専用コマンドを受け付けられるかどうかで、Preference を書き換える
                    val enableGr2Command = !response0.isNullOrBlank()
                    try
                    {
                        val editor = preferences.edit()
                        editor.putBoolean(ID_PREFERENCE_USE_GR2_SPECIAL_COMMAND, enableGr2Command)
                        editor.apply()
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                    val gr2LcdSleep = preferences.getBoolean(ID_PREFERENCE_RICOH_GR2_LCD_SLEEP, false)
                    gr2CommandNotify.setUseGR2Command(enableGr2Command, gr2LcdSleep)

                    // 接続時、カメラの画面を消す
                    if ((enableGr2Command)&&(gr2LcdSleep))
                    {
                        val postData0 = "cmd=lcd sleep on"
                        val response1: String? = httpClient.httpPost(grCommandUrl, postData0, TIMEOUT_MS)
                        Log.v(TAG, "$grCommandUrl $response1")
                    }
                }
                onConnectNotify()
            } else {
                onConnectError(context.getString(ID_STRING_CAMERA_NOT_FOUND))
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            onConnectError(e.localizedMessage)
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

    private fun onConnectError(reason: String?)
    {
        cameraConnection.alertConnectingFailed(reason)
    }
}
