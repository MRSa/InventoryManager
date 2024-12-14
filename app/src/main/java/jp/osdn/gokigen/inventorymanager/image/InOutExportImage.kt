package jp.osdn.gokigen.inventorymanager.image

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import jp.osdn.gokigen.inventorymanager.AppSingleton
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class InOutExportImage(private val context : Context)
{
    private var lastExportedUri: Uri? = null

    fun storeImageLocal(id: Long, fileName: String, image: Bitmap?)
    {
        try
        {
            if (image == null)
            {
                Log.v(TAG, "storeImageLocal : $id/$fileName  : image is NULL...")
                return
            }
            // ----- いったんイメージデータをコピーする
            val imageBitmap = Bitmap.createBitmap(image)
            try
            {
                // ----- 保管場所の準備
                val baseDir = context.getExternalFilesDir(null)
                if (baseDir != null)
                {
                    if (!baseDir.exists())
                    {
                        baseDir.mkdirs()
                    }
                    // ----= 保管ディレクトリ
                    val path = "${baseDir.absolutePath}/$id"
                    val directory = File(path)
                    if (!directory.exists())
                    {
                        directory.mkdirs()
                    }
                    // ----- イメージファイルの保存処理
                    val imageFile = File(directory, fileName)
                    try
                    {
                        val outputStream = FileOutputStream(imageFile)
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.close()
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                }
                else
                {
                    Log.v(TAG, "ERR>Did not get the storage path.")
                }
            }
            catch (t: Throwable)
            {
                t.printStackTrace()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun getImageLocal(id: Long, fileName: String) : Bitmap?
    {
        try
        {
            val baseDir = context.getExternalFilesDir(null)
            val filePath = "${baseDir?.absolutePath}/$id/$fileName"
            val file = File(filePath)
            if (file.exists())
            {
                // イメージファイルが存在する場合、Bitmapに変換する
                return BitmapFactory.decodeFile(filePath)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (null)
    }

    /**
     *   イメージファイルを読めるところにコピーする。
     */
    fun exportImage(id: Long, fileName: String, callback: IImageAccessor?)
    {
        try
        {
            Log.v(TAG, "exportImage($id, $fileName)")
            Thread {
                try
                {
                    val image = getImageLocal(id, fileName)
                    if (image == null)
                    {
                        Log.v(TAG, "exportImage($id, $fileName) : image get failure")
                        callback?.resultReport(false, "image get failure($id/$fileName)")
                    }
                    else
                    {
                        exportToFile("${id}_$fileName.jpeg", image)
                        callback?.resultReport(true, "image exported : $id/$fileName")
                    }
                }
                catch (t: Throwable)
                {
                    t.printStackTrace()
                    callback?.resultReport(false, "ERR>Unknown: ${t.localizedMessage}")
                }
            }.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun exportToFile(fileName: String, targetImage: Bitmap): String
    {
        //  ----- ビットマップデータを(JPEG形式で)保管する。
        var resultMessage = ""
        try
        {
            val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + "/" + AppSingleton.APP_NAMESPACE + "/"
            val resolver = context.contentResolver
            var outputStream: OutputStream? = null
            val extStorageUri: Uri
            var imageUri: Uri? = null
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, fileName)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + AppSingleton.APP_NAMESPACE)
                values.put(MediaStore.Images.Media.IS_PENDING, true)
                extStorageUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                Log.v(TAG, "---------- $fileName $values")
                imageUri = resolver.insert(extStorageUri, values)
                if (imageUri != null)
                {
                    ////////////////////////////////////////////////////////////////
                    if (DUMPLOG)
                    {
                        try
                        {
                            val cursor = resolver.query(imageUri, null, null, null, null)
                            DatabaseUtils.dumpCursor(cursor)
                            cursor!!.close()
                        }
                        catch (e: Exception)
                        {
                            e.printStackTrace()
                            resultMessage = e.message ?:""
                        }
                    }
                    ////////////////////////////////////////////////////////////////
                    try
                    {
                        outputStream = resolver.openOutputStream(imageUri, "wa")
                    }
                    catch (ee: Exception)
                    {
                        ee.printStackTrace()
                    }
                }
                else
                {
                    Log.v(TAG, " cannot get imageUri...")
                }
            }
            else
            {
                val path = File(outputDir)
                if (!path.mkdir())
                {
                    Log.v(TAG, " mkdir fail: $outputDir")
                }
                values.put(MediaStore.Images.Media.DATA, path.absolutePath + File.separator + fileName
                )
                val targetPath = File(outputDir + File.separator + fileName)
                try
                {
                    outputStream = FileOutputStream(targetPath)
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            if (outputStream != null)
            {
                targetImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                if (imageUri != null)
                {
                    resolver.update(imageUri, values, null, null)
                    lastExportedUri = imageUri
                }
            }
        }
        catch (t: Throwable)
        {
            t.printStackTrace()
            resultMessage = t.message?: ""
            lastExportedUri = null
        }
        return (resultMessage)
    }

    companion object
    {
        private val  TAG = InOutExportImage::class.java.simpleName
        private const val DUMPLOG = false
    }
}