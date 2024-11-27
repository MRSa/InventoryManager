package jp.osdn.gokigen.gokigenassets.camera.vendor.omds

import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.gokigenassets.camera.preference.ICameraPreferenceProvider
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.connection.OmdsCameraConnection
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.liveview.OmdsLiveViewControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.operation.OmdsRunModeControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.operation.OmdsZoomLensControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.status.OmdsCameraStatusWatcher
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.wrapper.OmdsCaptureControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.omds.wrapper.OmdsFocusControl
import jp.osdn.gokigen.constants.IApplicationConstantConvert
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW_DEFAULT_VALUE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE_DEFAULT_VALUE
import jp.osdn.gokigen.gokigenassets.liveview.ICachePositionProvider
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.ILiveView
import jp.osdn.gokigen.gokigenassets.liveview.ILiveViewRefresher
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.liveview.image.CameraLiveViewListenerImpl
import jp.osdn.gokigen.gokigenassets.liveview.storeimage.StoreImage
import jp.osdn.gokigen.gokigenassets.preference.PreferenceAccessWrapper
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.gokigenassets.scene.IVibrator

class OmdsCameraControl(private val context: AppCompatActivity, private val vibrator: IVibrator, informationNotify : IInformationReceiver, private val preference: ICameraPreferenceProvider, provider: ICameraStatusReceiver, private val number : Int = 0,  private val liveViewListener: CameraLiveViewListenerImpl = CameraLiveViewListenerImpl(context, informationNotify)) : ICameraControl, View.OnClickListener, View.OnLongClickListener, ICameraShutter, IKeyDown, IDisplayInjector, IOmdsProtocolNotify
{
    private val statusChecker = OmdsCameraStatusWatcher()
    private val runModeControl = OmdsRunModeControl()
    private val zoomLensControl = OmdsZoomLensControl(statusChecker)
    private val storeImage = StoreImage(context, liveViewListener)
    private val liveViewControl = OmdsLiveViewControl(liveViewListener, statusChecker)
    private val cameraConnection = OmdsCameraConnection(context, provider, statusChecker, liveViewControl, this)

    private lateinit var cachePositionProvider : ICachePositionProvider
    private lateinit var focusControl: OmdsFocusControl
    private lateinit var captureControl: OmdsCaptureControl

    private var cameraPositionId = 0

    override fun getConnectionMethod(): String
    {
        return ("OMDS")
    }

    override fun initialize()
    {
        Log.v(TAG, " --- initialize() : SEQ : ${preference.getConnectionSequence()}")
    }

    override fun connectToCamera()
    {
        Log.v(TAG, " connectToCamera() : OMDS ")
        try
        {
            cameraConnection.connect()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun startCamera(isPreviewView: Boolean, cameraSequence : Int)
    {
        try
        {
            if (cameraConnection.getConnectionStatus() != ICameraConnectionStatus.CameraConnectionStatus.CONNECTED)
            {
                cameraConnection.startWatchWifiStatus(context)
            }
            else
            {
                cameraConnection.connect()
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun finishCamera(isPowerOff: Boolean)
    {
        try
        {
            liveViewControl.stopLiveView()
            statusChecker.stopStatusWatch()
            cameraConnection.disconnect(isPowerOff)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun changeCaptureMode(mode: String)
    {
        Log.v(TAG, "changeCaptureMode($mode) : ${runModeControl.isRecordingMode()}")

        //dummy call
        Log.v(TAG, " canZoom() : ${zoomLensControl.canZoom()}")
    }

    override fun needRotateImage(): Boolean { return (false) }

    override fun setRefresher(id : Int, refresher: ILiveViewRefresher, imageView: ILiveView, cachePosition : ICachePositionProvider)
    {
        try
        {
            liveViewListener.setRefresher(refresher)
            imageView.setImageProvider(liveViewListener)
            cachePositionProvider = cachePosition
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun captureButtonReceiver(id: Int): View.OnClickListener
    {
        cameraPositionId = id
        return (this)
    }

    override fun onLongClickReceiver(id: Int): View.OnLongClickListener
    {
        cameraPositionId = id
        return (this)
    }

    override fun keyDownReceiver(id: Int): IKeyDown
    {
        cameraPositionId = id
        return (this)
    }

    override fun getFocusingControl(id: Int): IFocusingControl
    {
        cameraPositionId = id
        return (focusControl)
    }

    override fun getDisplayInjector(): IDisplayInjector
    {
        return (this)
    }

    override fun injectDisplay(frameDisplayer: IAutoFocusFrameDisplay, indicator: IIndicatorControl, focusingModeNotify: IFocusingModeNotify)
    {
        Log.v(TAG, "injectDisplay()")
        focusControl = OmdsFocusControl(frameDisplayer, indicator)
        captureControl = OmdsCaptureControl(frameDisplayer, indicator, statusChecker)
        statusChecker.setIOpcFocusLockResult(focusControl.getFocusLockResult())
    }

    override fun setNeighborCameraControl(index: Int, camera0: ICameraControl?, camera1: ICameraControl?, camera2: ICameraControl?, camera3: ICameraControl?) { }
    override fun setNeighborCameraControlFinished() { }
    override fun getCameraStatus(): ICameraStatus { return (statusChecker) }

    override fun onClick(v: View?)
    {
        if (v == null)
        {
            return
        }
        when (v.id)
        {
            IApplicationConstantConvert.ID_BUTTON_SHUTTER -> { doShutter() }
            else -> { }
        }
    }

    override fun handleKeyDown(keyCode: Int, event: KeyEvent): Boolean
    {
        if ((event.action == KeyEvent.ACTION_DOWN)&&((keyCode == KeyEvent.KEYCODE_VOLUME_UP)||(keyCode == KeyEvent.KEYCODE_CAMERA)))
        {
            doShutter()
            return (true)
        }
        return (false)
    }

    override fun onLongClick(v: View?): Boolean
    {
        return (false)
    }

    override fun doShutter()
    {
        try
        {
            Log.v(TAG, " doShutter()")
            val isNotDriveShutter = captureImageLiveView()
            if (isNotDriveShutter)
            {
                //  シャッターを駆動させない(けど、バイブレーションで通知する)
                vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT)
                return
            }
            captureControl.doCapture(0)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun doShutterOff()
    {
        try
        {
            Log.v(TAG, " doShutterOff()")
            captureControl.doCapture(0)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun captureImageLiveView() : Boolean
    {
        try
        {
            //  preferenceから設定を取得する
            val captureBothCamera = PreferenceAccessWrapper(context).getBoolean(ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW, ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW_DEFAULT_VALUE)
            val notUseShutter = PreferenceAccessWrapper(context).getBoolean(ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE, ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE_DEFAULT_VALUE)
            if ((captureBothCamera)&&(liveViewListener.isImageReceived()))
            {
                // ライブビュー画像を保管する場合...
                val thread = Thread { storeImage.doStore(cameraPositionId, false, cachePositionProvider.getCachePosition()) }
                try
                {
                    thread.start()
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            return (notUseShutter)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    override fun getCameraNumber(): Int
    {
        return (number)
    }

    override fun getCameraShutter(id: Int): ICameraShutter { return (this) }
    override fun getZoomControl(id: Int): IZoomLensControl { return (zoomLensControl) }

    override fun detectedOpcProtocol(opcProtocol: Boolean)
    {
        zoomLensControl.detectedOpcProtocol(opcProtocol)
        focusControl.detectedOpcProtocol(opcProtocol)
        captureControl.detectedOpcProtocol(opcProtocol)
        statusChecker.detectedOpcProtocol(opcProtocol)
    }

    companion object
    {
        private val TAG = OmdsCameraControl::class.java.simpleName
    }
}
