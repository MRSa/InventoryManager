package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.connection

import android.content.*
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnection
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ILiveViewController
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.operation.IOperationCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.operation.ThetaOptionSetControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status.IThetaSessionIdNotifier
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status.IThetaSessionIdProvider
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_THETA_LIVEVIEW_RESOLUTION
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_THETA_LIVEVIEW_RESOLUTION_DEFAULT_VALUE
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CONNECT_CHECK_WIFI
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_DIALOG_BUTTON_NETWORK_SETTINGS
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_DIALOG_BUTTON_RETRY
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_DIALOG_TITLE_CONNECT_FAILED
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 *
 */
class ThetaCameraConnection(private val context: AppCompatActivity, private val statusReceiver: ICameraStatusReceiver, private val sessionIdNotifier: IThetaSessionIdNotifier, private val sessionIdProvider: IThetaSessionIdProvider, private val liveViewControl : ILiveViewController) : ICameraConnection, ICameraConnectionStatus
{
    private val cameraExecutor: Executor = Executors.newFixedThreadPool(1)
    private var connectionStatus: ICameraConnectionStatus.CameraConnectionStatus = ICameraConnectionStatus.CameraConnectionStatus.UNKNOWN
    private var connectionReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            onReceiveBroadcastOfConnection(context, intent)
        }
    }

    companion object
    {
        private val TAG = ThetaCameraConnection::class.java.simpleName
    }

    /**
     *
     *
     */
    private fun onReceiveBroadcastOfConnection(context: Context, intent: Intent)
    {
        statusReceiver.onStatusNotify(context.getString(ID_STRING_CONNECT_CHECK_WIFI))
        Log.v(TAG, context.getString(ID_STRING_CONNECT_CHECK_WIFI))
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

    /**
     *
     *
     */
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

    /**
     *
     *
     */
    fun stopWatchWifiStatus(context: Context)
    {
        Log.v(TAG, "stopWatchWifiStatus()")
        try
        {
            context.unregisterReceiver(connectionReceiver)
            disconnect(false)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     *
     *
     */
    fun disconnect(powerOff: Boolean)
    {
        Log.v(TAG, "disconnect()")
        disconnectFromCamera(powerOff)
        connectionStatus = ICameraConnectionStatus.CameraConnectionStatus.DISCONNECTED
        statusReceiver.onCameraDisconnected()
        liveViewControl.stopLiveView()
    }

    /**
     *
     *
     */
    fun connect()
    {
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
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            .setTitle(context.getString(ID_STRING_DIALOG_TITLE_CONNECT_FAILED))
            .setMessage(message)
            .setPositiveButton(context.getString(ID_STRING_DIALOG_BUTTON_RETRY)) { _, _ -> connect() }
            .setNeutralButton(ID_STRING_DIALOG_BUTTON_NETWORK_SETTINGS) { _, _ ->
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

    /**
     *
     *
     */
    override fun getConnectionStatus(): ICameraConnectionStatus.CameraConnectionStatus
    {
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
            startLiveView()
            //liveViewControl.startLiveView()
        }
        else if (status == ICameraConnectionStatus.CameraConnectionStatus.DISCONNECTED)
        {
            liveViewControl.stopLiveView()
        }
    }

    /**
     *
     *
     */
    private fun startLiveView()
    {
        try
        {
/*
            val optionGet = ThetaOptionGetControl(sessionIdProvider)
            optionGet.getOptions("[\"previewFormat\", \"previewFormatSupport\"]", (sessionIdProvider.sessionId.isBlank()),
                    object : IOperationCallback { override fun operationExecuted(result: Int, resultStr: String?)
                    {
                        Log.v(TAG, " >>>>> optionGet.getOptions : $resultStr ")
                    }})
*/
            val optionSet = ThetaOptionSetControl(sessionIdProvider)
            optionSet.setOptions("\"captureMode\" : \"image\"", (sessionIdProvider.sessionId.isBlank()),
                    object : IOperationCallback { override fun operationExecuted(result: Int, resultStr: String?)
                    {
                        val previewFormat = PreferenceManager.getDefaultSharedPreferences(context).getString(ID_PREFERENCE_THETA_LIVEVIEW_RESOLUTION, ID_PREFERENCE_THETA_LIVEVIEW_RESOLUTION_DEFAULT_VALUE)
                        //val previewFormat= "{\"width\": 640, \"height\": 320, \"framerate\": 30}"
                        Log.v(TAG, " optionSet.setOptions(live view) : $resultStr : $previewFormat")
                        optionSet.setOptions("\"previewFormat\" : $previewFormat", (sessionIdProvider.sessionId.isBlank()),
                                object : IOperationCallback { override fun operationExecuted(result: Int, resultStr: String?)
                                {
                                    Log.v(TAG, " optionSet.setOptions(live view) : $resultStr ($previewFormat)")
                                    liveViewControl.startLiveView()
                                }})
                    }})
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }


    /**
     * カメラとの切断処理
     */
    private fun disconnectFromCamera(powerOff: Boolean)
    {
        Log.v(TAG, "disconnectFromCamera() : $powerOff")
        try
        {
            cameraExecutor.execute(ThetaCameraDisconnectSequence())
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
        Log.v(TAG, " connectToCamera()")
        connectionStatus = ICameraConnectionStatus.CameraConnectionStatus.CONNECTING
        try
        {
            cameraExecutor.execute(ThetaCameraConnectSequence(context, statusReceiver, sessionIdNotifier, this))
        }
        catch (e: Exception)
        {
            Log.v(TAG, "connectToCamera() EXCEPTION : " + e.message)
            e.printStackTrace()
        }
    }
}
