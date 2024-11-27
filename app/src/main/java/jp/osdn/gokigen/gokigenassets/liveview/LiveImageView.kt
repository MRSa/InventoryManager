package jp.osdn.gokigen.gokigenassets.liveview

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.os.Looper
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IFocusingModeNotify
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_DRAWABLE_BACKGROUND_IMAGE
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.MAX_VALUE_SEEKBAR
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IFocusFrameDrawer
import jp.osdn.gokigen.gokigenassets.liveview.gridframe.GridFrameFactory
import jp.osdn.gokigen.gokigenassets.liveview.gridframe.IGridFrameDrawer
import jp.osdn.gokigen.gokigenassets.liveview.gridframe.IShowGridFrame
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider
import jp.osdn.gokigen.gokigenassets.liveview.message.IMessageDrawer
import jp.osdn.gokigen.gokigenassets.liveview.message.InformationDrawer
import java.util.*
import kotlin.math.min

class LiveImageView : View, ILiveView, ILiveViewRefresher, IShowGridFrame, OnSeekBarChangeListener, IFocusingModeNotify, IFocusFrameDrawer, IAutoFocusFrameDisplay, ICachePositionProvider, IImageRotation
{
    companion object
    {
        private val TAG = LiveImageView::class.java.simpleName
    }

    private val dummyRect = RectF(0.0f, 0.0f, 1.0f, 1.0f)
    private lateinit var orientationEventListener : CameraOrientationEventReceiver
    private var sliderPosition : Float = 0.0f
    private var imageRotationDegrees : Int = 0
    private var showGrid : Boolean = false
    private var gridColor : Int = Color.argb(130, 235, 235, 235)
    private var showingFocusFrame = false
    private val imageScaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER
    private lateinit var imageBitmap: Bitmap
    private var focusFrameStatus: IAutoFocusFrameDisplay.FocusFrameStatus = IAutoFocusFrameDisplay.FocusFrameStatus.None
    private lateinit var imageProvider : IImageProvider
    private lateinit var gridFrameDrawer : IGridFrameDrawer
    private lateinit var informationDrawer : InformationDrawer
    private lateinit var indicatorControl: IndicatorControl
    private var focusFrameRect: RectF? = null
    private var focusFrameHideTimer: Timer? = null
    private var isRotationImage = false
    private var anotherDrawer : IAnotherDrawer? = null
    private var overwriteDrawer : IAnotherDrawer? = null

    constructor(context: Context) : super(context)
    {
        initComponent(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        initComponent(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    {
        initComponent(context)
    }

    //constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    //{
    //    initComponent(context)
    //}

    private fun initComponent(context: Context)
    {
        gridFrameDrawer = GridFrameFactory().getGridFrameDrawer(0)
        //focusFrameDrawer = FocusFrameDrawer(context)
        informationDrawer = InformationDrawer(this)
        indicatorControl = IndicatorControl()
        imageBitmap = BitmapFactory.decodeResource(context.resources, ID_DRAWABLE_BACKGROUND_IMAGE)
        orientationEventListener = CameraOrientationEventReceiver(context)
        orientationEventListener.enable()
    }

    fun injectDisplay(cameraControl: ICameraControl)
    {
        isRotationImage = cameraControl.needRotateImage()
        cameraControl.getDisplayInjector()?.injectDisplay(this, indicatorControl, this)
    }

    override fun getMessageDrawer() : IMessageDrawer
    {
        return (informationDrawer)
    }

    override fun refresh()
    {
        refreshCanvas()
    }

    override fun setImageProvider(provider: IImageProvider)
    {
        this.imageProvider = provider
    }

    override fun setAnotherDrawer(drawer: IAnotherDrawer?, drawer2: IAnotherDrawer?)
    {
        Log.v(TAG, " setAnotherDrawer() ")
        this.anotherDrawer = drawer
        this.overwriteDrawer = drawer2
    }

    override fun updateImageRotation(degrees: Int)
    {
        this.imageRotationDegrees = degrees
        refreshCanvas()
    }

    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas)
        if (anotherDrawer != null)
        {
            // 別の描画ロジックを使う場合...
            anotherDrawer?.onDraw(canvas, dummyRect,0)
            return
        }
        if (!(::imageProvider.isInitialized))
        {
            Log.v(TAG, " ===== image provider is not ready. ==== ")
            return
        }

        var addDegrees = 0
        if (isRotationImage)
        {
            try
            {
                val config = context.resources.configuration
                if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
                {
                    addDegrees = if (orientationEventListener.isPositionIsClockWise()) { 90 } else { 270 }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }

        //Log.v(TAG, " ----- onDraw() ----- ")
        canvas.drawARGB(255, 0, 0, 0)
        val imageRectF = drawImage(canvas, addDegrees)
        if (showGrid)
        {
            gridFrameDrawer.drawFramingGrid(canvas, imageRectF, gridColor, addDegrees)
        }
        this.drawFocusFrame(canvas,imageRectF.width(), imageRectF.height())
        informationDrawer.drawInformationMessages(canvas, imageRectF)
        informationDrawer.drawLevelGauge(canvas, imageRotationDegrees)

        if (overwriteDrawer != null)
        {
            // 画面の上にデータを載せる場合...
            overwriteDrawer?.onDraw(canvas, imageRectF, addDegrees)
            return
        }
    }

    override fun showGridFrame(isShowGrid: Boolean, color : Int)
    {
        this.showGrid = isShowGrid
        gridColor = color
        refreshCanvas()
    }

    private fun drawImage(canvas: Canvas, addDegrees: Int) : RectF
    {
        val centerX = canvas.width / 2
        val centerY = canvas.height / 2

        val paint = Paint(Color.LTGRAY)
        paint.strokeWidth = 1.0f
        paint.style = Paint.Style.STROKE

        imageBitmap = imageProvider.getImage(sliderPosition)

        val degrees = imageRotationDegrees + addDegrees
        val viewRect = decideViewRect(canvas, imageBitmap, degrees)
        val width : Int = imageBitmap.width
        val height : Int = imageBitmap.height
        val imageRect = Rect(0, 0, width, height)

        //Log.v(TAG, " canvas:   ${canvas.width} x ${canvas.height} (D: ${rotationDegrees}) ")
        //Log.v(TAG, " bitmap:   ${imageBitmap.width} x ${imageBitmap.height} (D: ${rotationDegrees}) ")
        //Log.v(TAG, " imageRect: [${imageRect.left},${imageRect.top}]-[${imageRect.right},${imageRect.bottom}] ")
        //Log.v(TAG, " viewRect: [${viewRect.left},${viewRect.top}]-[${viewRect.right},${viewRect.bottom}] ")

        canvas.rotate(degrees.toFloat(), centerX.toFloat(), centerY.toFloat())
        canvas.drawBitmap(imageBitmap, imageRect, viewRect, paint)
        canvas.rotate(-degrees.toFloat(), centerX.toFloat(), centerY.toFloat())

        return (viewRect)
    }

    private fun refreshCanvas()
    {
        //Log.v(TAG, " refreshCanvas()")
        if (Looper.getMainLooper().thread === Thread.currentThread())
        {
            invalidate()
        }
        else
        {
            postInvalidate()
        }
    }

    private fun decideViewRect(canvas: Canvas, bitmapToShow: Bitmap?, rotationDegrees: Int): RectF
    {
        if (bitmapToShow == null)
        {
            return (RectF(0.0f, 0.0f, 1.0f, 1.0f))
        }
        val srcWidth: Int
        val srcHeight: Int
        if ((rotationDegrees == 0)||(rotationDegrees == 180))
        {
            srcWidth = bitmapToShow.width
            srcHeight = bitmapToShow.height
        }
        else
        {
            srcWidth = bitmapToShow.height
            srcHeight = bitmapToShow.width
        }
        val maxWidth = canvas.width
        val maxHeight = canvas.height
        val centerX = canvas.width / 2
        val centerY = canvas.height / 2
        val widthRatio = maxWidth / srcWidth.toFloat()
        val heightRatio = maxHeight / srcHeight.toFloat()
        val smallRatio = min(widthRatio, heightRatio)
        val dstWidth: Float
        val dstHeight: Float
        if (widthRatio < heightRatio)
        {
            dstWidth = maxWidth.toFloat()
            dstHeight = (smallRatio * srcHeight)
        }
        else
        {
            dstHeight = maxHeight.toFloat()
            dstWidth = (smallRatio * srcWidth)
        }
        val halfWidth = dstWidth * 0.5f
        val halfHeight = dstHeight * 0.5f
        return (if (rotationDegrees == 0 || rotationDegrees == 180)
        {
            RectF(
                (centerX - halfWidth),
                (centerY - halfHeight),
                ((centerX - halfWidth) + dstWidth),
                ((centerY - halfHeight) + dstHeight)
            )
        }
        else
        {
            RectF(
                (centerX - halfHeight),
                (centerY - halfWidth),
                ((centerX - halfHeight) + dstHeight),
                ((centerY - halfWidth) + dstWidth)
            )
        })
    }

    override fun getCachePosition(): Float
    {
        //Log.v(TAG, " ----- sliderPosition : $sliderPosition")
        return (sliderPosition)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
    {
        Log.v(TAG, " onProgressChanged() Progress: $progress (fromUser:$fromUser)")
        sliderPosition = (((progress).toFloat()) / ((MAX_VALUE_SEEKBAR).toFloat()))
        refreshCanvas()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?)
    {
        Log.v(TAG, " onStartTrackingTouch() ")
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?)
    {
        Log.v(TAG, " onStopTrackingTouch() ")
    }

    override fun changedFocusingMode()
    {
        Log.v(TAG, " changedFocusingMode()")
    }

    override fun drawFocusFrame(canvas: Canvas, imageWidth : Float, imageHeight : Float)
    {
        if (!showingFocusFrame)
        {
            // フォーカスマークは表示しないので、描画しない
            return
        }

        val focusRectOnImage: RectF = convertRectOnViewfinderIntoLiveImage(focusFrameRect, imageWidth, imageHeight, imageRotationDegrees)
        val focusRectOnView: RectF = convertRectFromImageArea(focusRectOnImage)

        // Draw a rectangle to the canvas.
        // Draw a rectangle to the canvas.
        val focusFramePaint = Paint()
        focusFramePaint.style = Paint.Style.STROKE
        when (focusFrameStatus)
        {
            IAutoFocusFrameDisplay.FocusFrameStatus.Running -> focusFramePaint.color = Color.WHITE
            IAutoFocusFrameDisplay.FocusFrameStatus.Focused -> focusFramePaint.color = Color.GREEN
            IAutoFocusFrameDisplay.FocusFrameStatus.Failed -> focusFramePaint.color = Color.RED
            IAutoFocusFrameDisplay.FocusFrameStatus.Errored -> focusFramePaint.color = Color.YELLOW
            else -> focusFramePaint.color = Color.BLACK
        }
        val focusFrameStrokeWidth = 2.0f
        val dm: DisplayMetrics = context.resources.displayMetrics
        val strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, focusFrameStrokeWidth, dm)
        focusFramePaint.strokeWidth = strokeWidth

        canvas.drawRect(focusRectOnView, focusFramePaint)
    }

    override fun getContentSizeWidth(): Float
    {
        return (imageBitmap.width).toFloat()
    }

    override fun getContentSizeHeight(): Float
    {
        return (imageBitmap.height).toFloat()
    }

    override fun getPointWithEvent(event: MotionEvent?): PointF?
    {
        if (event == null)
        {
            return null
        }

        val pointOnView = PointF(event.x - x, event.y - y) // Viewの表示位置に補正
        val pointOnImage: PointF = convertPointFromViewArea(pointOnView)
        val imageWidth: Float
        val imageHeight: Float
        if (imageRotationDegrees == 0 || imageRotationDegrees == 180)
        {
            imageWidth = imageBitmap.width.toFloat()
            imageHeight = imageBitmap.height.toFloat()
        }
        else
        {
            imageWidth = imageBitmap.height.toFloat()
            imageHeight = imageBitmap.width.toFloat()
        }
        return convertPointOnLiveImageIntoViewfinder(
            pointOnImage,
            imageWidth,
            imageHeight,
            imageRotationDegrees
        )
    }

    override fun isContainsPoint(point: PointF?): Boolean
    {
        return point != null && RectF(0.0f, 0.0f, 1.0f, 1.0f).contains(point.x, point.y)
    }

    override fun showFocusFrame(rect : RectF?, status : IAutoFocusFrameDisplay.FocusFrameStatus, duration : Float)
    {
        try
        {
            if (focusFrameHideTimer != null) {
                focusFrameHideTimer?.cancel()
                focusFrameHideTimer = null
            }

            showingFocusFrame = true
            focusFrameStatus = status
            focusFrameRect = rect

            refreshCanvas()

            if (duration > 0) {
                focusFrameHideTimer = Timer()
                focusFrameHideTimer?.schedule(object : TimerTask() {
                    override fun run() {
                        hideFocusFrame()
                    }
                }, (duration * 1000).toLong())
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun hideFocusFrame()
    {
        try
        {
            if (focusFrameHideTimer != null)
            {
                focusFrameHideTimer?.cancel()
                focusFrameHideTimer = null
            }
            showingFocusFrame = false
            refreshCanvas()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun convertRectOnViewfinderIntoLiveImage(rect: RectF?, width: Float, height: Float, rotatedDegrees: Int): RectF
    {
        var top = 0.0f
        var bottom = 1.0f
        var left = 0.0f
        var right = 1.0f
        try
        {
            if (rect != null)
            {
                if (rotatedDegrees == 0 || rotatedDegrees == 180)
                {
                    top = rect.top * height
                    bottom = rect.bottom * height
                    left = rect.left * width
                    right = rect.right * width
                }
                else
                {
                    left = rect.top * height
                    right = rect.bottom * height
                    top = rect.left * width
                    bottom = rect.right * width
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        //Log.v(TAG, " [$left,$top]-[$right,$bottom]")
        return RectF(left, top, right, bottom)
    }

    private fun convertRectFromImageArea(rect: RectF): RectF
    {
        val imageTopLeft = PointF(rect.left, rect.top)
        val imageBottomRight = PointF(rect.right, rect.bottom)
        val viewTopLeft: PointF = convertPointFromImageArea(imageTopLeft)
        val viewBottomRight: PointF = convertPointFromImageArea(imageBottomRight)
        return RectF(viewTopLeft.x, viewTopLeft.y, viewBottomRight.x, viewBottomRight.y)
    }

    private fun convertPointFromImageArea(point: PointF): PointF
    {
        var viewPointX = point.x
        var viewPointY = point.y
        val imageSizeWidth: Float
        val imageSizeHeight: Float
        if (imageRotationDegrees == 0 || imageRotationDegrees == 180)
        {
            imageSizeWidth = (imageBitmap.width).toFloat()
            imageSizeHeight = (imageBitmap.height).toFloat()
        }
        else
        {
            imageSizeWidth = imageBitmap.height.toFloat()
            imageSizeHeight = imageBitmap.width.toFloat()
        }
        val viewSizeWidth: Float = this.width.toFloat()
        val viewSizeHeight: Float = this.height.toFloat()
        val ratioX = viewSizeWidth / imageSizeWidth
        val ratioY = viewSizeHeight / imageSizeHeight
        val scale: Float
        when (imageScaleType)
        {
            ImageView.ScaleType.FIT_XY -> {
                viewPointX *= ratioX
                viewPointY *= ratioY
            }
            ImageView.ScaleType.FIT_CENTER, ImageView.ScaleType.CENTER_INSIDE -> {
                scale = ratioX.coerceAtMost(ratioY)
                //viewPointX *= scale
                //viewPointY *= scale
                viewPointX += (viewSizeWidth - imageSizeWidth * scale)  / 2.0f
                viewPointY += (viewSizeHeight - imageSizeHeight * scale) / 2.0f
            }
            ImageView.ScaleType.CENTER_CROP -> {
                scale = ratioX.coerceAtLeast(ratioY)
                viewPointX *= scale
                viewPointY *= scale
                viewPointX += (viewSizeWidth - imageSizeWidth * scale) / 2.0f
                viewPointY += (viewSizeHeight - imageSizeHeight * scale) / 2.0f
            }
            ImageView.ScaleType.CENTER -> {
                viewPointX += (viewSizeWidth / 2.0f - imageSizeWidth / 2.0f)
                viewPointY += (viewSizeHeight / 2.0f - imageSizeHeight / 2.0f)
            }
            else -> {
            }
        }
        // Log.v(TAG, "(viewPointX : $viewPointX, viewPointY : $viewPointY) : $imageSizeWidth x $imageSizeHeight")
        return PointF(viewPointX, viewPointY)
    }

    /**
     * 　　ライブビュー座標系の点座標をビューファインダー座標系の点座標に変換
     *
     */
    private fun convertPointOnLiveImageIntoViewfinder(point: PointF, width: Float, height: Float, rotatedDegrees: Int): PointF
    {
        var viewFinderPointX = 0.5f
        var viewFinderPointY = 0.5f
        try
        {
            if (rotatedDegrees == 0 || rotatedDegrees == 180) {
                viewFinderPointX = point.x / width
                viewFinderPointY = point.y / height
            } else {
                viewFinderPointX = point.y / width
                viewFinderPointY = point.x / height
            }
        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
        }
        return PointF(viewFinderPointX, viewFinderPointY)
    }

    /**
     * Converts a point on view area to a point on image area.
     *
     * @param point A point on view area. (e.g. a touch panel view)
     * @return A point on image area. (e.g. a live preview image)
     */
    private fun convertPointFromViewArea(point: PointF): PointF
    {
        var imagePointX = point.x
        var imagePointY = point.y
        val imageSizeWidth: Float
        val imageSizeHeight: Float
        if (imageRotationDegrees == 0 || imageRotationDegrees == 180) {
            imageSizeWidth = imageBitmap.width.toFloat()
            imageSizeHeight = imageBitmap.height.toFloat()
        } else {
            imageSizeWidth = imageBitmap.height.toFloat()
            imageSizeHeight = imageBitmap.width.toFloat()
        }
        val viewSizeWidth = this.width.toFloat()
        val viewSizeHeight = this.height.toFloat()
        val ratioX = viewSizeWidth / imageSizeWidth
        val ratioY = viewSizeHeight / imageSizeHeight
        val scale: Float // = 1.0f;
        when (imageScaleType) {
            ImageView.ScaleType.FIT_XY -> {
                imagePointX /= ratioX
                imagePointY /= ratioY
            }
            ImageView.ScaleType.FIT_CENTER, ImageView.ScaleType.CENTER_INSIDE -> {
                scale = ratioX.coerceAtMost(ratioY)
                imagePointX -= (viewSizeWidth - imageSizeWidth * scale) / 2.0f
                imagePointY -= (viewSizeHeight - imageSizeHeight * scale) / 2.0f
                imagePointX /= scale
                imagePointY /= scale
            }
            ImageView.ScaleType.CENTER_CROP -> {
                scale = ratioX.coerceAtLeast(ratioY)
                imagePointX -= (viewSizeWidth - imageSizeWidth * scale) / 2.0f
                imagePointY -= (viewSizeHeight - imageSizeHeight * scale) / 2.0f
                imagePointX /= scale
                imagePointY /= scale
            }
            ImageView.ScaleType.CENTER -> {
                imagePointX -= (viewSizeWidth - imageSizeWidth) / 2.0f
                imagePointY -= (viewSizeHeight - imageSizeHeight) / 2.0f
            }
            else -> {
            }
        }
        return PointF(imagePointX, imagePointY)
    }
}
