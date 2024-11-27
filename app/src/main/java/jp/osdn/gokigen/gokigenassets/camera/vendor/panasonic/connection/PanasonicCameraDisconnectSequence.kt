package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.connection

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.vendor.ICameraControlCoordinator

class PanasonicCameraDisconnectSequence(private val context: AppCompatActivity, private val powerOff: Boolean, private val cameraCoordinator: ICameraControlCoordinator, private val number : Int) : Runnable
{
    override fun run()
    {
        // カメラをPowerOffして接続を切る
        Log.v(PanasonicCameraDisconnectSequence::class.java.simpleName, " Disconnect from Panasonic.")
        try
        {
            // カメラとの接続を切る
            cameraCoordinator.releaseCameraControl(number)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
