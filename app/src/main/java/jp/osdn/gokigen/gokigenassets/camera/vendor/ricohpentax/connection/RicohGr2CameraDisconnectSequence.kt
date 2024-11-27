package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.connection

import android.app.Activity
import android.util.Log
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_RICOH_GR2_LCD_SLEEP
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient


class RicohGr2CameraDisconnectSequence(private val activity: Activity, private val powerOff: Boolean, private val executeUrl : String = "http://192.168.0.1") : Runnable
{
    companion object
    {
        private val TAG = RicohGr2CameraDisconnectSequence::class.java.simpleName
        private const val TIMEOUT_MS = 5000
    }

    override fun run()
    {
        // カメラをPowerOffして接続を切る
        try
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val httpClient = SimpleHttpClient()
            if (preferences.getBoolean(ID_PREFERENCE_RICOH_GR2_LCD_SLEEP, false))
            {
                val screenOnUrl = "$executeUrl/_gr"
                val postData = "lcd sleep off"
                val response: String? = httpClient.httpPost(screenOnUrl, postData, TIMEOUT_MS)
                Log.v(TAG, "$screenOnUrl $response")
            }
            if (powerOff)
            {
                val cameraPowerOffUrl = "$executeUrl/v1/device/finish"
                val postData = ""
                val response: String? = httpClient.httpPost(cameraPowerOffUrl, postData, TIMEOUT_MS)
                Log.v(TAG, "$cameraPowerOffUrl $response")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
