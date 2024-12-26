package jp.osdn.gokigen.inventorymanager.export

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.storage.DateConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Locale

class DataExporter(private val activity: AppCompatActivity)
{
    private val storageDao = AppSingleton.db.storageDao()

    fun doExport(baseDirectory: String = "inventory", fileName: String = "inventoryDataExport.json")
    {
        try
        {
            val directory = "$baseDirectory${SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())}"

            Thread { exportImpl(directory, fileName) }.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun exportImpl(directory: String, fileName: String)
    {
        Log.v(TAG, "DataExporter::export $directory/$fileName")

        try
        {
            Log.v(TAG, " Export: $directory/$fileName  START")
            activity.runOnUiThread {
                Toast.makeText(activity, "${activity.getString(R.string.label_data_start_export)} : $directory/$fileName", Toast.LENGTH_SHORT).show()
            }

            // ----- 出力するデータをここで確保。
            val exportImageFileList = ArrayList<ExportImageFileList>()
            val dateConverter = DateConverter()
            val exportTarget = ArrayList<DataContentSerializable>()
            storageDao.getAll().forEach { data ->
                exportTarget.add(DataContentSerializable(
                    id = data.id,
                    title = data.title,
                    subTitle = data.subTitle,
                    author = data.author,
                    publisher = data.publisher,
                    description = data.description,
                    isbn = data.isbn,
                    productId = data.productId,
                    urlStr = data.urlStr,
                    bcrText = data.bcrText,
                    note = data.note,
                    category = data.category,
                    imageFile1 = data.imageFile1,
                    imageFile2 = data.imageFile2,
                    imageFile3 = data.imageFile3,
                    imageFile4 = data.imageFile4,
                    imageFile5 = data.imageFile5,
                    checked = data.checked,
                    informMessage = data.informMessage,
                    informDate = dateConverter.fromDateToLong(data.informDate),
                    isDelete = data.isDelete,
                    deleteDate = dateConverter.fromDateToLong(data.deleteDate),
                    updateDate = dateConverter.fromDateToLong(data.updateDate),
                    createDate = dateConverter.fromDateToLong(data.createDate)
                ))
                if ((data.imageFile1 ?: "").isNotEmpty())
                {
                    exportImageFileList.add(ExportImageFileList(data.id, data.imageFile1))
                }
                if ((data.imageFile2 ?: "").isNotEmpty())
                {
                    exportImageFileList.add(ExportImageFileList(data.id, data.imageFile2))
                }
                if ((data.imageFile3 ?: "").isNotEmpty())
                {
                    exportImageFileList.add(ExportImageFileList(data.id, data.imageFile3))
                }
                if ((data.imageFile4 ?: "").isNotEmpty())
                {
                    exportImageFileList.add(ExportImageFileList(data.id, data.imageFile4))
                }
                if ((data.imageFile5 ?: "").isNotEmpty())
                {
                    exportImageFileList.add(ExportImageFileList(data.id, data.imageFile5))
                }
            }
            exportFilesMain(directory, fileName, exportTarget)
            exportImageFilesMain(directory, exportImageFileList)

            Log.v(TAG, " Export: $directory/$fileName  FINISHED.")

            activity.runOnUiThread {
                Toast.makeText(activity, "${activity.getString(R.string.label_data_exported)} : $directory/$fileName", Toast.LENGTH_SHORT).show()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun exportFilesMain(directory: String, exportedFileName: String, dataContent: List<DataContentSerializable>): Boolean
    {
        try
        {
            var documentUri: Uri? = null

            val outputDir = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path}/$directory/"
            val resolver = activity.contentResolver

            val extStorageUri: Uri
            val writer: OutputStreamWriter
            val values = ContentValues()
            values.put(MediaStore.Downloads.TITLE, exportedFileName)
            values.put(MediaStore.Downloads.DISPLAY_NAME, exportedFileName)
            values.put(MediaStore.Downloads.MIME_TYPE, "text/json")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/$directory")
                values.put(MediaStore.Downloads.IS_PENDING, true)
                extStorageUri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                Log.v(TAG, "---------- $exportedFileName $values")

                documentUri = resolver.insert(extStorageUri, values)
                if (documentUri != null)
                {
                    val uriData : Uri = documentUri
                    val outputStream = resolver.openOutputStream(uriData, "wa")
                    writer = OutputStreamWriter(outputStream)
                }
                else
                {
                    return (false)
                }
            }
            else
            {
                val path = File(outputDir)
                path.mkdir()
                values.put(MediaStore.Downloads.DATA, path.absolutePath + File.separator + exportedFileName)
                val targetFile = File(outputDir + File.separator + exportedFileName)
                val outputStream = FileOutputStream(targetFile)
                writer = OutputStreamWriter(outputStream)
            }

            // -------
            // JSONに変換して出力
            writer.write(Json.encodeToString(DataContentListSerializer(dataContent)))
            writer.flush()
            writer.close()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                values.put(MediaStore.Downloads.IS_PENDING, false)
                if (documentUri != null)
                {
                    val myUri: Uri = documentUri
                    resolver.update(myUri, values, null, null)
                }
            }
            return (true)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    private fun exportImageFilesMain(baseDirectory: String, imageFileList: ArrayList<ExportImageFileList>)
    {
        // 保管した画像ファイルを一括出力する...
        Log.v(TAG, "exportImageFilesMain() : $baseDirectory, ${imageFileList.size} images.")
        try
        {
            for (item in imageFileList)
            {
                Log.v(TAG, "${item.id}/${item.imageFileName} -> $baseDirectory/${item.id}/${item.imageFileName}")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = DataExporter::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}
