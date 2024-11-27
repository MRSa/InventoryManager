package jp.osdn.gokigen.gokigenassets.camera.console

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ScaleGestureDetectorCompat
import jp.osdn.gokigen.gokigenassets.camera.preference.ICameraPreferenceProvider
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.AE
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.APERTURE
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.BATTERY
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.CAPTURE_MODE
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.EFFECT
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.EXPREV
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.FOCAL_LENGTH
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.FOCUS_STATUS
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.ISO_SENSITIVITY
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.REMAIN_SHOTS
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.SHUTTER_SPEED
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.TAKE_MODE
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.TORCH_MODE
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus.Companion.WHITE_BALANCE
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status.ICaptureModeReceiver
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_ARRAY_CAMERA_METHOD
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_CAMERAX
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_OMDS
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_PANASONIC
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_PENTAX
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_PIXPRO
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_SONY
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_THETA
import jp.osdn.gokigen.gokigenassets.liveview.*
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.gokigenassets.scene.IVibrator

class ConsolePanelControl (private val context: AppCompatActivity, private val vibrator : IVibrator, informationNotify: IInformationReceiver, private val preference: ICameraPreferenceProvider, private val number : Int = 0) : IDisplayInjector,
    ILiveViewController, ICameraControl, View.OnClickListener, View.OnLongClickListener, ICaptureModeReceiver, ICameraShutter, IKeyDown, IAnotherDrawer, View.OnTouchListener, ICameraStatus, IDetectPositionReceiver, IZoomLensControl
{
    private val gestureListener = ConsolePanelGestureListener(this)
    private val gestureDetector = GestureDetectorCompat(context, gestureListener)
    private val scaleGestureDetector = ScaleGestureDetector(context, gestureListener)
    private val statusItemSelector = StatusItemSelector(context, vibrator)

    private lateinit var refresher: ILiveViewRefresher

    private var isRefreshLoop = false
    private var currentCameraControlId : Int = -1
    private var currentCameraControl : ICameraControl? = null

    private var camera0: ICameraControl? = null
    private var camera1: ICameraControl? = null
    private var camera2: ICameraControl? = null
    private var camera3: ICameraControl? = null
    private var camera4: ICameraControl? = null
    private var camera5: ICameraControl? = null
    private var camera6: ICameraControl? = null
    private var camera7: ICameraControl? = null

    private var canvasWidth : Float = 0.0f
    private var canvasHeight : Float = 0.0f
    private var touchedX : Float = -1.0f
    private var touchedY : Float = -1.0f

    companion object
    {
        private val TAG = ConsolePanelControl::class.java.simpleName
        private const val MAX_CONTROL_CAMERAS = 8
        private const val MARGIN = 10.0f
        private const val sleepMs = 1000L
        private const val NOF_AREA_HORIZONTAL = 3.0f
        private const val NOF_AREA_VERTICAL = 9.0f
        private const val RADIUS = 5.0f
        private const val showTouchedPosition = false
    }

    init
    {
        try
        {
            currentCameraControlId = preference.getCameraOption1().toInt() - 1
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            currentCameraControlId = -1
        }
        ScaleGestureDetectorCompat.setQuickScaleEnabled(scaleGestureDetector, true)
        gestureDetector.setIsLongpressEnabled(true)
    }

    override fun getConnectionMethod(): String { return ("CONSOLE") }
    override fun startLiveView(isCameraScreen : Boolean) { }
    override fun stopLiveView() { }
    override fun connectToCamera() { }
    override fun startCamera(isPreviewView: Boolean, cameraSequence: Int) { }
    override fun finishCamera(isPowerOff: Boolean) { }
    override fun changeCaptureMode(mode: String) { }
    override fun needRotateImage(): Boolean { return (false) }
    override fun captureButtonReceiver(id: Int): View.OnClickListener { return (this) }
    override fun onLongClickReceiver(id: Int): View.OnLongClickListener { return (this) }
    override fun keyDownReceiver(id: Int): IKeyDown { return (this) }
    override fun getFocusingControl(id: Int): IFocusingControl? { return (null) }
    override fun getDisplayInjector(): IDisplayInjector { return (this) }
    override fun changedCaptureMode(captureMode: String) { }
    override fun doShutter() { }
    override fun doShutterOff() { }
    override fun getAnotherTouchListener(id : Int) : View.OnTouchListener { return (this) }
    override fun getCameraNumber(): Int { return (number) }
    override fun getCameraShutter(id: Int): ICameraShutter { return (this) }
    override fun getZoomControl(id: Int): IZoomLensControl { return (this) }

    override fun setNeighborCameraControl(index: Int, camera0: ICameraControl?, camera1: ICameraControl?, camera2: ICameraControl?, camera3: ICameraControl?)
    {
        Log.v(TAG, " setNeighborCameraControl($index) ")
        if (index == 0)
        {
            this.camera0 = camera0
            this.camera1 = camera1
            this.camera2 = camera2
            this.camera3 = camera3
        }
        else  // if (index == 1)
        {
            this.camera4 = camera0
            this.camera5 = camera1
            this.camera6 = camera2
            this.camera7 = camera3
        }
    }

    override fun setNeighborCameraControlFinished()
    {
        Log.v(TAG, " setNeighborCameraControlFinished() ")
        decideCameraControl()
    }

    override fun getCameraStatus(): ICameraStatus
    {
        return (this)
    }

    override fun injectDisplay(frameDisplayer: IAutoFocusFrameDisplay, indicator: IIndicatorControl, focusingModeNotify: IFocusingModeNotify)
    {
        try
        {

        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun initialize()
    {
        try
        {

        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun setRefresher(id: Int, refresher: ILiveViewRefresher, imageView: ILiveView, cachePosition: ICachePositionProvider)
    {
        try
        {
            this.refresher = refresher
            imageView.setAnotherDrawer(this, null)
            startConsoleRefresh()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?)
    {
        try
        {

        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun decideCameraControl()
    {
        try
        {
            var count = 0
            do
            {
                currentCameraControlId++
                if (currentCameraControlId >= MAX_CONTROL_CAMERAS)
                {
                    currentCameraControlId = 0
                }
                if (isAvailableCameraControl(currentCameraControlId))
                {
                    setCurrentCameraControl(currentCameraControlId)
                    break   // return (true)
                }
                count++
            } while (count <= MAX_CONTROL_CAMERAS)
            preference.getUpdater()?.setCameraOption1(currentCameraControlId.toString())
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        Log.v(TAG, " currentCameraControlId == $currentCameraControlId")
    }

    override fun onLongClick(v: View?): Boolean
    {
        try
        {

        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    override fun handleKeyDown(keyCode: Int, event: KeyEvent): Boolean
    {
        try
        {

        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean
    {
        //Log.v(TAG, " onTouch")
        try
        {
            var ret = false
            if (event != null)
            {
                if (event.pointerCount == 1)
                {
                    ret = gestureDetector.onTouchEvent(event)
                }
                else
                {
                    ret = scaleGestureDetector.onTouchEvent(event)
                }
            }

            // 表示を更新する
            if (::refresher.isInitialized)
            {
                refresher.refresh()
            }
            return (ret)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    private fun changeControlPanel()
    {
        try
        {
            decideCameraControl()
            vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_LONG)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

/*
    private fun drawPanelRect(canvas: Canvas)
    {
        try
        {
            val paint = Paint()
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.isAntiAlias = true
            paint.strokeWidth = 5.0f
            val rect = RectF(0.0f, 0.0f, canvas.width.toFloat(), canvas.height.toFloat())
            canvas.drawRect(rect, paint)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
*/

    override fun onDraw(canvas: Canvas?, imageRectF: RectF, rotationDegrees: Int)
    {
        if (canvas == null)
        {
            return
        }

        try
        {
            //Log.v(TAG, " onDraw")
            canvas.drawARGB(255, 0, 0, 0)

            // エリアの大きさを設定
            canvasWidth = canvas.width  / NOF_AREA_HORIZONTAL
            canvasHeight = canvas.height  / NOF_AREA_VERTICAL

            //
            //drawPanelRect(canvas)

            //　
            drawControlPanelNumber(canvas)

            val currentCameraStatus = currentCameraControl?.getCameraStatus()
            if (currentCameraStatus != null)
            {
                drawProgramMode(canvas, currentCameraStatus)
                drawShutterSpeed(canvas, currentCameraStatus)
                drawAperture(canvas, currentCameraStatus)
                drawExpRev(canvas, currentCameraStatus)
                drawCaptureMode(canvas, currentCameraStatus)
                drawIsoSensitivity(canvas, currentCameraStatus)
                drawWhiteBalance(canvas, currentCameraStatus)
                drawMeteringMode(canvas, currentCameraStatus)
                drawPictureEffect(canvas, currentCameraStatus)
                drawFocusStatus(canvas, currentCameraStatus)
                drawTorchMode(canvas, currentCameraStatus)
                drawFocalLength(canvas, currentCameraStatus)
                drawRemainShotNumber(canvas, currentCameraStatus)
                drawBatteryLevel(canvas, currentCameraStatus)
            }
            drawFramingGrid(canvas)

            if (showTouchedPosition)
            {
                drawTouchedPosition(canvas)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawProgramMode(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : 0,0
            val rect = RectF(canvasWidth * 0.0f, canvasHeight * 0.0f, canvasWidth * 1.0f,canvasHeight * 2.0f)
            val msg = currentCameraStatus.getStatus(TAKE_MODE)
            val color = currentCameraStatus.getStatusColor(TAKE_MODE)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawShutterSpeed(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : 1,0
            val rect = RectF(canvasWidth * 1.0f, canvasHeight * 0.0f, canvasWidth * 2.0f,canvasHeight * 2.0f)
            val msg = currentCameraStatus.getStatus(SHUTTER_SPEED)
            val color = currentCameraStatus.getStatusColor(SHUTTER_SPEED)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawAperture(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : 2,0
            val rect = RectF(canvasWidth * 2.0f, canvasHeight * 0.0f, canvasWidth * 3.0f,canvasHeight * 2.0f)
            val msg = currentCameraStatus.getStatus(APERTURE)
            val color = currentCameraStatus.getStatusColor(APERTURE)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawIsoSensitivity(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : 0,1
            val rect = RectF(canvasWidth * 0.0f, canvasHeight * 2.0f, canvasWidth * 1.0f,canvasHeight * 4.0f)
            val msg = currentCameraStatus.getStatus(ISO_SENSITIVITY)
            val color = currentCameraStatus.getStatusColor(ISO_SENSITIVITY)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawExpRev(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : 1,1
            val rect = RectF(canvasWidth * 1.0f, canvasHeight * 2.0f, canvasWidth * 2.0f,canvasHeight * 4.0f)
            val msg = currentCameraStatus.getStatus(EXPREV)
            val color = currentCameraStatus.getStatusColor(EXPREV)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawMeteringMode(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : 2,1
            val rect = RectF(canvasWidth * 2.0f, canvasHeight * 2.0f, canvasWidth * 3.0f,canvasHeight * 4.0f)
            val msg = currentCameraStatus.getStatus(AE)
            val color = currentCameraStatus.getStatusColor(AE)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawWhiteBalance(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : 0,2
            val rect = RectF(canvasWidth * 0.0f, canvasHeight * 4.0f, canvasWidth * 1.0f,canvasHeight * 6.0f)
            val msg = currentCameraStatus.getStatus(WHITE_BALANCE)
            val color = currentCameraStatus.getStatusColor(WHITE_BALANCE)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }


    private fun drawPictureEffect(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : 1,2
            val rect = RectF(canvasWidth * 1.0f, canvasHeight * 4.0f, canvasWidth * 2.0f,canvasHeight * 6.0f)
            val msg = currentCameraStatus.getStatus(EFFECT)
            val color = currentCameraStatus.getStatusColor(EFFECT)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawCaptureMode(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : 2,2
            val rect = RectF(canvasWidth * 2.0f, canvasHeight * 4.0f, canvasWidth * 3.0f,canvasHeight * 6.0f)
            val msg = currentCameraStatus.getStatus(CAPTURE_MODE)
            val color = currentCameraStatus.getStatusColor(CAPTURE_MODE)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawControlPanelNumber(canvas: Canvas)
    {
        try
        {
            //  area : bottom-left
            var methodName = ""
            val rect = RectF(canvasWidth * 0.0f, canvasHeight * 8.0f, canvasWidth * 1.0f,canvasHeight * 9.0f)
            val method = currentCameraControl?.getConnectionMethod()
            if (method != null)
            {
                methodName = getConnectionMethodName(method)
            }
            drawString(canvas, rect, "${currentCameraControlId + 1}: $methodName", Color.WHITE)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun getConnectionMethodName(method : String) : String
    {
        try
        {
            val index = context.resources.getStringArray(ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE).indexOf(method)
            if (index >= 0)
            {
                return (context.resources.getStringArray(ID_PREFERENCE_ARRAY_CAMERA_METHOD)[index])
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (method)
    }

    private fun drawFocusStatus(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : bottom-left UP
            val rect = RectF(canvasWidth * 0.0f, canvasHeight * 6.0f, canvasWidth * 1.0f, canvasHeight * 7.0f)
            val msg = currentCameraStatus.getStatus(FOCUS_STATUS)
            val color = currentCameraStatus.getStatusColor(FOCUS_STATUS)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawFocalLength(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : bottom-left UP
            val rect = RectF(canvasWidth * 0.0f, canvasHeight * 7.0f, canvasWidth * 1.0f, canvasHeight * 8.0f)
            val msg = currentCameraStatus.getStatus(FOCAL_LENGTH)
            val color = currentCameraStatus.getStatusColor(FOCAL_LENGTH)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawTorchMode(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : bottom-right UP
            val rect = RectF(canvasWidth * 2.0f, canvasHeight * 6.0f, canvas.width.toFloat(), canvasHeight * 7.0f)
            val msg = currentCameraStatus.getStatus(TORCH_MODE)
            val color = currentCameraStatus.getStatusColor(TORCH_MODE)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawRemainShotNumber(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : bottom-right UP
            val rect = RectF(canvasWidth * 2.0f, canvasHeight * 7.0f, canvas.width.toFloat(), canvasHeight * 8.0f)
            val msg = currentCameraStatus.getStatus(REMAIN_SHOTS)
            val color = currentCameraStatus.getStatusColor(REMAIN_SHOTS)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun drawBatteryLevel(canvas: Canvas, currentCameraStatus : ICameraStatus)
    {
        try
        {
            //  area : bottom-right DOWN
            val rect = RectF(canvasWidth * 2.0f, canvasHeight * 8.0f, canvas.width.toFloat(), canvas.height.toFloat())
            val msg = currentCameraStatus.getStatus(BATTERY)
            val color = currentCameraStatus.getStatusColor(BATTERY)
            drawString(canvas, rect, msg, color)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun isAvailableCameraControl(id : Int) : Boolean
    {
        var ret = false
        try
        {
            val cameraControl = when(id) {
                0 -> camera0
                1 -> camera1
                2 -> camera2
                3 -> camera3
                4 -> camera4
                5 -> camera5
                6 -> camera6
                7 -> camera7
                else -> null
            } ?: return (false)

            Log.v(TAG, " isAvailableCameraControl($id) : ${cameraControl.getConnectionMethod()}")

            ret = when (cameraControl.getConnectionMethod())
            {
                PREFERENCE_CAMERA_METHOD_CAMERAX -> true
                PREFERENCE_CAMERA_METHOD_PENTAX -> true
                PREFERENCE_CAMERA_METHOD_THETA -> true
                PREFERENCE_CAMERA_METHOD_PANASONIC -> true
                PREFERENCE_CAMERA_METHOD_SONY -> true
                PREFERENCE_CAMERA_METHOD_PIXPRO -> true
                PREFERENCE_CAMERA_METHOD_OMDS -> true
                else -> false
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (ret)
    }

    private fun setCurrentCameraControl(id : Int)
    {
        Log.v(TAG, "  setCurrentCameraControl($id)")
        currentCameraControl = when (id) {
            0 -> camera0
            1 -> camera1
            2 -> camera2
            3 -> camera3
            4 -> camera4
            5 -> camera5
            6 -> camera6
            7 -> camera7
            else -> null
        }
    }

    private fun drawFramingGrid(canvas: Canvas, color : Int = Color.BLACK)
    {

        val paint = Paint()
        canvas.width
        paint.color = color
        paint.strokeWidth = 3.0f
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        val rect = RectF(0.0f, 0.0f, canvas.width.toFloat(), canvas.height.toFloat())
        canvas.drawRect(rect, paint)
    }

    private fun drawTouchedPosition(canvas: Canvas, color : Int = Color.DKGRAY)
    {
        if ((touchedX >= 0.0f)&&(touchedY >= 0.0f))
        {
            val paint = Paint()
            canvas.width
            paint.color = color
            paint.strokeWidth = 3.0f
            paint.style = Paint.Style.STROKE
            paint.isAntiAlias = true
            canvas.drawCircle(touchedX, touchedY, RADIUS, paint)
        }
    }

    /**
     *   枠内に文字を（大きさを決めて）表示する
     *
     */
    private fun drawString(canvas: Canvas, region: RectF, target: String?, color: Int)
    {
        if ((target == null)||(target.isEmpty()))
        {
            return
        }
        val textPaint = Paint()
        textPaint.color = color
        textPaint.isAntiAlias = true

        val maxWidth: Float = region.width() - MARGIN
        var textSize: Float = region.height() - MARGIN
        textPaint.textSize = textSize
        var textWidth = textPaint.measureText(target)

        while (maxWidth < textWidth)
        {
            // テキストサイズが横幅からあふれるまでループ
            textPaint.textSize = --textSize
            textWidth = textPaint.measureText(target)
        }

        // センタリングするための幅を取得
        val margin = (region.width() - textWidth) / 2.0f

        // 文字を表示する
        val fontMetrics = textPaint.fontMetrics
        canvas.drawText(target, region.left + margin, region.bottom - fontMetrics.descent, textPaint)
    }

    private fun startConsoleRefresh()
    {
        try
        {
            val thread = Thread {
                try
                {
                    while (isRefreshLoop)
                    {
                        if (::refresher.isInitialized)
                        {
                            refresher.refresh()
                        }
                        Thread.sleep(sleepMs)
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            isRefreshLoop = true
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getStatusList(key: String): List<String?> { return (ArrayList<String>()) }
    override fun getStatus(key: String): String { return ("") }
    override fun getStatusColor(key: String): Int { return (Color.WHITE) }
    override fun setStatus(key: String, value: String) { }

    // IDetectPositionReceiver
    override fun onLongPress(positionX: Float, positionY: Float)
    {
        touchedX = positionX
        touchedY = positionY

        val widthPosition = (touchedX / canvasWidth).toInt()
        val heightPosition = (touchedY / canvasHeight).toInt()

        // Log.v(TAG, "   ----- POSITION : $widthPosition, $heightPosition")

        //  長押しした場所に合わせて処理を切り替える
        if ((widthPosition == 0)&&(heightPosition == 8))
        {
            // 制御パネルを切り替える
            changeControlPanel()
        }
    }

    override fun onSingleTapUp(positionX: Float, positionY: Float): Boolean
    {
        // 画面をタップしたとき、、設定値の変更を行う
        touchedX = positionX
        touchedY = positionY

        val widthPosition = (touchedX / canvasWidth).toInt()
        val heightPosition = (touchedY / canvasHeight).toInt()

        // Log.v(TAG, "   ----- POSITION : $widthPosition, $heightPosition")
        if (currentCameraControl == null)
        {
            return (false)
        }
        return (statusItemSelector.itemSelected(currentCameraControl, widthPosition, heightPosition))
    }

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
