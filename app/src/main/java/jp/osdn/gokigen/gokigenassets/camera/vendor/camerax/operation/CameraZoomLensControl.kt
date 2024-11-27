package jp.osdn.gokigen.gokigenassets.camera.vendor.camerax.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IZoomLensControl


class CameraZoomLensControl(private val cameraXCameraControl: CameraXCameraControl) : IZoomLensControl
{
    private var isDrivingZoom = false
    private var minimumZoomRatio : Float = cameraXCameraControl.getZoomState()?.value?.minZoomRatio ?: 1.0f
    private var maximumZoomRatio : Float = cameraXCameraControl.getZoomState()?.value?.maxZoomRatio ?: 1.0f
    private var currentZoomRatio = minimumZoomRatio
    private var zoomStepRatio : Float = (maximumZoomRatio - minimumZoomRatio) / 5.0f

    override fun canZoom(): Boolean
    {
        val zoomState = cameraXCameraControl.getZoomState()
        Log.v(TAG, " -=-=-=-=-=- getZoomState() : ${zoomState?.value} -=-=-=-=-=- ")
        minimumZoomRatio = cameraXCameraControl.getZoomState()?.value?.minZoomRatio ?: 1.0f
        maximumZoomRatio = cameraXCameraControl.getZoomState()?.value?.maxZoomRatio ?: 1.0f
        zoomStepRatio = (maximumZoomRatio - minimumZoomRatio) / 5.0f
        return (true)
    }

    override fun updateStatus()
    {
        //
    }

    override fun getMaximumFocalLength(): Float
    {
        return (maximumZoomRatio)
    }

    override fun getMinimumFocalLength(): Float
    {
       return (minimumZoomRatio)
    }

    override fun getCurrentFocalLength(): Float
    {
        return (currentZoomRatio)
    }

    override fun driveZoomLens(targetLength: Float)
    {
        Log.v(TAG, " driveZoomLens($targetLength)")
    }

    override fun driveZoomLens(isZoomIn: Boolean)
    {
        try
        {
            if (isZoomIn)
            {
                currentZoomRatio += zoomStepRatio
                if (currentZoomRatio > maximumZoomRatio)
                {
                    currentZoomRatio = maximumZoomRatio
                }
            }
            else
            {
                currentZoomRatio -= zoomStepRatio
                if (currentZoomRatio < minimumZoomRatio)
                {
                    currentZoomRatio = minimumZoomRatio
                }
            }
            Log.v(TAG, " driveZoomLens(zoomStepRatio = $zoomStepRatio, zoomRatio = $currentZoomRatio, isZoomIn = $isZoomIn)")
            cameraXCameraControl.setZoomRatio(currentZoomRatio)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun moveInitialZoomPosition()
    {
        Log.v(TAG, " moveInitialZoomPosition()")
    }

    override fun isDrivingZoomLens(): Boolean
    {
        return (isDrivingZoom)
    }

    companion object
    {
        private val TAG = CameraZoomLensControl::class.java.simpleName
    }

}
