package jp.osdn.gokigen.gokigenassets.utils.imagefile

import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_LABEL_APP_LOCATION
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_MESSAGE_LABEL_CAPTURE_SUCCESS
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ImageStoreExternalLegacy(private val context: FragmentActivity) : IImageStore
{
    private fun getExternalOutputDirectory(): File
    {
        @Suppress("DEPRECATION") val directoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path + "/" + context.getString(ID_LABEL_APP_LOCATION) + "/"
        val target = File(directoryPath)
        try
        {
            target.mkdirs()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        Log.v(TAG, "  ----- RECORD Directory PATH : $directoryPath -----")
        return (target)
    }

    override fun takePhoto(id : Int, imageCapture : ImageCapture?) : Boolean
    {
        if ((!isExternalStorageWritable())||(imageCapture == null))
        {
            Log.v(TAG, " takePhotoExternal() : cannot write image to external.")
            return (false)
        }

        Log.v(TAG, " takePhotoExternal()")
        try
        {
            val outputDir = getExternalOutputDirectory()

            val mimeType = "image/jpeg"
            val photoFile = "P" + SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + "_" + id + ".jpg"

            val extStorageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, photoFile)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, photoFile)
            values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            @Suppress("DEPRECATION")
            values.put(MediaStore.Images.Media.DATA, outputDir.absolutePath + File.separator + photoFile)

            val resolver = context.contentResolver
            val imageUri = resolver.insert(extStorageUri, values)?: return (false)
            resolver.update(imageUri, values, null, null)

            /////////////////////////////
            //val cursor = resolver.query(imageUri, null, null, null, null)
            //DatabaseUtils.dumpCursor(cursor)
            //cursor!!.close()
            /////////////////////////////

            val openStream = resolver.openOutputStream(imageUri) ?: return (false)
            val outputOptions = ImageCapture.OutputFileOptions.Builder(openStream).build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback
                {
                    override fun onError(e: ImageCaptureException)
                    {
                        Log.e(TAG, "Photo capture failed: ${e.message} ", e)
                        e.printStackTrace()
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults)
                    {
                        val msg = context.getString(ID_MESSAGE_LABEL_CAPTURE_SUCCESS) + " ${outputDir.canonicalPath}/$photoFile"
                        Toast.makeText(context.baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.v(TAG, msg)
                    }
                })
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            return (false)
        }
        return (true)
    }

    private fun isExternalStorageWritable(): Boolean
    {
        return (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
    }

    companion object
    {
        private val TAG = ImageStoreExternalLegacy::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}
