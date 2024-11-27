package jp.osdn.gokigen.gokigenassets.camera.interfaces

import android.view.View
import jp.osdn.gokigen.gokigenassets.liveview.ICachePositionProvider
import jp.osdn.gokigen.gokigenassets.liveview.ILiveView
import jp.osdn.gokigen.gokigenassets.liveview.ILiveViewRefresher

interface ICameraControl
{
    fun getConnectionMethod() : String
    fun initialize()

    fun connectToCamera()
    fun startCamera(isPreviewView : Boolean = true, cameraSequence : Int = 0)
    fun finishCamera(isPowerOff: Boolean)

    fun changeCaptureMode(mode : String)
    fun needRotateImage() : Boolean

    fun setRefresher(id : Int, refresher : ILiveViewRefresher, imageView : ILiveView, cachePosition : ICachePositionProvider)
    fun captureButtonReceiver(id : Int = 0) : View.OnClickListener
    fun onLongClickReceiver(id : Int = 0) : View.OnLongClickListener
    fun keyDownReceiver(id : Int = 0) : IKeyDown

    fun getFocusingControl(id : Int = 0) : IFocusingControl?
    fun getDisplayInjector() : IDisplayInjector?
    fun getAnotherTouchListener(id : Int = 0) : View.OnTouchListener? = null
    fun getCameraStatus() : ICameraStatus?
    fun getCameraNumber() : Int
    fun getCameraShutter(id: Int = 0) : ICameraShutter?
    fun getZoomControl(id : Int = 0) : IZoomLensControl?

    fun setNeighborCameraControl(index: Int, camera0: ICameraControl?, camera1: ICameraControl?, camera2: ICameraControl?, camera3: ICameraControl?)
    fun setNeighborCameraControlFinished()
}
