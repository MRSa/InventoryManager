package jp.osdn.gokigen.gokigenassets.camera.vendor.omds.connection

import android.content.*
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.IOmdsProtocolNotify
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.liveview.IOmdsLiveViewControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.status.IOmdsCommunicationInfo
import jp.osdn.gokigen.constants.IStringResourceConstantConvert
import java.lang.Exception
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class OmdsCameraConnection(private val context: AppCompatActivity, private val statusReceiver: ICameraStatusReceiver, private val communicationInfo: IOmdsCommunicationInfo, private val liveViewControl: IOmdsLiveViewControl, private val useOpcProtocolNotify: IOmdsProtocolNotify, private val liveViewQuality : String = "0640x0480", private val userAgent : String = "OlympusCameraKit", private val executeUrl : String = "http://192.168.0.10") : ICameraConnection, ICameraConnectionStatus
{
    private val cameraExecutor: Executor = Executors.newFixedThreadPool(1)
    private var connectionStatus: ICameraConnectionStatus.CameraConnectionStatus = ICameraConnectionStatus.CameraConnectionStatus.UNKNOWN

    private val connectionReceiver: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            onReceiveBroadcastOfConnection(context, intent)
        }
    }

    private fun onReceiveBroadcastOfConnection(context: Context, intent: Intent)
    {
        statusReceiver.onStatusNotify(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CHECK_WIFI))
        Log.v(TAG, context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CHECK_WIFI))
        val action = intent.action
        if (action == null)
        {
            Log.v(TAG, "intent.getAction() : null")
            return
        }
        try
        {
            @Suppress("DEPRECATION")
            if (action == ConnectivityManager.CONNECTIVITY_ACTION)
            {
                Log.v(TAG, "onReceiveBroadcastOfConnection() : CONNECTIVITY_ACTION")
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val info = wifiManager.connectionInfo
                if ((wifiManager.isWifiEnabled)&&(info != null))
                {
                    if (info.networkId != -1)
                    {
                        Log.v(TAG, "Network ID is -1, there is no currently connected network.")
                    }
                    // カメラと接続
                    connectToCamera()
                }
                else
                {
                    if (info == null)
                    {
                        Log.v(TAG, "NETWORK INFO IS NULL.")
                    }
                    else
                    {
                        Log.v(TAG, "isWifiEnabled : " + wifiManager.isWifiEnabled + " NetworkId : " + info.networkId)
                    }
                }
            }
        }
        catch (e: Exception)
        {
            Log.w(TAG, "onReceiveBroadcastOfConnection() EXCEPTION" + e.message)
            e.printStackTrace()
        }
    }

    fun startWatchWifiStatus(context: Context)
    {
        Log.v(TAG, "startWatchWifiStatus()")
        try
        {
            statusReceiver.onStatusNotify("prepare")
            val filter = IntentFilter()
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            @Suppress("DEPRECATION")
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            context.registerReceiver(connectionReceiver, filter)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    fun stopWatchWifiStatus(context: Context)
    {
        Log.v(TAG, "stopWatchWifiStatus()")
        context.unregisterReceiver(connectionReceiver)
        disconnect(false)
    }

    fun disconnect(powerOff: Boolean)
    {
        Log.v(TAG, "disconnect()")
        disconnectFromCamera(powerOff)
        connectionStatus = ICameraConnectionStatus.CameraConnectionStatus.DISCONNECTED
        statusReceiver.onCameraDisconnected()
    }

    fun connect()
    {
        Log.v(TAG, "connect()")
        connectToCamera()
    }

    override fun alertConnectingFailed(message: String?)
    {
        Log.v(TAG, "alertConnectingFailed() : $message")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            .setTitle(context.getString(IStringResourceConstantConvert.ID_STRING_DIALOG_TITLE_CONNECT_FAILED))
            .setMessage(message)
            .setPositiveButton(context.getString(IStringResourceConstantConvert.ID_STRING_DIALOG_BUTTON_RETRY)) { _, _ -> connect() }
            .setNeutralButton(IStringResourceConstantConvert.ID_STRING_DIALOG_BUTTON_NETWORK_SETTINGS) { _, _ ->
                try {
                    // Wifi 設定画面を表示する
                    context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                } catch (ex: ActivityNotFoundException) {
                    // Activity が存在しなかった...設定画面が起動できなかった
                    Log.v(TAG, "android.content.ActivityNotFoundException...")

                    // この場合は、再試行と等価な動きとする
                    connect()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        context.runOnUiThread {
            try
            {
                builder.show()
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }

    override fun getConnectionStatus(): ICameraConnectionStatus.CameraConnectionStatus
    {
        Log.v(TAG, "getConnectionStatus()")
        return connectionStatus
    }

    override fun forceUpdateConnectionStatus(status: ICameraConnectionStatus.CameraConnectionStatus)
    {
        Log.v(TAG, "forceUpdateConnectionStatus()")
        connectionStatus = status

        when (status)
        {
            ICameraConnectionStatus.CameraConnectionStatus.CONNECTED -> { liveViewControl.startOmdsLiveView() }
            ICameraConnectionStatus.CameraConnectionStatus.DISCONNECTED -> { liveViewControl.stopOmdsLiveView() }
            else -> { }
        }
    }

    /**
     * カメラとの切断処理
     */
    private fun disconnectFromCamera(powerOff: Boolean)
    {
        Log.v(TAG, "disconnectFromCamera()")
        try
        {
            cameraExecutor.execute(OmdsCameraDisconnectSequence(context, powerOff, userAgent, executeUrl))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * カメラとの接続処理
     */
    private fun connectToCamera()
    {
        Log.v(TAG, "connectToCamera()")
        connectionStatus = ICameraConnectionStatus.CameraConnectionStatus.CONNECTING
        try
        {
            cameraExecutor.execute(OmdsCameraConnectSequence(context, statusReceiver, this, communicationInfo, useOpcProtocolNotify, liveViewQuality, userAgent, executeUrl))
        }
        catch (e: Exception)
        {
            Log.v(TAG, "connectToCamera() EXCEPTION : " + e.message)
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = OmdsCameraConnection::class.java.simpleName
    }
}
