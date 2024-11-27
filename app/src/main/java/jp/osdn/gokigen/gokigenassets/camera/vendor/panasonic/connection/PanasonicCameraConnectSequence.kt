package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.connection

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnection
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraChangeListener
import jp.osdn.gokigen.gokigenassets.camera.vendor.ICameraControlCoordinator
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCameraHolder
import jp.osdn.gokigen.constants.IStringResourceConstantConvert
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CONNECT_CAMERA_DETECTED
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CONNECT_START

class PanasonicCameraConnectSequence(private val context: AppCompatActivity, private val cameraStatusReceiver: ICameraStatusReceiver, private val cameraConnection: ICameraConnection, private val cameraHolder: IPanasonicCameraHolder, private val listener: ICameraChangeListener, cameraCoordinator: ICameraControlCoordinator, number : Int) : Runnable, PanasonicSsdpClient.ISearchResultCallback
{
    companion object
    {
        private val TAG = PanasonicCameraConnectSequence::class.java.simpleName
    }
    private val client = PanasonicSsdpClient(context, this, cameraStatusReceiver, cameraCoordinator, number, 1)

    override fun run()
    {
        Log.v(TAG, "search()")
        try
        {
            cameraStatusReceiver.onStatusNotify(context.getString(ID_STRING_CONNECT_START))
            client.search()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
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

    /* ISearchResultCallback */
    override fun onDeviceFound(cameraDevice: IPanasonicCamera)
    {
        try
        {
            cameraStatusReceiver.onStatusNotify(context.getString(ID_STRING_CONNECT_CAMERA_DETECTED) + " " + cameraDevice.getFriendlyName())
            cameraHolder.detectedCamera(cameraDevice)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /* ISearchResultCallback */
    override fun onFinished()
    {
        Log.v(TAG, "PanasonicCameraConnectSequence.onFinished()")
        try
        {
            val thread = Thread {
                try
                {
                    cameraHolder.prepare()
                    cameraHolder.startRecMode()
                    cameraHolder.startEventWatch(listener)
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                Log.v(TAG, "CameraConnectSequence:: connected.")
                onConnectNotify()
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /* ISearchResultCallback */
    override fun onErrorFinished(reason: String?)
    {
        cameraConnection.alertConnectingFailed(reason)
    }
}
