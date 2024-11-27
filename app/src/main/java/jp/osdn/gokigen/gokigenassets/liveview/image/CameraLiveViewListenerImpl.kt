package jp.osdn.gokigen.gokigenassets.liveview.image

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.ImageFormat.NV21
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_DRAWABLE_SPLASH_IMAGE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CACHE_LIVE_VIEW_PICTURES
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_NUMBER_OF_CACHE_PICTURES
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_NUMBER_OF_CACHE_PICTURES_DEFAULT_VALUE
import jp.osdn.gokigen.gokigenassets.liveview.ILiveViewRefresher
import jp.osdn.gokigen.gokigenassets.liveview.bitmapconvert.IPreviewImageConverter
import jp.osdn.gokigen.gokigenassets.liveview.bitmapconvert.ImageConvertFactory
import jp.osdn.gokigen.gokigenassets.preference.PreferenceAccessWrapper
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import java.io.ByteArrayOutputStream
import java.util.*


data class MyImageByteArray(val imageData : ByteArray, val rotationDegrees: Int)

class CameraLiveViewListenerImpl(private val context: Context,  private val informationReceiver: IInformationReceiver, private val isDisableCache : Boolean = false) : IImageDataReceiver, IImageProvider, ImageAnalysis.Analyzer
{
    private var cachePics = ArrayList<MyImageByteArray>()
    private var isImageReceived = false
    private var maxCachePics : Int = 0
    private var cacheIsFull = false
    private var bitmapConverter : IPreviewImageConverter = ImageConvertFactory().getImageConverter(0)
    private val refresher = ArrayList<ILiveViewRefresher>()

    companion object
    {
        private val TAG = CameraLiveViewListenerImpl::class.java.simpleName
    }

    init
    {
        refresher.clear()
    }

    fun setRefresher(refresher: ILiveViewRefresher)
    {
        this.refresher.add(refresher)
        setupLiveviewCache()
    }


    override fun onUpdateLiveView(data: ByteArray, metadata: Map<String, Any>?, degrees : Int)
    {
        try
        {
            insertCache(data, degrees)
            isImageReceived = true
            refresh()
        }
        catch (t : Throwable)
        {
            t.printStackTrace()
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy)
    {
        try
        {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            isImageReceived = true

            if (imageProxy.image?.planes?.get(1)?.pixelStride == 1)
            {
                // from I420 format
                convertToBitmapI420(imageProxy, rotationDegrees)
                //Log.v(TAG, " convertToBitmapI420 $rotationDegrees ")
                return
            }
            if (imageProxy.format == ImageFormat.YUV_420_888)
            {
                if (imageProxy.image?.planes?.get(1)?.rowStride != imageProxy.image?.width)
                {
                    //  Format : NV12, YU12, YV12  YUV_420_888
                    convertToBitmapYUV420888(imageProxy, rotationDegrees)
                    //Log.v(TAG, " convertToBitmapYUV420888 $rotationDegrees ")
                    return
                }
            }
            convertToBitmapYUV420888NV21(imageProxy, rotationDegrees)
            //convertToBitmapYUV420888(imageProxy, rotationDegrees)
            //Log.v(TAG, " convertToBitmapYUV420888NV21 $rotationDegrees ")
        }
        catch (e: Throwable)
        {
            e.printStackTrace()
        }
    }

    private fun convertToBitmapI420(imageProxy: ImageProxy, rotationDegrees: Int)
    {
        //Log.v(TAG, " convertToBitmap(I420) $rotationDegrees ")

        //  ImageFormat.YUV_420_888 : 35
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        try
        {
            var orgIndex = 0
            var index = ySize
            while (index < (ySize + uSize + vSize))
            {
                nv21[index++] = imageProxy.planes[2].buffer.get(orgIndex)
                nv21[index++] = imageProxy.planes[1].buffer.get(orgIndex)
                orgIndex++
            }
        }
        catch (t : Throwable)
        {
            t.printStackTrace()
        }

        val width = imageProxy.width
        val height = imageProxy.height
        val yuvImage = YuvImage(nv21, NV21, width, height, null)

        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)

        insertCache(out.toByteArray(), rotationDegrees)
        imageProxy.close()
        refresh()
    }


    private fun convertToBitmapYUV420888(imageProxy: ImageProxy, rotationDegrees: Int)
    {
        //Log.v(TAG, " convertToBitmap(YUV420-888) $rotationDegrees ")

        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        var outputOffset = 0

        /////////  Y BUFFER  /////////
        if (imageProxy.planes[0].pixelStride != 1)
        {
            //Log.v(TAG, " [0] pixelStride = ${imageProxy.planes[0].pixelStride}, rowStride = ${imageProxy.planes[0].rowStride}, width = ${imageProxy.width}")
            val rowBuffer1 = ByteArray(imageProxy.planes[0].rowStride)
            for (row in 0 until (imageProxy.height / imageProxy.planes[0].pixelStride))
            {
                yBuffer.position(row * imageProxy.planes[0].rowStride)
                yBuffer.get(rowBuffer1, 0, imageProxy.planes[0].pixelStride)
                if (outputOffset > 0)
                {
                    for (col in 0 until imageProxy.width)
                    {
                        nv21[outputOffset] = rowBuffer1[col * imageProxy.planes[0].pixelStride]
                        outputOffset += imageProxy.planes[0].pixelStride
                    }
                }
            }
        }
        else
        {
            //  imageProxy.planes[0].pixelStride == 1
            //Log.v(TAG, " [0] pixelStride = ${imageProxy.planes[0].pixelStride}, rowStride = ${imageProxy.planes[0].rowStride}, width = ${imageProxy.width}, height = ${imageProxy.height}")
            for (row in 0 until imageProxy.height)
            {
                yBuffer.position(row * imageProxy.planes[0].rowStride)
                yBuffer.get(nv21, outputOffset, imageProxy.width)
                outputOffset += imageProxy.width
            }
        }

        /////////  V BUFFER  /////////
        try
        {
            for (row in 0 until ((imageProxy.height / imageProxy.planes[2].pixelStride) - 1))
            {
                vBuffer.position(row * imageProxy.planes[2].rowStride)
                vBuffer.get(nv21, outputOffset, imageProxy.width)
                outputOffset += imageProxy.width
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

        /////////  U BUFFER  /////////
        try
        {
            for (row in 0 until ((imageProxy.height / imageProxy.planes[1].pixelStride) - 1))
            {
                uBuffer.position(row * imageProxy.planes[1].rowStride)
                uBuffer.get(nv21, outputOffset, imageProxy.width)
                outputOffset += imageProxy.width
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

        val out = ByteArrayOutputStream()
        val width = imageProxy.width
        val height = imageProxy.height
        val yuvImage = YuvImage(nv21, NV21, width, height, null)
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)

        insertCache(out.toByteArray(), rotationDegrees)
        imageProxy.close()
        refresh()
    }


    private fun convertToBitmapYUV420888NV21(imageProxy: ImageProxy, rotationDegrees: Int)
    {
        //Log.v(TAG, " convertToBitmap(YUV420-NV21) $rotationDegrees ")

        //  ImageFormat.YUV_420_888 : 35
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val out = ByteArrayOutputStream()
        val width = imageProxy.width
        val height = imageProxy.height
        val yuvImage = YuvImage(nv21, NV21, width, height, null)
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)

        insertCache(out.toByteArray(), rotationDegrees)
        imageProxy.close()
        refresh()
    }

    private fun getImageBitmap(image: MyImageByteArray) : Bitmap?
    {
        var imageBitmap : Bitmap? = null
        try
        {
            val rotationMatrix = Matrix()
            rotationMatrix.postRotate(image.rotationDegrees.toFloat())
            imageBitmap = BitmapFactory.decodeByteArray(image.imageData, 0, image.imageData.size)
            if (imageBitmap != null)
            {
                imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.width, imageBitmap.height, rotationMatrix, true)
                System.gc()
            }
        }
        catch (t: Throwable)
        {
            t.printStackTrace()
        }
        return (imageBitmap)
    }

    private fun insertCache(byteArray : ByteArray, rotationDegrees: Int)
    {
        try
        {
            if ((cachePics.size != 0)&&(cachePics.size >= maxCachePics))
            {
                cachePics.removeAt(0)
            }
            cachePics.add(MyImageByteArray(byteArray, rotationDegrees))
            if (maxCachePics > 0)
            {
                if (cachePics.size != maxCachePics)
                {
                    Log.v(TAG, " -=-=- image cache : ${cachePics.size} / $maxCachePics")
                    informationReceiver.updateMessage("cache:${cachePics.size}/$maxCachePics")
                }
                else
                {
                    if (!cacheIsFull)
                    {
                        informationReceiver.updateMessage("")
                        cacheIsFull = true
                    }
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getImage(position: Float) : Bitmap
    {
        try
        {
            if (cachePics.size == 0)
            {
                // 画像が入っていない...
                return (BitmapFactory.decodeResource(context.resources, ID_DRAWABLE_SPLASH_IMAGE))
            }

            val pos = (position * maxCachePics.toFloat()).toInt()
            //Log.v(TAG, " getImage (pos: $position : $pos)")
            val image : MyImageByteArray =
            if ((cachePics.size > 0)&&(pos >= cachePics.size))
            {
                cachePics[cachePics.size - 1]
            }
            else
            {
                cachePics[pos]
            }
            val imageBitmap = getImageBitmap(image)
            if (imageBitmap != null)
            {
                return (imageBitmap)
            }
        }
        catch (e : Throwable)
        {
            e.printStackTrace()
        }
        return (BitmapFactory.decodeResource(context.resources, ID_DRAWABLE_SPLASH_IMAGE))
    }

    fun isImageReceived() : Boolean
    {
        return (isImageReceived)
    }

    private fun refresh()
    {
        try
        {
            for (p in refresher)
            {
                p.refresh()
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun setupLiveviewCache()
    {
        val preference = PreferenceAccessWrapper(context)
        if ((!preference.getBoolean(ID_PREFERENCE_CACHE_LIVE_VIEW_PICTURES, false))||(isDisableCache))
        {
            return
        }

        cachePics.clear()
        cacheIsFull = false
        val nofCachePics = preference.getString(ID_PREFERENCE_NUMBER_OF_CACHE_PICTURES, ID_PREFERENCE_NUMBER_OF_CACHE_PICTURES_DEFAULT_VALUE)
        maxCachePics = try {
            nofCachePics.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            500
        }
    }
}
