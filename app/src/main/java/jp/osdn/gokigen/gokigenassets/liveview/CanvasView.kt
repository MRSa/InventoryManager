package jp.osdn.gokigen.gokigenassets.liveview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import jp.osdn.gokigen.gokigenassets.liveview.message.IIndicator

class CanvasView : View
{
    private var showCameraStatus = false

    private val informationMessage = mutableMapOf<IIndicator.Area, String>()
    private val informationColor = mutableMapOf<IIndicator.Area, Int>()

    constructor(context: Context) : super(context)
    {
        initComponent()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    {
        initComponent()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    {
        initComponent()
    }

    //constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    //{
    //    initComponent()
    //}

    private fun initComponent()
    {
        Log.v(TAG, "initComponent")
    }

    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas)
        //Log.v(TAG, " ----- onDraw() ----- ")
        //canvas.drawARGB(255, 0, 0, 0)

        if (showCameraStatus)
        {
            drawInformationMessages(canvas)
        }
    }

    fun setMessage(area: IIndicator.Area, color: Int, message: String)
    {
        try
        {
            informationMessage[area] = message
            informationColor[area] = color
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun refresh()
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

    /**
     * 　 画面にメッセージを表示する
     */
    private fun drawInformationMessages(canvas: Canvas)
    {
        try
        {
            val paint = Paint()
            paint.color = Color.WHITE
            paint.textSize = 16.0f
            paint.isAntiAlias = true
            paint.setShadowLayer(5.0f, 3.0f, 3.0f, Color.BLACK)

            val fontMetrics = paint.fontMetrics
            val fontHeight = fontMetrics.bottom - fontMetrics.top + 4.0f

            var posX = 10.0f
            var posY = 20.0f
            var message = informationMessage[IIndicator.Area.AREA_1]
            var color = informationColor[IIndicator.Area.AREA_1]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_2]
            color = informationColor[IIndicator.Area.AREA_2]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_3]
            color = informationColor[IIndicator.Area.AREA_3]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_4]
            color = informationColor[IIndicator.Area.AREA_4]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_5]
            color = informationColor[IIndicator.Area.AREA_5]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_6]
            color = informationColor[IIndicator.Area.AREA_6]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_7]
            color = informationColor[IIndicator.Area.AREA_7]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_8]
            color = informationColor[IIndicator.Area.AREA_8]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_9]
            color = informationColor[IIndicator.Area.AREA_9]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_10]
            color = informationColor[IIndicator.Area.AREA_10]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_11]
            color = informationColor[IIndicator.Area.AREA_11]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_12]
            color = informationColor[IIndicator.Area.AREA_12]
            if ((message != null) && (color != null)) {
                paint.color = color
                canvas.drawText(message, posX, posY, paint)
            }

            //////////////////////////////////////////////////////////////////////////
            posY = 20.0f
            //posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_A]
            color = informationColor[IIndicator.Area.AREA_A]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_B]
            color = informationColor[IIndicator.Area.AREA_B]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_C]
            color = informationColor[IIndicator.Area.AREA_C]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_D]
            color = informationColor[IIndicator.Area.AREA_D]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_E]
            color = informationColor[IIndicator.Area.AREA_E]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_F]
            color = informationColor[IIndicator.Area.AREA_F]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_G]
            color = informationColor[IIndicator.Area.AREA_G]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_H]
            color = informationColor[IIndicator.Area.AREA_H]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_I]
            color = informationColor[IIndicator.Area.AREA_I]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }


            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_J]
            color = informationColor[IIndicator.Area.AREA_J]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_K]
            color = informationColor[IIndicator.Area.AREA_K]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_L]
            color = informationColor[IIndicator.Area.AREA_L]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_M]
            color = informationColor[IIndicator.Area.AREA_M]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_N]
            color = informationColor[IIndicator.Area.AREA_N]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_O]
            color = informationColor[IIndicator.Area.AREA_O]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_P]
            color = informationColor[IIndicator.Area.AREA_P]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }

            posY += fontHeight
            message = informationMessage[IIndicator.Area.AREA_Q]
            color = informationColor[IIndicator.Area.AREA_Q]
            if ((message != null) && (color != null)) {
                paint.color = color
                posX = canvas.width - 10.0f - paint.measureText(message)
                canvas.drawText(message, posX, posY, paint)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    //fun setShowCameraStatus(isEnable: Boolean)
    //{
    //    showCameraStatus = isEnable
    //}

    companion object {
        private val TAG = CanvasView::class.java.simpleName
    }
}
