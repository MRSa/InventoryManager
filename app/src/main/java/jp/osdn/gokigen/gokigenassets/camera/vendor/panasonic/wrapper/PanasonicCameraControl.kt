package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.wrapper

import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.preference.ICameraPreferenceProvider
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraChangeListener
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCameraHolder
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.connection.PanasonicCameraConnection
import jp.osdn.gokigen.gokigenassets.camera.vendor.ICameraControlCoordinator
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.liveview.PanasonicLiveViewControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation.PanasonicCameraCaptureControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation.PanasonicCameraFocusControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation.PanasonicCameraZoomLensControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.status.CameraEventObserver
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
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient

class PanasonicCameraControl(private val context: AppCompatActivity, private val vibrator : IVibrator, private val informationNotify: IInformationReceiver, private val preference: ICameraPreferenceProvider, provider: ICameraStatusReceiver, private val cameraCoordinator: ICameraControlCoordinator, private val number: Int = 0, private val liveViewListener: CameraLiveViewListenerImpl = CameraLiveViewListenerImpl(context, informationNotify)) : IPanasonicCameraHolder, IDisplayInjector,ILiveViewController, ICameraControl, View.OnClickListener, View.OnLongClickListener, ICameraShutter, IKeyDown
{
    private val cardSlotSelector = PanasonicCardSlotSelector()
    private val cameraConnection: PanasonicCameraConnection = PanasonicCameraConnection(context, provider, this, this, cardSlotSelector, cameraCoordinator, number)
    private val storeImage = StoreImage(context, liveViewListener)

    private lateinit var cachePositionProvider : ICachePositionProvider
    private lateinit var panasonicCamera: IPanasonicCamera
    private lateinit var statusChecker: CameraEventObserver
    private var liveViewControl: PanasonicLiveViewControl? = null
    private var focusControl: PanasonicCameraFocusControl? = null
    private var captureControl: PanasonicCameraCaptureControl? = null
    private val zoomControl = PanasonicCameraZoomLensControl()
    private var isStatusWatch = false
    private var cameraPositionId = 0

    companion object
    {
        private val TAG = PanasonicCameraControl::class.java.simpleName
        private const val TIMEOUT_MS = 3000
        private const val CONNECT_DELAY_MS : Long = 150
    }

    override fun prepare()
    {
        if (::panasonicCamera.isInitialized)
        {
            Log.v(TAG, " prepare : " + panasonicCamera.getFriendlyName() + " " + panasonicCamera.getModelName())
            try
            {
                if (!::statusChecker.isInitialized)
                {
                    statusChecker = CameraEventObserver(context, panasonicCamera, cardSlotSelector)
                }
                if (liveViewControl == null)
                {
                    liveViewControl = PanasonicLiveViewControl(liveViewListener, panasonicCamera, statusChecker.getCameraStatusEventObserver(), informationNotify, number)
                }
                focusControl?.setCamera(panasonicCamera)
                captureControl?.setCamera(panasonicCamera)
                captureControl?.setCameraStatus(statusChecker.getCameraStatusConvert())
                zoomControl.setCamera(panasonicCamera)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
        else
        {
            Log.v(TAG, " panasonicCamera is not initialized...")
        }
    }

    override fun startRecMode()
    {
        try
        {
            val http = SimpleHttpClient()
            val sessionId = panasonicCamera.getCommunicationSessionId()

            // 撮影モード(RecMode)に切り替え
            val recModeUrl = panasonicCamera.getCmdUrl() + "cam.cgi?mode=camcmd&value=recmode"
            val reply = if (!sessionId.isNullOrEmpty())
            {
                val headerMap: MutableMap<String, String> = HashMap()
                headerMap["X-SESSION_ID"] = sessionId
                http.httpGetWithHeader(recModeUrl, headerMap, null, TIMEOUT_MS) ?: ""
            }
            else
            {
                http.httpGet(recModeUrl, TIMEOUT_MS)
            }
            if (!reply.contains("ok"))
            {
                Log.v(TAG, "CAMERA REPLIED ERROR : CHANGE RECMODE.")
            }

            //  フォーカスに関しては、１点に切り替える（仮）
            val focusModeUrl = panasonicCamera.getCmdUrl() + "cam.cgi?mode=setsetting&type=afmode&value=1area"
            val reply1 = if (!sessionId.isNullOrEmpty())
            {
                val headerMap: MutableMap<String, String> = HashMap()
                headerMap["X-SESSION_ID"] = sessionId
                http.httpGetWithHeader(focusModeUrl, headerMap, null, TIMEOUT_MS) ?: ""
            }
            else
            {
                http.httpGet(focusModeUrl, TIMEOUT_MS)
            }
            if (!reply1.contains("ok"))
            {
                Log.v(TAG, "CAMERA REPLIED ERROR : CHANGE AF MODE 1area.")
            }

            //  測光モードに関しては、画面全体の測光に切り替える（仮）
            val aeModeUrl = panasonicCamera.getCmdUrl() + "cam.cgi?mode=setsetting&type=lightmetering&value=multi"
            val reply2 = if (!sessionId.isNullOrEmpty())
            {
                val headerMap: MutableMap<String, String> = HashMap()
                headerMap["X-SESSION_ID"] = sessionId
                http.httpGetWithHeader(aeModeUrl, headerMap, null, TIMEOUT_MS) ?: ""
            }
            else
            {
                http.httpGet(aeModeUrl, TIMEOUT_MS)
            }
            if (!reply2.contains("ok"))
            {
                Log.v(TAG, "CAMERA REPLIED ERROR : CHANGE AF MODE 1area.")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun startEventWatch(listener: ICameraChangeListener?)
    {
        try
        {
            if (::statusChecker.isInitialized)
            {
                if (listener != null)
                {
                    statusChecker.setEventListener(listener)
                }
                statusChecker.startStatusWatch(null, null)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun detectedCamera(camera: IPanasonicCamera)
    {
        Log.v(TAG, "detectedCamera()")
        panasonicCamera = camera
    }

    override fun getConnectionMethod(): String
    {
        return ("PANASONIC")
    }

    override fun initialize()
    {
        // TODO("Not yet implemented")
    }

    override fun connectToCamera()
    {
        Log.v(TAG, " connectToCamera() : PANASONIC ")
        try
        {
            while (!cameraCoordinator.startConnectToCamera(number))
            {
                try
                {
                    Thread.sleep(CONNECT_DELAY_MS)
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            cameraConnection.connect()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun startCamera(isPreviewView: Boolean, cameraSequence: Int)
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
            if (isStatusWatch)
            {
                if (::statusChecker.isInitialized)
                {
                    statusChecker.stopStatusWatch()
                }
                isStatusWatch = false
            }
            cameraConnection.disconnect(isPowerOff)
            cameraConnection.stopWatchWifiStatus(context)
            stopLiveView()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun changeCaptureMode(mode: String)
    {
        // TODO("Not yet implemented")
    }

    override fun needRotateImage(): Boolean
    {
        return (false)
    }

    override fun setRefresher(id: Int, refresher: ILiveViewRefresher, imageView: ILiveView, cachePosition : ICachePositionProvider)
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

    override fun getFocusingControl(id: Int): IFocusingControl?
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
        focusControl = PanasonicCameraFocusControl(frameDisplayer, indicator)
        captureControl = PanasonicCameraCaptureControl(frameDisplayer, indicator)
    }

    override fun startLiveView(isCameraScreen: Boolean)
    {
        Log.v(TAG, " startLiveView($isCameraScreen) ")
        try
        {
            if (!isStatusWatch)
            {
                if (::statusChecker.isInitialized)
                {
                    statusChecker.startStatusWatch(null, null)
                    isStatusWatch = true
                }
            }
            liveViewControl?.startLiveView()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun stopLiveView()
    {
        Log.v(TAG, " stopLiveView() ")
        try
        {
            liveViewControl?.stopLiveView()
            if (isStatusWatch)
            {
                if (::statusChecker.isInitialized)
                {
                    statusChecker.stopStatusWatch()
                    isStatusWatch = false
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

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
            if (captureControl == null)
            {
                Log.v(TAG, " captureControl is NULL.")
            }
            captureControl?.doCapture(0)
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
            captureControl?.doCapture(0)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
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

    override fun onLongClick(v: View?): Boolean
    {
        return (false)
    }

    override fun setNeighborCameraControl(index: Int, camera0: ICameraControl?, camera1: ICameraControl?, camera2: ICameraControl?, camera3: ICameraControl?) { }
    override fun setNeighborCameraControlFinished() { }

    override fun getCameraStatus(): ICameraStatus?
    {
        if (!::statusChecker.isInitialized)
        {
            return (null)
        }
        return (statusChecker.getCameraStatusConvert())
    }

    override fun getCameraNumber(): Int
    {
        return (number)
    }

    override fun getCameraShutter(id: Int): ICameraShutter { return (this) }
    override fun getZoomControl(id: Int): IZoomLensControl { return (zoomControl) }

}
