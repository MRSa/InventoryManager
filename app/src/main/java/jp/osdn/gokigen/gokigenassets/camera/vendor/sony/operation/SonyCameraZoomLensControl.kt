package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCameraApi
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IZoomLensControl
import java.lang.Exception

class SonyCameraZoomLensControl : IZoomLensControl
{
    private lateinit var cameraApi: ISonyCameraApi

    companion object
    {
        private val TAG = SonyCameraZoomLensControl::class.java.simpleName
    }
    init
    {
        Log.v(TAG, "SonyCameraZoomLensControl()")
    }

    fun setCameraApi(sonyCameraApi: ISonyCameraApi)
    {
        cameraApi = sonyCameraApi
    }

    override fun canZoom(): Boolean
    {
        Log.v(TAG, "canZoom()")
        return true
    }

    override fun updateStatus()
    {
        Log.v(TAG, "updateStatus()")
    }

    override fun getMaximumFocalLength(): Float
    {
        Log.v(TAG, "getMaximumFocalLength()")
        return (0.0f)
    }

    override fun getMinimumFocalLength(): Float
    {
        Log.v(TAG, "getMinimumFocalLength()")
        return (0.0f)
    }

    override fun getCurrentFocalLength(): Float
    {
        Log.v(TAG, "getCurrentFocalLength()")
        return (0.0f)
    }

    override fun driveZoomLens(targetLength: Float)
    {
        Log.v(TAG, "driveZoomLens() : $targetLength")
    }

    override fun moveInitialZoomPosition()
    {
        Log.v(TAG, "moveInitialZoomPosition()")
    }

    override fun isDrivingZoomLens(): Boolean
    {
        Log.v(TAG, "isDrivingZoomLens()")
        return (false)
    }

    override fun driveZoomLens(isZoomIn: Boolean)
    {
        Log.v(TAG, "driveZoomLens() : $isZoomIn")
        if (!::cameraApi.isInitialized)
        {
             Log.v(TAG, "ISonyCameraApi is not initialized...")
            return
        }
        try
        {
            val direction = if (isZoomIn) "in" else "out"
            val movement = "1shot"
            val thread = Thread {
                try
                {
                    val resultsObj = cameraApi.actZoom(direction, movement)
                    if (resultsObj == null)
                    {
                        Log.v(TAG, "driveZoomLens() reply is null.")
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
