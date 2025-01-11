package jp.osdn.gokigen.inventorymanager.export

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor
import jp.osdn.gokigen.inventorymanager.storage.DateConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DataExporter(private val activity: AppCompatActivity)
{
    private val storageDao = AppSingleton.db.storageDao()
    private var isExporting = false

    fun doExport(baseDirectory: String = "gokigen_bookshelf_", fileName: String = "GokigenBookshelfExport.json", callback : IExportProgressCallback? = null)
    {
        try
        {
            val archiveOnlyOneFile = PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_EXPORT_ARCHIVE_ONLY_ONE_FILE,
                IPreferencePropertyAccessor.PREFERENCE_EXPORT_ARCHIVE_ONLY_ONE_FILE_DEFAULT_VALUE
            )

            if (isExporting)
            {
                // ----- 既に実行中なので、実行しない
                activity.runOnUiThread {
                    Toast.makeText(activity, activity.getString(R.string.export_is_started), Toast.LENGTH_SHORT).show()
                    AppSingleton.vibrator.vibrate(activity, IVibrator.VibratePattern.SIMPLE_MIDDLE)
                }
                return
            }
            val directory = "$baseDirectory${SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())}"
            isExporting = true
            Thread {
                exportImpl(directory, fileName, callback, archiveOnlyOneFile)
            }.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun exportImpl(directory: String, fileName: String, callback : IExportProgressCallback?, archiveOnlyOneFile: Boolean)
    {
        Log.v(TAG, "DataExporter::export $directory/$fileName (isSingle: $archiveOnlyOneFile)")

        var failureCount = 0
        var outputCount = 0
        try
        {
            // ----- エクスポート開始を通知する
            Log.v(TAG, " Export: $directory/$fileName  START")
            activity.runOnUiThread {
                callback?.startExportFile("$directory/$fileName")
                // Toast.makeText(activity, "${activity.getString(R.string.label_data_start_export)} : $directory/$fileName", Toast.LENGTH_SHORT).show()
                AppSingleton.vibrator.vibrate(activity, IVibrator.VibratePattern.SIMPLE_MIDDLE)
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
                    imageFile1 = if ((data.imageFile1?:"").isNotEmpty()) { if (archiveOnlyOneFile) { "${data.imageFile1}" } else { "${data.id}/${data.imageFile1}" } } else { data.imageFile1 },
                    imageFile2 = if ((data.imageFile2?:"").isNotEmpty()) { if (archiveOnlyOneFile) { "${data.imageFile2}" } else { "${data.id}/${data.imageFile2}" } } else { data.imageFile2 },
                    imageFile3 = if ((data.imageFile3?:"").isNotEmpty()) { if (archiveOnlyOneFile) { "${data.imageFile3}" } else { "${data.id}/${data.imageFile3}" } } else { data.imageFile3 },
                    imageFile4 = if ((data.imageFile4?:"").isNotEmpty()) { if (archiveOnlyOneFile) { "${data.imageFile4}" } else { "${data.id}/${data.imageFile4}" } } else { data.imageFile4 },
                    imageFile5 = if ((data.imageFile5?:"").isNotEmpty()) { if (archiveOnlyOneFile) { "${data.imageFile5}" } else { "${data.id}/${data.imageFile5}" } } else { data.imageFile5 },
                    checked = data.checked,
                    informMessage = data.informMessage,
                    level = data.level,
                    counter = data.counter,
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
            activity.runOnUiThread {
                callback?.progressExportFile(0, ((exportImageFileList.size) + 1))
            }

            outputCount = exportImageFileList.size
            if (archiveOnlyOneFile)
            {
                exportJsonFileLocal(fileName, exportTarget)
                failureCount = createArchiveFile(directory, fileName, exportImageFileList, callback)
            }
            else
            {
                exportJsonFileExternal(directory, fileName, exportTarget)
                failureCount = exportImageFilesMain(directory, exportImageFileList, callback)
            }

            Log.v(TAG, " Export: $directory/$fileName  FINISHED.")

            // ----- エクスポート終了を通知する
            activity.runOnUiThread {
                Toast.makeText(activity, "${activity.getString(R.string.label_data_exported)} : $directory/$fileName  ${activity.getString(R.string.label_output_image_files)}: $outputCount (${activity.getString(R.string.label_output_image_failures)}: $failureCount)", Toast.LENGTH_SHORT).show()
                AppSingleton.vibrator.vibrate(activity, IVibrator.VibratePattern.SIMPLE_LONG)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        isExporting = false
        activity.runOnUiThread {
            callback?.finishExportFile("$directory/$fileName", failureCount, outputCount, archiveOnlyOneFile)
        }
    }

    private fun exportJsonFileExternal(directory: String, exportedFileName: String, dataContent: List<DataContentSerializable>): Boolean
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

    private fun exportJsonFileLocal(exportedFileName: String, dataContent: List<DataContentSerializable>): Boolean
    {
        try
        {
            val baseDir = activity.getExternalFilesDir(null)
            val filePath = "${baseDir?.absolutePath}/$exportedFileName"
            val targetFile = File(filePath)
            if (targetFile.exists())
            {
                try
                {
                    // ファイルが存在する場合、一度削除する
                    targetFile.delete()
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            val outputStream = FileOutputStream(targetFile)
            val writer = OutputStreamWriter(outputStream)

            // -------
            // データをJSONに変換してローカルストレージに出力する
            writer.write(Json.encodeToString(DataContentListSerializer(dataContent)))
            writer.flush()
            writer.close()

            return (true)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    private fun exportImageFilesMain(baseDirectory: String, imageFileList: ArrayList<ExportImageFileList>, callback : IExportProgressCallback?) : Int
    {
        var resultOk = 0
        var resultNg = 0

        // 保管した画像ファイルを一括出力する...
        Log.v(TAG, "exportImageFilesMain() : $baseDirectory, ${imageFileList.size} images.")
        try
        {
            val exporter = InOutExportImage(activity)
            for (item in imageFileList)
            {
                Log.v(TAG, "${item.id}/${item.imageFileName} -> $baseDirectory/${item.id}/${item.imageFileName}")
                if (exporter.exportJpegFile(item.id, item.imageFileName?:"image.jpg", baseDirectory))
                {
                    resultOk++
                }
                else
                {
                    resultNg++
                }

                if (((resultOk + resultNg) % 10) == 0)
                {
                    activity.runOnUiThread {
                        // ----- 進捗を報告する
                        callback?.progressExportFile((resultOk + resultNg) + 1, ((imageFileList.size) + 1))
                    }
                }
            }
            Log.v(TAG, "Export Jpeg Images : Success:$resultOk  Failure:$resultNg")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (resultNg)
    }

    private fun createArchiveFile(destinationDirectory: String, fileName: String, imageFileList: ArrayList<ExportImageFileList>, callback : IExportProgressCallback?) : Int
    {
        var nofErrorFile = 0
        var nofProcessedFile = 0
        val targetFileList = ArrayList<File>()
        val baseDir = activity.getExternalFilesDir(null)
        val totalFileCount = imageFileList.size + 1
        val archiveZipFileName = "$fileName.zip"
        try
        {
            targetFileList.add(File("${baseDir?.absolutePath}/$fileName"))
            for (imageFile in imageFileList)
            {
                targetFileList.add(File("${baseDir?.absolutePath}/${imageFile.id}/${imageFile.imageFileName}"))
            }

            var documentUri: Uri? = null
            val outputDir = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path}/$destinationDirectory/"
            val resolver = activity.contentResolver
            val extStorageUri: Uri
            val values = ContentValues()

            values.put(MediaStore.Downloads.TITLE, archiveZipFileName)
            values.put(MediaStore.Downloads.DISPLAY_NAME, archiveZipFileName)
            values.put(MediaStore.Downloads.MIME_TYPE, "application/zip")

            val outputStream : OutputStream?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/$destinationDirectory")
                values.put(MediaStore.Downloads.IS_PENDING, true)
                extStorageUri = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                Log.v(TAG, "---------- $archiveZipFileName $values")

                documentUri = resolver.insert(extStorageUri, values)
                if (documentUri != null)
                {
                    val uriData : Uri = documentUri
                    outputStream = resolver.openOutputStream(uriData, "wa")
                }
                else
                {
                    outputStream = null
                }
            }
            else
            {
                val path = File(outputDir)
                path.mkdir()
                values.put(MediaStore.Downloads.DATA, path.absolutePath + File.separator + archiveZipFileName)
                val targetFile = File(outputDir + File.separator + archiveZipFileName)
                outputStream = FileOutputStream(targetFile)
            }

            if (outputStream != null)
            {
                ZipOutputStream(outputStream).use { zipOutputStream ->
                    targetFileList.forEach { sourceFile ->
                        try
                        {
                            val zipEntry = ZipEntry(sourceFile.name)
                            zipOutputStream.putNextEntry(zipEntry)
                            sourceFile.inputStream().use { inputStream ->
                                inputStream.copyTo(zipOutputStream)
                            }
                            zipOutputStream.closeEntry()
                            nofProcessedFile++
                            if (nofProcessedFile % 10 == 0)
                            {
                                activity.runOnUiThread {
                                    callback?.progressExportFile(nofProcessedFile, totalFileCount)  // ----- 進捗を報告する
                                }
                            }
                        }
                        catch (e: Exception)
                        {
                            e.printStackTrace()
                            nofErrorFile++
                        }
                    }
                }
            }
            else
            {
                nofErrorFile = imageFileList.size + 1
            }
            outputStream?.flush()
            outputStream?.close()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                values.put(MediaStore.Downloads.IS_PENDING, false)
                if (documentUri != null)
                {
                    val myUri: Uri = documentUri
                    resolver.update(myUri, values, null, null)
                }
            }
            activity.runOnUiThread {
                callback?.progressExportFile(nofProcessedFile, totalFileCount)  // ----- 進捗を報告する
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (nofErrorFile)
    }

    interface IExportProgressCallback
    {
        fun startExportFile(fileName: String)
        fun progressExportFile(currentFileCount: Int, totalFileCount: Int)
        fun finishExportFile(fileName: String, exportNG: Int, totalFile: Int, archiveOnlyOneFile: Boolean)
    }

    companion object
    {
        private val TAG = DataExporter::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}
