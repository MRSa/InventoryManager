package jp.osdn.gokigen.gokigenassets.utils.imagefile

import android.content.ContentValues
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_LABEL_APP_LOCATION
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_MESSAGE_LABEL_CAPTURE_SUCCESS
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_EXTERNAL_STORAGE_LOCATION
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_EXTERNAL_STORAGE_LOCATION_DEFAULT_VALUE
import jp.osdn.gokigen.gokigenassets.preference.PreferenceAccessWrapper
import java.io.File
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.Q)
class ImageStoreExternal(private val context: FragmentActivity) : IImageStore, IImageStoreGrant
{
    private fun getExternalOutputDirectory(): String
    {
        var uriString = ""
        var prefString : String? = ""
        try
        {
            prefString = PreferenceAccessWrapper(context).getString(ID_PREFERENCE_EXTERNAL_STORAGE_LOCATION, ID_PREFERENCE_EXTERNAL_STORAGE_LOCATION_DEFAULT_VALUE)
            uriString = Uri.decode(prefString)
            if (uriString.isEmpty())
            {
                // 設定がない場合はデフォルトの場所に...
                uriString = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path + "/" + context.getString(ID_LABEL_APP_LOCATION) + "/"
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

        try
        {
            File(uriString).mkdirs()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        Log.v(TAG, "  ----- RECORD Directory PATH : ${uriString} : ${prefString} -----")
        return (uriString)
    }

    override fun takePhoto(id : Int, imageCapture : ImageCapture?) : Boolean {
        if ((!isExternalStorageWritable()) || (imageCapture == null)) {
            Log.v(TAG, " takePhotoExternal() : cannot write image to external.")
            return (false)
        }
        Log.v(TAG, " takePhotoExternal()")

        val outputDirString = getExternalOutputDirectory()
        if (outputDirString.isEmpty()) {
            return (false)
        }
        val outputDir = File(outputDirString)
        val resolver = context.contentResolver

        val mimeType = "image/jpeg"
        val now = System.currentTimeMillis()
        val path =
            Environment.DIRECTORY_DCIM + File.separator + context.getString(ID_LABEL_APP_LOCATION) // Environment.DIRECTORY_PICTURES  + File.separator + "gokigen" //"DCIM/aira01a/"
        val photoFile = "P" + SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(now) + "_" + id + ".jpg"

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, photoFile)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, photoFile)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        val extStorageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, path)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            values.put(MediaStore.Images.Media.DATA, outputDir.absolutePath + File.separator + photoFile)
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        var imageUri: Uri? = null
        try
        {
            imageUri = resolver.insert(extStorageUri, values)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        if (imageUri == null)
        {
            return (false)
        }

        /////////////////////////////
        Log.v(TAG, "  ===== imageUri : $imageUri =====")
        val cursor = resolver.query(imageUri, null, null, null, null)
        DatabaseUtils.dumpCursor(cursor)
        cursor!!.close()
        /////////////////////////////

        var openStream : OutputStream? = null
        try
        {
            openStream = resolver.openOutputStream(imageUri)
        }
        catch (e : Exception)
        {
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            resolver.update(imageUri, values, null, null)
            e.printStackTrace()
        }

        try
        {
            if (openStream != null)
            {
                val outputOptions = ImageCapture.OutputFileOptions.Builder(openStream).build()
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(e: ImageCaptureException) {
                            Log.e(TAG, "Photo capture failed: ${e.message} ", e)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                //values.clear()
                                values.put(MediaStore.Images.Media.IS_PENDING, false)
                                resolver.update(imageUri, values, null, null)
                            }
                            e.printStackTrace()
                        }

                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                //values.clear()
                                values.put(MediaStore.Images.Media.IS_PENDING, false)
                                resolver.update(imageUri, values, null, null)
                            }
                            val msg = context.getString(ID_MESSAGE_LABEL_CAPTURE_SUCCESS) + " $path/$photoFile"
                            Toast.makeText(context.baseContext, msg, Toast.LENGTH_SHORT).show()
                            //Snackbar.make(
                            //    context.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(
                            //        ID_MAIN_ACTIVITY_LAYOUT
                            //    ), msg, Snackbar.LENGTH_SHORT
                            //).show()
                            Log.v(TAG, msg)
                        }
                    })
            }
            else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    //values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    resolver.update(imageUri, values, null, null)
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
         return (true)
    }

    override fun grantStoreImage()
    {

    }

    private fun isExternalStorageWritable(): Boolean
    {
        return (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
    }

/*
    private fun isExternalStorageReadable(): Boolean
    {
        return (Environment.getExternalStorageState() in setOf(
            Environment.MEDIA_MOUNTED,
            Environment.MEDIA_MOUNTED_READ_ONLY
        ))
    }
*/

    companion object
    {
        private val TAG = ImageStoreExternal::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }

}