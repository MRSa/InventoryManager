package jp.osdn.gokigen.gokigenassets.camera

import android.graphics.Color
import android.view.KeyEvent
import android.view.View
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.gokigenassets.liveview.ICachePositionProvider
import jp.osdn.gokigen.gokigenassets.liveview.ILiveView
import jp.osdn.gokigen.gokigenassets.liveview.ILiveViewRefresher

class DummyCameraControl(private val number : Int = 0) : ICameraControl, View.OnClickListener, View.OnLongClickListener, IKeyDown, ICameraStatus, ICameraShutter, IZoomLensControl
{
    override fun getConnectionMethod(): String { return ("NONE") }
    override fun initialize() { }
    override fun connectToCamera() { }
    override fun startCamera(isPreviewView: Boolean, cameraSequence : Int) { }
    override fun finishCamera(isPowerOff: Boolean) { }
    override fun changeCaptureMode(mode: String) { }
    override fun needRotateImage(): Boolean { return (false) }
    override fun setRefresher(id : Int, refresher: ILiveViewRefresher, imageView: ILiveView, cachePosition : ICachePositionProvider) { }
    override fun captureButtonReceiver(id: Int): View.OnClickListener { return (this) }
    override fun onLongClickReceiver(id: Int): View.OnLongClickListener { return (this) }
    override fun keyDownReceiver(id: Int): IKeyDown { return (this) }
    override fun getFocusingControl(id: Int): IFocusingControl? { return (null) }
    override fun getDisplayInjector(): IDisplayInjector? { return (null) }
    override fun setNeighborCameraControl(index: Int, camera0: ICameraControl?, camera1: ICameraControl?, camera2: ICameraControl?, camera3: ICameraControl?) { }
    override fun setNeighborCameraControlFinished() { }
    override fun getCameraStatus(): ICameraStatus { return (this) }
    override fun getCameraNumber(): Int { return (number) }
    override fun getCameraShutter(id: Int): ICameraShutter { return (this) }
    override fun getZoomControl(id: Int): IZoomLensControl { return (this) }

    override fun onClick(v: View?) { }
    override fun handleKeyDown(keyCode: Int, event: KeyEvent): Boolean { return (false) }
    override fun onLongClick(v: View?): Boolean { return (false) }
    override fun getStatusList(key: String): List<String?> { return (ArrayList<String>()) }
    override fun getStatus(key: String): String { return ("") }
    override fun getStatusColor(key: String): Int { return (Color.WHITE) }
    override fun setStatus(key: String, value: String) { }
    override fun doShutter() { }
    override fun doShutterOff() { }

    override fun canZoom(): Boolean { return (false) }
    override fun updateStatus() { }
    override fun getMaximumFocalLength(): Float { return (0.0f) }
    override fun getMinimumFocalLength(): Float { return (0.0f) }
    override fun getCurrentFocalLength(): Float { return (0.0f) }
    override fun driveZoomLens(targetLength: Float) { }
    override fun driveZoomLens(isZoomIn: Boolean) { }
    override fun moveInitialZoomPosition() { }
    override fun isDrivingZoomLens(): Boolean { return (false) }
}
