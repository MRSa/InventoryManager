package jp.osdn.gokigen.gokigenassets.utils.imagefile

import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_MESSAGE_LABEL_CAPTURE_SUCCESS
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ImageStoreLocal(private val context: FragmentActivity)
{
    /**
     *   保存用ディレクトリを準備する（ダメな場合はアプリ領域のディレクトリを確保する）
     *
     */
    private fun prepareLocalOutputDirectory(): File
    {
        val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        mediaDir?.mkdirs()
        return (if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir)
    }

    fun takePhoto(id : Int, imageCapture : ImageCapture?)
    {
        if (imageCapture == null)
        {
            Log.v(TAG, " takePhotoLocal() : ImageCapture is null.")
            return
        }
        Log.v(TAG, " takePhotoLocal()")
        try
        {
            val photoFile = File(prepareLocalOutputDirectory(), "P" + SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + "_" + id + ".jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback
                {
                    override fun onError(exc: ImageCaptureException)
                    {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults)
                    {
                        val savedUri = Uri.fromFile(photoFile)
                        val msg = context.getString(ID_MESSAGE_LABEL_CAPTURE_SUCCESS) + " $savedUri"
                        Toast.makeText(context.baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.v(TAG, msg)
                    }
                }
            )
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = ImageStoreLocal::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}