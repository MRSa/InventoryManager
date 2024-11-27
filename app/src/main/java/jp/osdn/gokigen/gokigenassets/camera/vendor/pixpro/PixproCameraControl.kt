package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro

import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.gokigenassets.camera.preference.ICameraPreferenceProvider
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.operation.FocusControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.operation.MovieShotControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.operation.SingleShotControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.operation.ZoomControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.IPixproCamera
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.IPixproCameraInitializer
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.PixproCamera
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.IPixproCommandPublisher
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.IPixproCommunication
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.IPixproCommunicationNotify
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.PixproCommandCommunicator
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.connection.PixproCameraConnection
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.liveview.PixproLiveViewControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.status.PixproStatusChecker
import jp.osdn.gokigen.constants.IApplicationConstantConvert
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW_DEFAULT_VALUE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE_DEFAULT_VALUE
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_STRING_COMMAND_LINE_DISCONNECTED
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

class PixproCameraControl(private val context: AppCompatActivity, private val vibrator: IVibrator, private val informationNotify : IInformationReceiver, private val preference: ICameraPreferenceProvider, provider: ICameraStatusReceiver, private val number : Int = 0, private val liveViewListener: CameraLiveViewListenerImpl = CameraLiveViewListenerImpl(context, informationNotify)) : ICameraControl, View.OnClickListener, View.OnLongClickListener, ICameraShutter, IKeyDown, IPixproInternalInterfaces, IPixproCommunicationNotify, IDisplayInjector
{
    private val statusChecker = PixproStatusChecker()
    private val cameraConnection = PixproCameraConnection(context, provider, this, statusChecker)
    private val pixproCameraParameter = PixproCamera()
    private val commandCommunicator = PixproCommandCommunicator(pixproCameraParameter, this, statusChecker)
    private val storeImage = StoreImage(context, liveViewListener)
    private val zoomControl = ZoomControl(commandCommunicator)

    private lateinit var liveViewControl : PixproLiveViewControl
    private lateinit var cachePositionProvider : ICachePositionProvider
    private lateinit var focusControl: FocusControl
    private lateinit var stillControl: SingleShotControl
    private lateinit var movieControl: MovieShotControl


    private var cameraPositionId = 0

    companion object
    {
        private val TAG = PixproCameraControl::class.java.simpleName
    }

    override fun getConnectionMethod(): String
    {
        return ("PIXPRO")
    }

    override fun initialize()
    {
        Log.v(TAG, " --- initialize() : SEQ : ${preference.getConnectionSequence()}")
        statusChecker.setCommandPublisher(commandCommunicator)
    }

    override fun connectToCamera()
    {
        Log.v(TAG, " connectToCamera() : PIXPRO ")
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
            if (::liveViewControl.isInitialized)
            {
                liveViewControl.stopLiveView()
            }
            statusChecker.stopStatusWatch()
            cameraConnection.disconnect(isPowerOff)
            cameraConnection.stopWatchWifiStatus(context)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun changeCaptureMode(mode: String) { }
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
        focusControl = FocusControl(commandCommunicator, frameDisplayer)
        stillControl = SingleShotControl(commandCommunicator, frameDisplayer)
        movieControl = MovieShotControl(commandCommunicator, frameDisplayer, statusChecker)
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

    override fun getIPixproCommunication(): IPixproCommunication { return (commandCommunicator) }
    override fun getIPixproCommandPublisher(): IPixproCommandPublisher { return (commandCommunicator) }
    override fun getInformationReceiver(): IInformationReceiver { return (informationNotify) }
    override fun getIPixproCameraInitializer(): IPixproCameraInitializer { return (pixproCameraParameter) }
    override fun getIPixproCamera(): IPixproCamera { return (pixproCameraParameter) }
    override fun getIPixproCommunicationNotify(): IPixproCommunicationNotify { return (this) }

    override fun readyToCommunicate()
    {
        Log.v(TAG, " ----- readyToCommunicate() ")
        try
        {
            if (!::liveViewControl.isInitialized)
            {
                liveViewControl = PixproLiveViewControl(liveViewListener, pixproCameraParameter)
            }
            liveViewControl.startLiveView()
            statusChecker.startStatusWatch(null, null)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun detectDisconnect()
    {
        Log.v(TAG, " ----- detectDisconnect() ")
        cameraConnection.forceUpdateConnectionStatus(ICameraConnectionStatus.CameraConnectionStatus.DISCONNECTED)
        cameraConnection.alertConnectingFailed(context.getString(ID_STRING_COMMAND_LINE_DISCONNECTED))
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
            when (statusChecker.getStatus(ICameraStatus.TAKE_MODE))
            {
                "Video" -> {
                    if (::movieControl.isInitialized)
                    {
                        movieControl.doCapture(0)
                        vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT)
                    }
                }
                else -> {
                    if (::stillControl.isInitialized)
                    {
                        stillControl.doCapture(0)
                    }
                }
            }
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
            when (statusChecker.getStatus(ICameraStatus.TAKE_MODE))
            {
                "Video" -> {
                    if (::movieControl.isInitialized)
                    {
                        movieControl.doCapture(-1)
                        vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_MIDDLE)
                    }
                }
                else -> {
                    if (::stillControl.isInitialized)
                    {
                        stillControl.doCapture(-1)
                    }
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getCameraNumber(): Int
    {
        return (number)
    }

    override fun getCameraShutter(id: Int): ICameraShutter { return (this) }
    override fun getZoomControl(id: Int): IZoomLensControl { return (zoomControl) }

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
}
