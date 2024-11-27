package jp.osdn.gokigen.gokigenassets.liveview.storeimage

import android.content.ContentValues
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.FragmentActivity
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_LABEL_APP_LOCATION
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SAVE_LOCAL_LOCATION
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SAVE_LOCAL_LOCATION_DEFAULT_VALUE
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider
import jp.osdn.gokigen.gokigenassets.preference.PreferenceAccessWrapper
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class StoreImage(private val context: FragmentActivity, private val imageProvider: IImageProvider, private val dumpLog : Boolean = false) : IStoreImage
{

    override fun doStore(id : Int, isEquirectangular : Boolean, position : Float, target: Bitmap?)
    {
        try
        {
            // 保存処理(プログレスダイアログ（「保存中...」）を表示

            val bitmapToStore = target ?: imageProvider.getImage(position)
            val isLocalLocation  = PreferenceAccessWrapper(context).getBoolean(ID_PREFERENCE_SAVE_LOCAL_LOCATION, ID_PREFERENCE_SAVE_LOCAL_LOCATION_DEFAULT_VALUE)
            if (isLocalLocation)
            {
                saveImageLocal(id, bitmapToStore)
            }
            else
            {
                saveImageExternal(id, bitmapToStore, isEquirectangular)
            }

            // 保存処理(プログレスダイアログ（「保存中...」）を削除
        }
        catch (t: Throwable)
        {
            t.printStackTrace()
        }
    }

    private fun prepareLocalOutputDirectory(): File
    {
        val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        mediaDir?.mkdirs()
        return (if (mediaDir != null && mediaDir.exists()) mediaDir else context.filesDir)
    }

    /**
     * ビットマップイメージをファイルに出力する
     *
     * @param targetImage  出力するビットマップイメージ
     */
    private fun saveImageLocal(id : Int, targetImage: Bitmap)
    {
        try
        {
            val fileName = "L" + SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + "_" + id + ".jpg"
            val photoFile = File(prepareLocalOutputDirectory(), fileName)
            val outputStream = FileOutputStream(photoFile)
            targetImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        }
        catch (t: Throwable)
        {
            t.printStackTrace()
        }
    }

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
        if (dumpLog)
        {
            Log.v(TAG, "  ----- RECORD Directory PATH : $directoryPath -----")
        }
        return (target)
    }

    /**
     * ビットマップイメージを外部ストレージのファイルに出力する
     *
     * @param targetImage  出力するビットマップイメージ
     */
    private fun saveImageExternal(id : Int, targetImage: Bitmap, isEquirectangular : Boolean)
    {
        try
        {
            if (!isExternalStorageWritable())
            {

                saveImageLocal(id, targetImage)
                return
            }

            val outputDir = getExternalOutputDirectory()
            val resolver = context.contentResolver
            val mimeType = "image/jpeg"
            val now = System.currentTimeMillis()
            val path = Environment.DIRECTORY_DCIM + File.separator + context.getString(ID_LABEL_APP_LOCATION) // Environment.DIRECTORY_PICTURES  + File.separator + "gokigen" //"DCIM/aira01a/"
            val fileName = "L" + SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(now) + "_" + id + ".jpg"

            val extStorageUri : Uri
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, fileName)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            extStorageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.RELATIVE_PATH, path)
                values.put(MediaStore.Images.Media.IS_PENDING, true)
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                @Suppress("DEPRECATION")
                values.put(MediaStore.Images.Media.DATA, outputDir.absolutePath + File.separator + fileName)
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val imageUri = resolver.insert(extStorageUri, values)
            if (imageUri != null)
            {
                //resolver.update(imageUri, values, null, null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                {
                    Log.v(TAG, "  ===== StorageOperationWithPermission() : $imageUri =====")
                    //StorageOperationWithPermission(context).requestAndroidRMediaPermission(imageUri)
                }

                ////////////////////////////////////////////////////////////////
                if (dumpLog)
                {
                    val cursor = resolver.query(imageUri, null, null, null, null)
                    DatabaseUtils.dumpCursor(cursor)
                    cursor!!.close()
                }
                ////////////////////////////////////////////////////////////////

                val outputStream = resolver.openOutputStream(imageUri)
                if (outputStream != null)
                {
                    if (isEquirectangular)
                    {
                        val xmpValue = "http://ns.adobe.com/xap/1.0/"
                        val xmpValue1 =
                            "<?xpacket begin=\"" + "\" ?> <x:xmpmeta xmlns:x=\"adobe:ns:meta/\" xmptk=\"EquirectangularPics\">" +
                                    "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"><rdf:Description rdf:about=\"\" xmlns:GPano=\"http://ns.google.com/photos/1.0/panorama/\">" +
                                    "<GPano:UsePanoramaViewer>True</GPano:UsePanoramaViewer><GPano:ProjectionType>equirectangular</GPano:ProjectionType>" +
                                    "<GPano:FullPanoWidthPixels>${targetImage.width}</GPano:FullPanoWidthPixels><GPano:FullPanoHeightPixels>${targetImage.height}</GPano:FullPanoHeightPixels>" +
                                    "<GPano:CroppedAreaImageWidthPixels>${targetImage.width}</GPano:CroppedAreaImageWidthPixels><GPano:CroppedAreaImageHeightPixels>${targetImage.height}</GPano:CroppedAreaImageHeightPixels>" +
                                    "<GPano:CroppedAreaLeftPixels>0</GPano:CroppedAreaLeftPixels><GPano:CroppedAreaTopPixels>0</GPano:CroppedAreaTopPixels></rdf:Description></rdf:RDF></x:xmpmeta><?xpacket end=\"r\"?>"
                        val xmpLength = xmpValue.length + xmpValue1.length + 3
                        val byteArrayStream = ByteArrayOutputStream()
                        targetImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayStream)
                        val jpegImage = byteArrayStream.toByteArray()

                        outputStream.write(jpegImage, 0, 20)
                        outputStream.write(0xff)
                        outputStream.write(0xe1)
                        outputStream.write((xmpLength and 0xff00) shr 8)
                        outputStream.write((xmpLength and 0x00ff))
                        outputStream.write(xmpValue.toByteArray())
                        outputStream.write(0x00)
                        outputStream.write(xmpValue1.toByteArray())
                        outputStream.write(jpegImage, 20, (jpegImage.size - 20))

                        //targetImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()
                    }
                    else
                    {
                        targetImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    resolver.update(imageUri, values, null, null)

                }
            }
        }
        catch (t: Throwable)
        {
            t.printStackTrace()
        }
    }

    private fun isExternalStorageWritable(): Boolean
    {
        return (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
    }

    companion object
    {
        private val TAG = StoreImage::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }

}
