package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.connection

import android.content.*
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CONNECT_CHECK_WIFI
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_DIALOG_BUTTON_NETWORK_SETTINGS
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_DIALOG_BUTTON_RETRY
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_DIALOG_TITLE_CONNECT_FAILED
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 *
 *
 */
class RicohGr2Connection(private val context: FragmentActivity, private val statusReceiver: ICameraStatusReceiver, private val liveViewControl: ILiveViewController, private val gr2CommandNotify: IUseGR2CommandNotify) : ICameraConnection
{
    companion object
    {
        private val TAG = RicohGr2Connection::class.java.simpleName
    }
    private val connectionReceiver: BroadcastReceiver

    //private final ConnectivityManager connectivityManager;
    private val cameraExecutor: Executor = Executors.newFixedThreadPool(1)

    //private final Handler networkConnectionTimeoutHandler;
    //private static final int MESSAGE_CONNECTIVITY_TIMEOUT = 1;
    private var connectionStatus: ICameraConnectionStatus.CameraConnectionStatus = ICameraConnectionStatus.CameraConnectionStatus.UNKNOWN

    /**
     *
     *
     */
    private fun onReceiveBroadcastOfConnection(context: Context, intent: Intent) {
        statusReceiver.onStatusNotify(context.getString(ID_STRING_CONNECT_CHECK_WIFI))
        Log.v(TAG, context.getString(ID_STRING_CONNECT_CHECK_WIFI))
        try
        {
            val action = intent.action
            if (action == null)
            {
                Log.v(TAG, "intent.getAction() : null")
                return
            }

            @Suppress("DEPRECATION")
            if (action == ConnectivityManager.CONNECTIVITY_ACTION)
            {
                Log.v(TAG, "onReceiveBroadcastOfConnection() : CONNECTIVITY_ACTION")
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val info = wifiManager.connectionInfo
                if (wifiManager.isWifiEnabled && info != null)
                {
                    Log.v(TAG, "Network ID is " + info.networkId)
                    // 自動接続が指示されていた場合は、カメラとの接続処理を行う
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

    /**
     *
     *
     */
    fun startWatchWifiStatus(context: Context) {
        Log.v(TAG, "startWatchWifiStatus()")
        statusReceiver.onStatusNotify("prepare")
        val filter = IntentFilter()
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        @Suppress("DEPRECATION")
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(connectionReceiver, filter)
    }

    /**
     *
     *
     */
    fun stopWatchWifiStatus(context: Context) {
        Log.v(TAG, "stopWatchWifiStatus()")
        context.unregisterReceiver(connectionReceiver)
        disconnect(false)
    }

    /**
     *
     *
     */
    fun disconnect(powerOff: Boolean) {
        Log.v(TAG, "disconnect()")
        disconnectFromCamera(powerOff)
        connectionStatus = ICameraConnectionStatus.CameraConnectionStatus.DISCONNECTED
        statusReceiver.onCameraDisconnected()
    }

    /**
     *
     *
     */
    fun connect() {
        Log.v(TAG, "connect()")
        connectToCamera()
    }

    /**
     *
     *
     */
    override fun alertConnectingFailed(message: String?)
    {
        Log.v(TAG, "alertConnectingFailed() : $message")
        try
        {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                .setTitle(context.getString(ID_STRING_DIALOG_TITLE_CONNECT_FAILED))
                .setMessage(message)
                .setPositiveButton(
                    context.getString(ID_STRING_DIALOG_BUTTON_RETRY)
                ) { _, _ -> connect() }
                .setNeutralButton(ID_STRING_DIALOG_BUTTON_NETWORK_SETTINGS
                ) { _, _ ->
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
            context.runOnUiThread { builder.show() }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    fun getConnectionStatus(): ICameraConnectionStatus.CameraConnectionStatus {
        Log.v(TAG, "getConnectionStatus()")
        return connectionStatus
    }

    /**
     *
     *
     */
    override fun forceUpdateConnectionStatus(status: ICameraConnectionStatus.CameraConnectionStatus)
    {
        Log.v(TAG, "forceUpdateConnectionStatus()")
        connectionStatus = status
        if (status == ICameraConnectionStatus.CameraConnectionStatus.CONNECTED)
        {
            liveViewControl.startLiveView()
        }
        else if (status == ICameraConnectionStatus.CameraConnectionStatus.DISCONNECTED)
        {
            liveViewControl.stopLiveView()
        }


    }

    /**
     * カメラとの切断処理
     */
    private fun disconnectFromCamera(powerOff: Boolean) {
        Log.v(TAG, "disconnectFromCamera()")
        try {
            cameraExecutor.execute(RicohGr2CameraDisconnectSequence(context, powerOff))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * カメラとの接続処理
     */
    private fun connectToCamera() {
        Log.v(TAG, "connectToCamera()")
        connectionStatus = ICameraConnectionStatus.CameraConnectionStatus.CONNECTING
        try {
            cameraExecutor.execute(
                RicohGr2CameraConnectSequence(
                    context,
                    statusReceiver,
                    this,
                    gr2CommandNotify
                )
            )
        } catch (e: Exception) {
            Log.v(TAG, "connectToCamera() EXCEPTION : " + e.message)
            e.printStackTrace()
        }
    }

    init {
        Log.v(TAG, " RicohGr2Connection()")
        connectionReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent)
            {
                onReceiveBroadcastOfConnection(context, intent)
            }
        }
    }
}
