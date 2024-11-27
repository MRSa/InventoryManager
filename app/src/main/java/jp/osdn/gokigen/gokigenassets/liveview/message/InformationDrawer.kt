package jp.osdn.gokigen.gokigenassets.liveview.message

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import jp.osdn.gokigen.gokigenassets.liveview.ILiveView
import jp.osdn.gokigen.gokigenassets.liveview.message.LevelHolder.Companion.LEVEL_GAUGE_THRESHOLD_MIDDLE
import jp.osdn.gokigen.gokigenassets.liveview.message.LevelHolder.Companion.LEVEL_GAUGE_THRESHOLD_OVER

class InformationDrawer(private val liveview: ILiveView?) : IMessageDrawer, IInformationDrawer
{
    private val topLeftMessage: MessageHolder = MessageHolder()
    private val topCenterMessage: MessageHolder = MessageHolder()
    private val topRightMessage: MessageHolder = MessageHolder()
    private val centerLeftMessage: MessageHolder = MessageHolder()
    private val centerMessage: MessageHolder = MessageHolder()
    private val centerRightMessage: MessageHolder = MessageHolder()
    private val bottomLeftMessage: MessageHolder = MessageHolder()
    private val bottomCenterMessage: MessageHolder = MessageHolder()
    private val bottomRightMessage: MessageHolder = MessageHolder()

    private val horizontalAngleLevel: LevelHolder = LevelHolder()
    private val verticalAngleLevel: LevelHolder = LevelHolder()

    override fun invalidate()
    {
        liveview?.invalidate()
    }

    override fun setMessageToShow(message: String, area: IMessageDrawer.MessageArea, color: Int, size: Int)
    {
        when (area)
        {
            IMessageDrawer.MessageArea.UPLEFT -> topLeftMessage.setValue(message, color, size)
            IMessageDrawer.MessageArea.UPRIGHT -> topRightMessage.setValue(message, color, size)
            IMessageDrawer.MessageArea.CENTER -> centerMessage.setValue(message, color, size)
            IMessageDrawer.MessageArea.LOWLEFT -> bottomLeftMessage.setValue(message, color, size)
            IMessageDrawer.MessageArea.LOWRIGHT -> bottomRightMessage.setValue(message, color, size)
            IMessageDrawer.MessageArea.UPCENTER -> topCenterMessage.setValue(message, color, size)
            IMessageDrawer.MessageArea.LOWCENTER -> bottomCenterMessage.setValue(message, color, size)
            IMessageDrawer.MessageArea.CENTERLEFT -> centerLeftMessage.setValue(message, color, size)
            IMessageDrawer.MessageArea.CENTERRIGHT -> centerRightMessage.setValue(message, color, size)
        }
    }

    override fun setLevelToShow(value: Float, area: IMessageDrawer.LevelArea)
    {
        when (area)
        {
            IMessageDrawer.LevelArea.LEVEL_HORIZONTAL -> horizontalAngleLevel.setAngleLevel(value)
            IMessageDrawer.LevelArea.LEVEL_VERTICAL -> verticalAngleLevel.setAngleLevel(value)
        }
    }

    private fun getLevelColor(value: Float): Int
    {
        val checkValue = Math.abs(value)
        if (checkValue < LEVEL_GAUGE_THRESHOLD_MIDDLE)
        {
            return (Color.GREEN)
        }
        return (if (checkValue > LEVEL_GAUGE_THRESHOLD_OVER) {
            (Color.RED)
        } else (Color.YELLOW))
    }

    override fun drawInformationMessages(canvas: Canvas, rect : RectF)
    {
        drawMessageCenter(canvas, rect)
        drawMessageCenterLeft(canvas, rect)
        drawMessageCenterRight(canvas, rect)
        drawMessageTopLeft(canvas, rect)
        drawMessageTopCenter(canvas, rect)
        drawMessageTopRight(canvas, rect)
        drawMessageBottomLeft(canvas, rect)
        drawMessageBottomCenter(canvas, rect)
        drawMessageBottomRight(canvas, rect)
    }

    override fun drawLevelGauge(canvas: Canvas, rotationDegrees: Int)
    {
        // 画像の傾きを表示する
        // (現状は表示しない...)

        // 垂直線 と 水平線 （枠外）
        //drawLevelGaugeHV(canvas, rotationDegrees)

        // 水平線 (スクリーン内の線)
        //drawLevelGaugeCenter(canvas, rotationDegrees)
    }

    private fun drawLevelGaugeHV(canvas: Canvas, rotationDegrees: Int)
    {
        val height = canvas.height
        val width = canvas.width
        val centerX = width / 2
        val centerY = height / 2
        val maxBandWidth = width / 3.0f    // ゲージの最大長 (画面の 1/3 ぐらい)
        val maxBandHeight = height / 3.0f  // ゲージの最大長 (画面の 1/3 ぐらい)
        val barWidthInitial = 5                  // 表示するゲージの幅（の初期値）
        var barWidth: Int                        // 実際に表示するゲージの幅
        val paint = Paint()

        // 垂直線
        val verticalValue: Float = verticalAngleLevel.getAngleLevel()
        var verticalSize = verticalValue / 60.0f * maxBandHeight // 45度で切り替わるはずだが、一応...

        if (Math.abs(verticalSize) < 1.0f)
        {
            // 線引き限界以下、水平検出とする (この時の線は倍の長さにする)
            verticalSize = 1.0f
            barWidth = barWidthInitial * 2
        }
        else
        {
            barWidth = barWidthInitial
        }
        paint.strokeWidth = barWidth.toFloat()
        paint.color = getLevelColor(verticalValue)
        canvas.drawLine((width - barWidth).toFloat(), centerY.toFloat(), (width - barWidth).toFloat(), centerY + verticalSize, paint)

        // 水平線
        val horizontalValue: Float = horizontalAngleLevel.getAngleLevel()
        var horizontalSize = horizontalValue / 60.0f * maxBandWidth // 45度ぐらいで切り替わるはずだが、一応...

        if (Math.abs(horizontalSize) < 1.0f)
        {
            // 線引き限界以下、水平検出とする (この時の線は倍の長さにする）
            horizontalSize = 1.0f
            barWidth = barWidthInitial * 2
        }
        else
        {
            barWidth = barWidthInitial
        }
        paint.strokeWidth = barWidth.toFloat()
        paint.color = getLevelColor(horizontalValue)
        canvas.drawLine(centerX.toFloat(), (height - barWidth).toFloat(), centerX + horizontalSize, (height - barWidth).toFloat(), paint)
    }

    private fun drawLevelGaugeCenter(canvas: Canvas, rotationDegrees: Int)
    {
        // 水平線 (スクリーン内の線)
        val horizontalValue0: Float = horizontalAngleLevel.getAngleLevel()
        val height = canvas.height
        val width = canvas.width
        val centerX = width / 2
        val centerY = height / 2

        val paint = Paint()
        paint.strokeWidth = 2.0f
        paint.isAntiAlias = true
        paint.color = getLevelColor(horizontalValue0)
        if (rotationDegrees == 0 || rotationDegrees == 180)
        {
            // 通常状態
            val YY = canvas.height / 2.0f // centerY
            val diffY = Math.sin(Math.toRadians(horizontalValue0.toDouble()))
                .toFloat() * centerX.toFloat()
            canvas.drawLine(0f, YY + diffY, width.toFloat(), YY - diffY, paint)
        }
        else
        {
            // 縦持ち状態
            val XX = canvas.width / 2.0f // centerX
            val diffX = Math.sin(Math.toRadians(horizontalValue0.toDouble())).toFloat() * centerY.toFloat()
            canvas.drawLine(XX + diffX, 0f, XX - diffX, canvas.height.toFloat(), paint)
        }
    }

    private fun drawMessageTopLeft(canvas: Canvas, rect : RectF)
    {
        val message = topLeftMessage.getMessage()
        if (message.isNotEmpty())
        {
            val paint = Paint()
            paint.color = topLeftMessage.getColor()
            paint.textSize = topLeftMessage.getSize().toFloat()
            paint.isAntiAlias = true
            val fontMetrics = paint.fontMetrics

            canvas.drawText(message, rect.left + 3.0f, rect.top + (fontMetrics.descent - fontMetrics.ascent), paint)
        }
    }

    private fun drawMessageTopCenter(canvas: Canvas, rect : RectF)
    {
        val message = topCenterMessage.getMessage()
        if (message.isNotEmpty())
        {
            val paint = Paint()
            paint.color = topCenterMessage.getColor()
            paint.textSize = topCenterMessage.getSize().toFloat()
            paint.isAntiAlias = true
            val fontMetrics = paint.fontMetrics

            val width = paint.measureText(message) / 2.0f
            canvas.drawText(message, (rect.centerX()) - width, rect.top + (fontMetrics.descent - fontMetrics.ascent), paint)
        }
    }

    private fun drawMessageTopRight(canvas: Canvas, rect : RectF)
    {
        val message = topRightMessage.getMessage()
        if (message.isNotEmpty())
        {
            val paint = Paint()
            paint.color = topRightMessage.getColor()
            paint.textSize = topRightMessage.getSize().toFloat()
            paint.isAntiAlias = true
            val fontMetrics = paint.fontMetrics

            val width = paint.measureText(message)
            canvas.drawText(message, (rect.right - 3.0f) - width, rect.top + (fontMetrics.descent - fontMetrics.ascent), paint)
        }
    }

    private fun drawMessageCenterLeft(canvas: Canvas, rect : RectF)
    {
        val message = centerLeftMessage.getMessage()
        if (message.isNotEmpty())
        {
            val paint = Paint()
            paint.color = centerLeftMessage.getColor()
            paint.textSize = centerLeftMessage.getSize().toFloat()
            paint.isAntiAlias = true
            val fontMetrics = paint.fontMetrics

            val cy = (canvas.height / 2.0f) - ((fontMetrics.ascent + fontMetrics.descent) / 2.0f)
            canvas.drawText(message, rect.left + 3.0f, cy, paint)
        }
    }

    private fun drawMessageCenter(canvas: Canvas, rect : RectF)
    {
        val message = centerMessage.getMessage()
        if (message.isNotEmpty())
        {
            val paint = Paint()
            paint.color = centerMessage.getColor()
            paint.textSize = centerMessage.getSize().toFloat()
            paint.isAntiAlias = true
            val fontMetrics = paint.fontMetrics

            val cx = (canvas.width / 2.0f) - (paint.measureText(message) / 2.0f)
            val cy = (canvas.height / 2.0f) - ((fontMetrics.ascent + fontMetrics.descent) / 2.0f)
            canvas.drawText(message, cx, cy, paint)
        }
    }

    private fun drawMessageCenterRight(canvas: Canvas, rect : RectF)
    {
        val message = centerRightMessage.getMessage()
        if (message.isNotEmpty())
        {
            val paint = Paint()
            paint.color = centerRightMessage.getColor()
            paint.textSize = centerRightMessage.getSize().toFloat()
            paint.isAntiAlias = true
            val fontMetrics = paint.fontMetrics

            val width = paint.measureText(message)
            val cy = (canvas.height / 2.0f) - ((fontMetrics.ascent + fontMetrics.descent) / 2.0f)
            canvas.drawText(message, (rect.right - 3.0f) - width, cy, paint)
        }
    }
    private fun drawMessageBottomLeft(canvas: Canvas, rect : RectF)
    {
        val message = bottomLeftMessage.getMessage()
        if (message.isNotEmpty())
        {
            val paint = Paint()
            paint.color = bottomLeftMessage.getColor()
            paint.textSize = bottomLeftMessage.getSize().toFloat()
            paint.isAntiAlias = true
            val fontMetrics = paint.fontMetrics

            canvas.drawText(message, rect.left + 3.0f, rect.bottom - fontMetrics.bottom, paint)
        }
    }

    private fun drawMessageBottomCenter(canvas: Canvas, rect : RectF)
    {
        val message = bottomCenterMessage.getMessage()
        if (message.isNotEmpty())
        {
            val paint = Paint()
            paint.color = bottomCenterMessage.getColor()
            paint.textSize = bottomCenterMessage.getSize().toFloat()
            paint.isAntiAlias = true
            val fontMetrics = paint.fontMetrics

            val width = paint.measureText(message) / 2.0f
            canvas.drawText(message, (rect.centerX()) - width, rect.bottom - fontMetrics.bottom, paint)
        }
    }

    private fun drawMessageBottomRight(canvas: Canvas, rect : RectF)
    {
        val message = bottomRightMessage.getMessage()
        if (message.isNotEmpty())
        {
            val paint = Paint()
            paint.color = bottomRightMessage.getColor()
            paint.textSize = bottomRightMessage.getSize().toFloat()
            paint.isAntiAlias = true
            val fontMetrics = paint.fontMetrics

            val width = paint.measureText(message)
            canvas.drawText(message, (rect.right - 3.0f) - width, rect.bottom - fontMetrics.bottom, paint)
        }
    }
}
