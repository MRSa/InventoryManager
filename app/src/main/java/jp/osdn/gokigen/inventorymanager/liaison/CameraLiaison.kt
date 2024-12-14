package jp.osdn.gokigen.inventorymanager.liaison

import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraShutterNotify
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.liveview.IAnotherDrawer
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.inventorymanager.R

class CameraLiaison(private val activity: AppCompatActivity, private val informationNotify: IInformationReceiver, statusReceiver : ICameraStatusReceiver, shutterNotify: ICameraShutterNotify)
{
    private val drawers = AnotherDrawerHolder()
    private val cameraProvider = CameraProvider(activity, informationNotify, statusReceiver, shutterNotify)
    private lateinit var cameraControl: ICameraControl  // = cameraProvider.getCameraXControl()

    init
    {
        try
        {
            cameraControl = try {
                cameraProvider.decideCameraControl(0)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                cameraProvider.getCameraXControl()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun initialize()
    {
        try
        {
            val msg = activity.getString(R.string.app_name)
            informationNotify.updateMessage(msg, isBold = false, isColor = true, color = Color.LTGRAY)

            cameraControl.initialize()
            cameraControl.startCamera(isPreviewView = false)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun connectToCamera()
    {
        try
        {
            cameraControl.connectToCamera()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun finish()
    {
        Log.v(TAG, " finishCamera() ")
        cameraControl.finishCamera(false)
    }

    fun getCameraControl() : ICameraControl { return (cameraControl) }
    fun getAnotherDrawer() : IAnotherDrawer { return (drawers) }

    fun handleKeyDown(keyCode: Int, event: KeyEvent): Boolean
    {
        try
        {
            Log.v(TAG, " handleKeyDown($keyCode, ${event.action})")
        }
        catch (e : java.lang.Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    companion object
    {
        private val  TAG = CameraLiaison::class.java.simpleName
    }
}
