package jp.osdn.gokigen.gokigenassets.camera.vendor.camerax.operation

import android.annotation.SuppressLint
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import androidx.camera.camera2.interop.Camera2CameraControl
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.core.ExposureState
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.ZoomState
import androidx.lifecycle.LiveData
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IDisplayInjector
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IFocusingControl
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IFocusingModeNotify
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay

class CameraXCameraControl : IFocusingControl, IDisplayInjector
{
    private var isCameraControlPrepared: Boolean = false
    private var isFrameDisplayPrepared: Boolean = false
    private lateinit var cameraXCameraControl: androidx.camera.core.CameraControl
    private lateinit var cameraXCameraInfo: androidx.camera.core.CameraInfo
    private lateinit var frameDisplay: IAutoFocusFrameDisplay
    private lateinit var indicatorControl: IIndicatorControl
    private lateinit var focusModeNotify: IFocusingModeNotify

    companion object
    {
        private val TAG = CameraXCameraControl::class.java.simpleName
    }

    fun setCameraControl(camera:  androidx.camera.core.Camera)
    {
        cameraXCameraControl = camera.cameraControl
        cameraXCameraInfo = camera.cameraInfo
        isCameraControlPrepared = true
    }

    // IDisplayInjector
    override fun injectDisplay(frameDisplayer: IAutoFocusFrameDisplay, indicator: IIndicatorControl, focusingModeNotify: IFocusingModeNotify)
    {
        Log.v(TAG, " injectDisplay()")
        frameDisplay = frameDisplayer
        indicatorControl = indicator
        focusModeNotify = focusingModeNotify
        isFrameDisplayPrepared = true
    }

    // IFocusingControl
    override fun driveAutoFocus(motionEvent: MotionEvent?): Boolean
    {
        //Log.v(TAG, "driveAutoFocus()")
        if ((!isCameraControlPrepared) || (!isFrameDisplayPrepared) || (motionEvent == null) || (motionEvent.action != MotionEvent.ACTION_DOWN))
        {
            return (false)
        }
        try
        {
            val point: PointF? = frameDisplay.getPointWithEvent(motionEvent)
            if ((point != null) && (frameDisplay.isContainsPoint(point)))
            {
                lockAutoFocus(point)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    // IFocusingControl
    override fun unlockAutoFocus()
    {
        if (!isCameraControlPrepared)
        {
            cameraXCameraControl.cancelFocusAndMetering()
        }
    }

    // IFocusingControl
    override fun halfPressShutter(isPressed: Boolean)
    {
        try
        {
            val thread = Thread {
                if ((isCameraControlPrepared) && (isFrameDisplayPrepared)) {
                    try {
                        if (isPressed) {
                            val autoFocusPoint =
                                SurfaceOrientedMeteringPointFactory(1.0f, 1.0f).createPoint(.5f, .5f)
                            val action =
                                FocusMeteringAction.Builder(autoFocusPoint, FocusMeteringAction.FLAG_AF)
                                    //.addPoint(autoFocusPoint, FocusMeteringAction.FLAG_AE)
                                    .apply { disableAutoCancel() }.build()
                            cameraXCameraControl.startFocusAndMetering(action)
                        } else {
                            cameraXCameraControl.cancelFocusAndMetering()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            thread.start()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun lockAutoFocus(point : PointF)
    {
        try
        {
            Log.v(TAG, "lockAutoFocus() : [$point]")
            val thread = Thread {
                try
                {
                    //val autoFocusPoint = SurfaceOrientedMeteringPointFactory(1.0f, 1.0f).createPoint(point.x, point.y)
                    val autoFocusPoint = SurfaceOrientedMeteringPointFactory(1.0f, 1.0f).createPoint(point.y, point.x)
                    val action = FocusMeteringAction.Builder(autoFocusPoint, FocusMeteringAction.FLAG_AF)
                        //FocusMeteringAction.Builder(autoFocusPoint)
                            //    .addPoint(autoFocusPoint, FocusMeteringAction.FLAG_AE)
                            .apply { disableAutoCancel() }.build()
                    cameraXCameraControl.startFocusAndMetering(action)

                    showFocusFrame(
                        getPreFocusFrameRect(point),
                        IAutoFocusFrameDisplay.FocusFrameStatus.Running,
                        0.0f
                    )
                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun getPreFocusFrameRect(point: PointF): RectF
    {
        val focusWidth = 0.075f // 0.075 is rough estimate.
        val focusHeight = 0.075f
        return RectF(point.x - focusWidth / 2.0f, point.y - focusHeight / 2.0f, point.x + focusWidth / 2.0f, point.y + focusHeight / 2.0f)
    }

    private fun showFocusFrame(rect: RectF, status: IAutoFocusFrameDisplay.FocusFrameStatus, duration: Float)
    {
        frameDisplay.showFocusFrame(rect, status, duration)
        indicatorControl.onAfLockUpdate(IAutoFocusFrameDisplay.FocusFrameStatus.Focused === status)
    }

    fun getExposureState() : ExposureState?
    {
        try
        {
            if (::cameraXCameraInfo.isInitialized)
            {
                return (cameraXCameraInfo.exposureState)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (null)
    }

    fun getZoomState() : LiveData<ZoomState>?
    {
        try
        {
            if (::cameraXCameraInfo.isInitialized)
            {
                return (cameraXCameraInfo.zoomState)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (null)
    }

    fun setZoomRatio(zoomRatio : Float) : Boolean
    {
        try
        {
            if (::cameraXCameraInfo.isInitialized)
            {
                cameraXCameraControl.setZoomRatio(zoomRatio)
                return (true)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }


    fun setExposureCompensation(indexValue : Int)
    {
        try
        {
            if (::cameraXCameraControl.isInitialized)
            {
                cameraXCameraControl.setExposureCompensationIndex(indexValue)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }



    @SuppressLint("UnsafeOptInUsageError")
    fun getCamera2CameraInfo() : Camera2CameraInfo?
    {
        try
        {
            if (::cameraXCameraInfo.isInitialized)
            {
                return (Camera2CameraInfo.from(cameraXCameraInfo))
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (null)
    }


    @SuppressLint("UnsafeOptInUsageError")
    fun getCamera2CameraControl() : Camera2CameraControl?
    {
        try
        {
            if (::cameraXCameraControl.isInitialized)
            {
                return (Camera2CameraControl.from(cameraXCameraControl))
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (null)
    }



}
