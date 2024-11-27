package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.connection

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCamera
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCameraHolder
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnection
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.camera.vendor.ICameraControlCoordinator
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CONNECT_CAMERA_FOUND
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CONNECT_CONNECTED
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_CONNECT_START
import java.lang.Exception

class SonyCameraConnectSequence(private val context: AppCompatActivity,
                                private val cameraStatusReceiver: ICameraStatusReceiver,
                                private val cameraConnection: ICameraConnection?,
                                private val cameraHolder: ISonyCameraHolder,
                                cameraCoordinator: ICameraControlCoordinator,
                                number : Int) : Runnable, SonySsdpClient.ISearchResultCallback
{
    private var client = SonySsdpClient(context, this, cameraStatusReceiver,cameraCoordinator, number, 1)

    companion object
    {
        private val TAG = SonyCameraConnectSequence::class.java.simpleName
    }

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

    override fun onDeviceFound(cameraDevice: ISonyCamera)
    {
        try
        {
            cameraStatusReceiver.onStatusNotify(context.getString(ID_STRING_CONNECT_CAMERA_FOUND) + " " + cameraDevice.getFriendlyName())
            cameraHolder.detectedCamera(cameraDevice)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onFinished()
    {
        Log.v(TAG, "SonyCameraConnectSequence.onFinished()")
        try
        {
            val thread = Thread {
                try
                {
                    cameraHolder.prepare()
                    cameraHolder.startRecMode()
                    cameraHolder.startEventWatch()
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

    private fun onConnectNotify()
    {
        try
        {
            val thread = Thread { // カメラとの接続確立を通知する
                cameraStatusReceiver.onStatusNotify(context.getString(ID_STRING_CONNECT_CONNECTED))
                cameraStatusReceiver.onCameraConnected()
                Log.v(TAG, "onConnectNotify()")
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

/*
    private fun waitForAMoment(mills: Long)
    {
        if (mills > 0)
        {
            try
            {
                Log.v(TAG, " WAIT " + mills + "ms")
                Thread.sleep(mills)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
*/

    override fun onErrorFinished(reason: String?)
    {
        cameraConnection?.alertConnectingFailed(reason)
    }
}
