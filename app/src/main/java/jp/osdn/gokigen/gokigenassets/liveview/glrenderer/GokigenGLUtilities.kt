package jp.osdn.gokigen.gokigenassets.liveview.glrenderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import java.io.IOException
import java.io.InputStream
import javax.microedition.khronos.opengles.GL10

class GokigenGLUtilities(private val mContext: Context)
{

    /**
     * テクスチャの準備
     *
     *
     */
    fun prepareTexture(gl: GL10?, resourceId: Int): Int
    {
        try
        {
            /*
             * Create our texture. This has to be done each time the
             * surface is created.
             */
            val textures = IntArray(1)
            gl?.glGenTextures(1, textures, 0)
            gl?.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])
            gl?.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat())
            gl?.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
            gl?.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE.toFloat())
            gl?.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE.toFloat())
            gl?.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE.toFloat())

            /////////////////////////////////////////////////////////////////////
            val inputStream: InputStream = mContext.getResources().openRawResource(resourceId)
            val bitmap: Bitmap
            bitmap = try
            {
                BitmapFactory.decodeStream(inputStream)
            }
            finally
            {
                try
                {
                    inputStream.close()
                }
                catch (e: IOException)
                {
                    // Ignore.
                    e.printStackTrace()
                }
            }
            try
            {
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
                bitmap.recycle()
            }
            catch (ex: Exception)
            {
                // ignore
                ex.printStackTrace()
            }
            return textures[0]
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return -1
        }
    }

    /**
     * テクスチャの準備(ビットマップ指定)
     *
     *
     */
    fun prepareTextureBitmap(gl: GL10?, bitmap: Bitmap): Int
    {
        try
        {
           /*
             * Create our texture. This has to be done each time the
             * surface is created.
             */
            val textures = IntArray(1)
            gl?.glGenTextures(1, textures, 0)
            gl?.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])
            gl?.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat())
            gl?.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
            gl?.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE.toFloat())
            gl?.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE.toFloat())
            gl?.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE.toFloat())

            try
            {
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
                //bitmap.recycle()
            }
            catch (ex: Exception)
            {
                // ignore
                ex.printStackTrace()
            }
            return textures[0]
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return -1
        }
    }

    fun updateTexture(gl: GL10?, bitmap: Bitmap)
    {
        try
        {
            GLUtils.texSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, bitmap)
        }
        catch (ex: Exception)
        {
            // ignore
            ex.printStackTrace()
        }



    }
}