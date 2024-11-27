package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.operation.takepicture

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCameraApi
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import java.lang.Exception

class ContinuousShotControl(private val frameDisplayer: IAutoFocusFrameDisplay, private val indicator: IIndicatorControl)
{
    private lateinit var cameraApi: ISonyCameraApi
    fun setCameraApi(sonyCameraApi: ISonyCameraApi)
    {
        cameraApi = sonyCameraApi
    }

    fun continuousShot(isStop: Boolean)
    {
        Log.v(TAG, "continuousShot()")
        if (!::cameraApi.isInitialized)
        {
            Log.v(TAG, "ISonyCameraApi is not initialized...")
            return
        }
        try
        {
            val thread = Thread {
                try
                {
                    val resultsObj = if (isStop) { cameraApi.stopContShooting() } else { cameraApi.startContShooting() }
                    if (resultsObj == null)
                    {
                        Log.v(TAG, "actTakePicture() reply is null.")
                    }
                    else
                    {
                        // 撮影後、タッチAFをキャンセルする
                        val resultsObj2 = cameraApi.cancelTouchAFPosition()
                        if (resultsObj2 == null)
                        {
                            Log.v(TAG, "cancelTouchAFPosition() reply is null.")
                        }
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                frameDisplayer.hideFocusFrame()
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = ContinuousShotControl::class.java.simpleName
    }
}
