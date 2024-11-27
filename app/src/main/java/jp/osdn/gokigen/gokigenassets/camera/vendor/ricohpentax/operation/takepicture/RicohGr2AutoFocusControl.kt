package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation.takepicture

import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import org.json.JSONObject
import kotlin.math.roundToInt

/**
 *
 *
 */
class RicohGr2AutoFocusControl(private val frameDisplayer: IAutoFocusFrameDisplay, private val indicator: IIndicatorControl, executeUrl : String = "http://192.168.0.1")
{
    private val autoFocusUrl = "$executeUrl/v1/lens/focus"
    private val lockAutoFocusUrl = "$executeUrl/v1/lens/focus/lock"
    private val unlockAutoFocusUrl = "$executeUrl/v1/lens/focus/unlock"
    private val timeoutMs = 6000
    private val httpClient = SimpleHttpClient()
    private var useGR2command = false

    fun setUseGR2Command(useGR2command: Boolean)
    {
        this.useGR2command = useGR2command
    }

    /**
     *
     *
     */
    fun lockAutoFocus(point: PointF)
    {
        Log.v(TAG, "lockAutoFocus() : [" + point.x + ", " + point.y + "]")
        try
        {
            val thread = Thread {
                val preFocusFrameRect = getPreFocusFrameRect(point)
                try
                {
                    showFocusFrame(preFocusFrameRect, IAutoFocusFrameDisplay.FocusFrameStatus.Running, 0.0f)

                    //int posX = (int) (Math.round(point.x * 100.0));
                    //int posY = (int) (Math.round(point.y * 100.0));
                    val focusUrl = if (useGR2command) lockAutoFocusUrl else autoFocusUrl
                    val postData =
                        "pos=" + (point.x * 100.0).roundToInt() + "," + (point.y * 100.0).roundToInt()
                    Log.v(TAG, "AF ($postData)")
                    val result: String? = httpClient.httpPost(focusUrl, postData, timeoutMs)
                    if (result == null || result.isEmpty())
                    {
                        Log.v(TAG, "setTouchAFPosition() reply is null.")
                    }
                    else if (findTouchAFPositionResult(result))
                    {
                        // AF FOCUSED
                        Log.v(TAG, "lockAutoFocus() : FOCUSED")
                        showFocusFrame(preFocusFrameRect, IAutoFocusFrameDisplay.FocusFrameStatus.Focused, 1.0f) // いったん1秒だけ表示
                    } else {
                        // AF ERROR
                        Log.v(
                            TAG,
                            "lockAutoFocus() : ERROR"
                        )
                        showFocusFrame(
                            preFocusFrameRect,
                            IAutoFocusFrameDisplay.FocusFrameStatus.Failed,
                            1.0f
                        )
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    try
                    {
                        showFocusFrame(
                            preFocusFrameRect,
                            IAutoFocusFrameDisplay.FocusFrameStatus.Errored,
                            1.0f
                        )
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
     *
     *
     */
    fun unlockAutoFocus() {
        Log.v(TAG, "unlockAutoFocus()")
        try
        {
            val thread = Thread {
                try
                {
                    val result: String? = httpClient.httpPost(unlockAutoFocusUrl, "", timeoutMs)
                    if (result == null || result.isEmpty())
                    {
                        Log.v(
                            TAG,
                            "cancelTouchAFPosition() reply is null."
                        )
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
    private fun showFocusFrame(rect: RectF, status: IAutoFocusFrameDisplay.FocusFrameStatus, duration: Float)
    {
        frameDisplayer.showFocusFrame(rect, status, duration)
        indicator.onAfLockUpdate(IAutoFocusFrameDisplay.FocusFrameStatus.Focused === status)
    }

    /**
     *
     *
     */
    private fun hideFocusFrame() {
        frameDisplayer.hideFocusFrame()
        indicator.onAfLockUpdate(false)
    }

    /**
     *
     *
     */
    private fun getPreFocusFrameRect(point: PointF): RectF
    {
        val imageWidth: Float = frameDisplayer.getContentSizeWidth()
        val imageHeight: Float = frameDisplayer.getContentSizeHeight()

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
        //Log.v(TAG, " getPreFocusFrameRect ($point) --> [$focusWidth | $focusHeight] ($imageWidth,$imageHeight)")
        return RectF(
            point.x - focusWidth / 2.0f, point.y - focusHeight / 2.0f,
            point.x + focusWidth / 2.0f, point.y + focusHeight / 2.0f
        )
    }

    companion object {
        private val TAG = RicohGr2AutoFocusControl::class.java.simpleName

        /**
         *
         *
         */
        private fun findTouchAFPositionResult(replyString: String): Boolean
        {
            var afResult = false
            try
            {
                //Log.v(TAG, " findTouchAFPositionResult() : $replyString")
                val resultObject = JSONObject(replyString)
                val result = resultObject.getString("errMsg")
                try
                {
                    val focused = resultObject.getBoolean("focused")
                    if (result.contains("OK"))
                    {
                        afResult = focused
                        Log.v(TAG, "AF drive Result : $afResult")
                    }
                }
                catch (e : Exception)
                {
                    // GR3 は、 focusedを返してくれないので、
                    Log.v(TAG, " AF Result : $replyString")
                    afResult = result.contains("OK")
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
            return (afResult)
        }
    }

}
