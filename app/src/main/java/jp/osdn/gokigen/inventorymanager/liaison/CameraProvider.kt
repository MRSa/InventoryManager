package jp.osdn.gokigen.inventorymanager.liaison

import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.DummyCameraControl
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.camera.preference.CameraPreference
import jp.osdn.gokigen.gokigenassets.camera.preference.CameraPreferenceKeySet
import jp.osdn.gokigen.gokigenassets.camera.preference.ICameraPreferenceProvider
import jp.osdn.gokigen.gokigenassets.camera.vendor.CameraControlCoordinator
import jp.osdn.gokigen.gokigenassets.camera.vendor.camerax.operation.CameraControl
import jp.osdn.gokigen.gokigenassets.liveview.ILiveViewRefresher
import jp.osdn.gokigen.gokigenassets.liveview.image.CameraLiveViewListenerImpl
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider
import jp.osdn.gokigen.gokigenassets.preference.PreferenceAccessWrapper
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_METHOD_1
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_METHOD_1_DEFAULT_VALUE
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION1_1
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION1_1_DEFAULT_VALUE
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION2_1
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION2_1_DEFAULT_VALUE
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION3_1
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION3_1_DEFAULT_VALUE
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION4_1
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION4_1_DEFAULT_VALUE
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION5_1
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_OPTION5_1_DEFAULT_VALUE
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_SEQUENCE_1
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor.Companion.PREFERENCE_CAMERA_SEQUENCE_1_DEFAULT_VALUE

class CameraProvider(private val activity: AppCompatActivity, private val informationNotify: IInformationReceiver, private val statusReceiver : ICameraStatusReceiver)
{
    private val liveViewListener = CameraLiveViewListenerImpl(activity, informationNotify)
    private val cameraCoordinator = CameraControlCoordinator(informationNotify)
    private var cameraXisCreated = false
    private lateinit var cameraXControl0: ICameraControl

    fun setRefresher(refresher: ILiveViewRefresher)
    {
        liveViewListener.setRefresher(refresher = refresher)
    }

    fun getImageProvider() : IImageProvider
    {
        return (liveViewListener)
    }

    fun decideCameraControl(connectionMethod : String, number : Int) : ICameraControl
    {
        try
        {
            val wrapper = PreferenceAccessWrapper(activity)
            val cameraPreference = setupCameraPreference0(wrapper)
            return (prepareCameraXControl(cameraPreference, number, liveViewListener))
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (DummyCameraControl())
    }

    fun getCameraXControl(number : Int = 0) : ICameraControl
    {
        try
        {
            return (prepareCameraXControl(setupCameraPreference0(PreferenceAccessWrapper(activity)), number, liveViewListener))
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (DummyCameraControl())
    }

    private fun setupCameraPreference0(wrapper : PreferenceAccessWrapper) : ICameraPreferenceProvider
    {
        val method  = wrapper.getString(PREFERENCE_CAMERA_METHOD_1, PREFERENCE_CAMERA_METHOD_1_DEFAULT_VALUE)
        val sequence  = wrapper.getString(PREFERENCE_CAMERA_SEQUENCE_1, PREFERENCE_CAMERA_SEQUENCE_1_DEFAULT_VALUE)
        val option1  = wrapper.getString(PREFERENCE_CAMERA_OPTION1_1, PREFERENCE_CAMERA_OPTION1_1_DEFAULT_VALUE)
        val option2  = wrapper.getString(PREFERENCE_CAMERA_OPTION2_1, PREFERENCE_CAMERA_OPTION2_1_DEFAULT_VALUE)
        val option3  = wrapper.getString(PREFERENCE_CAMERA_OPTION3_1, PREFERENCE_CAMERA_OPTION3_1_DEFAULT_VALUE)
        val option4  = wrapper.getString(PREFERENCE_CAMERA_OPTION4_1, PREFERENCE_CAMERA_OPTION4_1_DEFAULT_VALUE)
        val option5  = wrapper.getString(PREFERENCE_CAMERA_OPTION5_1, PREFERENCE_CAMERA_OPTION5_1_DEFAULT_VALUE)

        return (CameraPreference(0, wrapper, method, false, sequence, option1, option2, option3, option4, option5, CameraPreferenceKeySet(PREFERENCE_CAMERA_OPTION1_1, PREFERENCE_CAMERA_OPTION2_1, PREFERENCE_CAMERA_OPTION3_1, PREFERENCE_CAMERA_OPTION4_1, PREFERENCE_CAMERA_OPTION5_1)))
    }

    private fun prepareCameraXControl(cameraPreference : ICameraPreferenceProvider, number : Int, liveViewListener: CameraLiveViewListenerImpl): ICameraControl
    {
        if ((cameraXisCreated)&&(::cameraXControl0.isInitialized))
        {
            return (cameraXControl0)
        }
        cameraXControl0 = CameraControl(activity, cameraPreference, AppSingleton.vibrator, informationNotify, statusReceiver, number, liveViewListener)
        cameraXisCreated = true
        return (cameraXControl0)
    }
}
