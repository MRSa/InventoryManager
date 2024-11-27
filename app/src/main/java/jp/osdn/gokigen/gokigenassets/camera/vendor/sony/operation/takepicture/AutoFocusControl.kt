package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.operation.takepicture

import org.json.JSONObject
import android.graphics.RectF
import android.graphics.PointF
import android.util.Log
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCameraApi
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay.FocusFrameStatus
import java.lang.Exception

class AutoFocusControl(private val frameDisplayer: IAutoFocusFrameDisplay, private val indicator: IIndicatorControl)
{
    private lateinit var cameraApi: ISonyCameraApi
    fun setCameraApi(sonyCameraApi: ISonyCameraApi)
    {
        cameraApi = sonyCameraApi
    }

    fun lockAutoFocus(point: PointF)
    {
        Log.v(TAG, "lockAutoFocus() : [" + point.x + ", " + point.y + "]")
        if (!::cameraApi.isInitialized)
        {
            Log.v(TAG, "ISonyCameraApi is not initialized...")
            return
        }
        try
        {
            val thread = Thread {
                val preFocusFrameRect = getPreFocusFrameRect(point)
                try
                {
                    showFocusFrame(preFocusFrameRect, FocusFrameStatus.Running, 0.0)
                    val posX = point.x * 100.0
                    val posY = point.y * 100.0
                    Log.v(TAG, "AF ($posX, $posY)")
                    val resultsObj = cameraApi.setTouchAFPosition(posX, posY)
                    if (resultsObj != null)
                    {
                        //Log.v(TAG, " lockAutoFocus() LEN:${resultsObj.length()}  $resultsObj")
                        if (findTouchAFPositionResult(resultsObj))
                        {
                            // AF FOCUSED
                            Log.v(TAG, "lockAutoFocus() : FOCUSED")
                            showFocusFrame(preFocusFrameRect, FocusFrameStatus.Focused, 0.0)
                        }
                        else
                        {
                            // AF ERROR
                            Log.v(TAG, "lockAutoFocus() : ERROR")
                            showFocusFrame(preFocusFrameRect, FocusFrameStatus.Failed, 1.0)
                        }
                    }
                    else
                    {
                        Log.v(TAG, "setTouchAFPosition() reply is null.")
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    try
                    {
                        showFocusFrame(preFocusFrameRect, FocusFrameStatus.Errored, 1.0)
                    }
                    catch (ee: Exception)
                    {
                        ee.printStackTrace()
                    }
                }
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * シャッター半押し処理
     *
     */
    fun halfPressShutter(isPressed: Boolean)
    {
        Log.v(TAG, "halfPressShutter() : $isPressed")
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
                    val resultsObj = if (isPressed) cameraApi.actHalfPressShutter() else cameraApi.cancelHalfPressShutter()
                    if (resultsObj == null)
                    {
                        Log.v(TAG, "halfPressShutter() [$isPressed] reply is null.")
                    }
                    else
                    {
                        indicator.onAfLockUpdate(isPressed)
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

    /**
     *
     *
     */
    fun unlockAutoFocus()
    {
        Log.v(TAG, "unlockAutoFocus()")
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
                    val resultsObj = cameraApi.cancelTouchAFPosition()
                    if (resultsObj == null)
                    {
                        Log.v(TAG, "cancelTouchAFPosition() reply is null.")
                    }
                    hideFocusFrame()
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

    /**
     *
     *
     */
    private fun showFocusFrame(rect: RectF, status: FocusFrameStatus, duration: Double)
    {
        frameDisplayer.showFocusFrame(rect, status, duration.toFloat())
        indicator.onAfLockUpdate(FocusFrameStatus.Focused === status)
    }

    /**
     *
     *
     */
    private fun hideFocusFrame()
    {
        frameDisplayer.hideFocusFrame()
        indicator.onAfLockUpdate(false)
    }

    /**
     *
     *
     */
    private fun findTouchAFPositionResult(replyJson: JSONObject): Boolean
    {
        var afResult = false
        try
        {
            val indexOfTouchAFPositionResult = 1
            val resultsObj = replyJson.getJSONArray("result")
            if (!resultsObj.isNull(indexOfTouchAFPositionResult))
            {
                val touchAFPositionResultObj = resultsObj.getJSONObject(indexOfTouchAFPositionResult)
                afResult = touchAFPositionResultObj.getBoolean("AFResult")
                Log.v(TAG, "AF Result : $afResult")
            }
        }
        catch (e: Exception)
        {
            //e.printStackTrace()

            // resultがない... halfPressShutterで対応する
            Log.v(TAG, " Touch-AF is fail. try halfPressShutter action.")
            halfPressShutter(true)
        }
        return afResult
    }

    /**
     *
     *
     */
    private fun getPreFocusFrameRect(point: PointF): RectF
    {
        val imageWidth = frameDisplayer.getContentSizeWidth()
        val imageHeight = frameDisplayer.getContentSizeHeight()

        // Display a provisional focus frame at the touched point.
        val focusWidth = 0.125f // 0.125 is rough estimate.
        var focusHeight = 0.125f
        focusHeight *= if (imageWidth > imageHeight)
        {
            imageWidth / imageHeight
        }
        else
        {
            imageHeight / imageWidth
        }
        return RectF(point.x - focusWidth / 2.0f, point.y - focusHeight / 2.0f, point.x + focusWidth / 2.0f, point.y + focusHeight / 2.0f)
    }

    companion object
    {
        private val TAG = AutoFocusControl::class.java.simpleName
    }
}
