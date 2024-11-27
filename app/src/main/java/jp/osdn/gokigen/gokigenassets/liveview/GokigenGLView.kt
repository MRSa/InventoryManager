package jp.osdn.gokigen.gokigenassets.liveview

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import jp.osdn.gokigen.gokigenassets.liveview.glrenderer.EquirectangularDrawer
import jp.osdn.gokigen.gokigenassets.liveview.glrenderer.GokigenGLRenderer
import jp.osdn.gokigen.gokigenassets.liveview.glrenderer.IGraphicsDrawer
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider
import jp.osdn.gokigen.gokigenassets.liveview.message.IMessageDrawer


class GokigenGLView : GLSurfaceView, ILiveViewRefresher, ILiveView, IMessageDrawer
{
    private var graphicsDrawer : IGraphicsDrawer = EquirectangularDrawer(context)
    private lateinit var imageProvider : IImageProvider
    private var anotherDrawer : IAnotherDrawer? = null
    private var overwriteDrawer : IAnotherDrawer? = null

    companion object
    {
        private val TAG = toString()
    }

    constructor(context: Context) : super(context)
    {
        initializeSelf(context, null)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        initializeSelf(context, attrs)
    }

    fun resetView()
    {
        try
        {
            graphicsDrawer.resetView()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    fun moveView(x : Float, y : Float)
    {
        try
        {
            graphicsDrawer.setViewMove(y, x, 0.0f)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    fun setScaleFactor(scaleFactor : Float)
    {
        try
        {
            Log.v(TAG, " scaleFactor : $scaleFactor")
            graphicsDrawer.setScaleFactor(scaleFactor)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * クラスの初期化処理...レンダラを設定する
     *
     */
    private fun initializeSelf(context: Context, attrs: AttributeSet?)
    {
        try
        {
            //setEGLConfigChooser(false);        // これだと画面透過はダメ！
            setEGLContextClientVersion(1)
            super.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
            //setEGLConfigChooser(5,6,5, 8, 16, 0);
            isFocusable = true
            isFocusableInTouchMode = true

            // レンダラを設定する
            val renderer = GokigenGLRenderer(context, graphicsDrawer)
            setRenderer(renderer)

            // 画面を透過させる
            holder.setFormat(PixelFormat.TRANSLUCENT )
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun refresh()
    {
        if (Looper.getMainLooper().thread === Thread.currentThread())
        {
            invalidate()
        }
        else
        {
            postInvalidate()
        }
    }

    override fun setImageProvider(provider: IImageProvider)
    {
        try
        {
            imageProvider = provider
            graphicsDrawer.setImageProvider(provider)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun setAnotherDrawer(drawer: IAnotherDrawer?, drawer2: IAnotherDrawer?)
    {
        Log.v(TAG, " setAnotherDrawer() ")
        this.anotherDrawer = drawer
        this.overwriteDrawer = drawer2
    }
/*
    override fun updateImageRotation(degrees: Int)
    {
        //TODO("Not yet implemented")
    }
*/
    override fun getMessageDrawer(): IMessageDrawer
    {
        return (this)
    }

    override fun setMessageToShow(message: String, area: IMessageDrawer.MessageArea, color: Int, size: Int)
    {
        //TODO("Not yet implemented")
    }

    override fun setLevelToShow(value: Float, area: IMessageDrawer.LevelArea)
    {
        //TODO("Not yet implemented")
    }

}