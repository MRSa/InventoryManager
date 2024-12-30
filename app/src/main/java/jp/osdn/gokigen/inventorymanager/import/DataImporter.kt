package jp.osdn.gokigen.inventorymanager.import

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.export.DataContentListSerializer
import jp.osdn.gokigen.inventorymanager.export.DataContentSerializable
import jp.osdn.gokigen.inventorymanager.storage.DataContent
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Date
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class DataImporter(private val activity: AppCompatActivity)
{
    private val storageDao = AppSingleton.db.storageDao()
    private var jsonFilePath: String = ""
    private var dataList: DataContentListSerializer? = null
    private var dataImportCount = 0

    fun doImport(callback: IImportProgress?)
    {
        try
        {
            if (dataList == null)
            {
                // ------ データがないので、何もせずに終了する
                activity.runOnUiThread {
                    callback?.finishImportFiles(
                        false,
                        0,
                        activity.getString(R.string.failure_import_no_data)
                    )
                }
                return
            }
            // ----- 処理開始を通知
            activity.runOnUiThread { callback?.startImportFiles() }

            dataImportCount = 0
            val result = doImportImpl(dataList?.list, callback)

            // ----- 処理終了を通知
            activity.runOnUiThread {
                callback?.finishImportFiles(result, dataImportCount, "")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun doImportImpl(dataList: List<DataContentSerializable>?, callback: IImportProgress?) : Boolean
    {
        var result = false
        try
        {
            if (dataList == null)
            {
                // ----- なにもしない
                return (false)
            }
            for (data in dataList)
            {
                // ----- データを１件づつ登録する
                entryIntoDatabase(data)
                dataImportCount++
                if (dataImportCount % 10 == 0)
                {
                    activity.runOnUiThread {
                        callback?.progressImportFiles(dataImportCount, dataList.size)
                    }
                }
            }
            result = true
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (result)
    }

    private fun entryIntoDatabase(data : DataContentSerializable) : Long
    {
        var entryId : Long
        try
        {
            // ----- レコードの登録
            val content = DataContent.create(
                data.title,
                data.subTitle,
                data.author,
                data.publisher,
                data.isbn,
                data.productId ,
                data.urlStr,
                data.bcrText,
                data.category,
                "",
                "",
                "",
                "",
                "",
                data.note)
            entryId = storageDao.insertSingle(content)

            // ----- 画像ファイルの移動と更新
            val imageFile1 = moveImageFile(entryId, data.imageFile1)
            val imageFile2 = moveImageFile(entryId, data.imageFile2)
            val imageFile3 = moveImageFile(entryId, data.imageFile3)
            val imageFile4 = moveImageFile(entryId, data.imageFile4)
            val imageFile5 = moveImageFile(entryId, data.imageFile5)
            storageDao.setImageFileName(entryId, imageFile1, imageFile2, imageFile3, imageFile4, imageFile5, Date())
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            entryId = 0L
        }
        return (entryId)
    }

    private fun moveImageFile(destinationId: Long, imageFileName: String?) : String
    {
        try
        {
            if ((imageFileName ?: "").isEmpty())
            {
                // ----- ファイルは存在しないのでファイル名を返さない
                return ("")
            }
            val newFileName = "${destinationId}_${imageFileName?.substring(imageFileName.indexOf("_") + 1, imageFileName.length)}"
            Log.v(TAG, "image file : $imageFileName -> $newFileName")

            // ベースディレクトリ
            val baseDir = activity.getExternalFilesDir(null)

            // コピー元ファイル
            val sourceFile = File("${baseDir?.absolutePath}/extract/$imageFileName")
            if (!sourceFile.exists())
            {
                // ----- ファイルが見つからない場合は。。。登録しない
                return ("")
            }

            // コピー先ディレクトリ
            val destinationDir = File("${baseDir?.absolutePath}/$destinationId")
            if (!destinationDir.exists())
            {
                // ----- ディレクトリが存在しない場合は、格納ディレクトリを作成する
                destinationDir.mkdirs()
            }

            // コピー先ファイル
            val destinationFile = File("${baseDir?.absolutePath}/$destinationId/$newFileName")

            // ファイルのコピー
            sourceFile.copyTo(destinationFile, overwrite = true)

            // コピー元のファイルを削除する
            sourceFile.delete()

            // イメージファイル名を応答する
            return (newFileName)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return ("")
    }

    fun extractZipFileIntoLocal(targetUri: Uri?, callback: IExtractProgress?)
    {
        var message = ""
        try
        {
            dataList = null
            Log.v(TAG, "extractZipFileIntoLocal() : START")
            activity.runOnUiThread {
                callback?.startExtractFiles()
                Toast.makeText(activity, activity.getString(R.string.label_check_zip_file), Toast.LENGTH_SHORT).show()
            }

            // ----- 出力先ディレクトリの準備
            val baseDir = activity.getExternalFilesDir(null)
            val targetPath = "${baseDir?.absolutePath}/extract"
            prepareExtractDir(targetPath)

            // ----- アーカイブファイルを展開...
            val fileCount = extractZipFileIntoLocalImpl(targetPath, targetUri, callback)
            if (fileCount == 0)
            {
                return
            }

            // ----- データファイル（JSONファイル）があるか探す
            jsonFilePath = ""
            checkIfJsonFileExists(targetPath, callback)
            Log.v(TAG, "JSON FILE : $jsonFilePath")
            if (jsonFilePath.isNotEmpty())
            {
                // ----- JSONファイルが見つかった。 InventoryManagerのJSONファイルか、確認する。
                activity.runOnUiThread {
                    callback?.progressExtractFiles(fileCount, jsonFilePath)
                }
                message = jsonFilePath

                 checkReadJsonFile(jsonFilePath)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        val dataCount = dataList?.list?.size ?: 0
        activity.runOnUiThread {
            if (dataCount > 0)
            {
                // ----- データの読み込みに成功
                callback?.finishExtractFiles(true, dataCount, message)
            }
            else
            {
                // ----- データの読み込みに失敗
                callback?.finishExtractFiles(false, 0, message)
            }
        }
    }

    private fun checkReadJsonFile(targetJsonFile: String)
    {
        try
        {
            val jsonFile = File(targetJsonFile)
            val jsonString = jsonFile.readText()
            val decodeData : DataContentListSerializer = Json.decodeFromString(jsonString)
            dataList = if ((decodeData.list).isNotEmpty()) { decodeData } else { null }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        Log.v(TAG, " checkReadJsonFile(): $targetJsonFile  count: ${dataList?.list?.size}")
        return
    }

    private fun prepareExtractDir(targetPath: String)
    {
        try
        {
            // ---- 展開先のディレクトリを準備する
            val destination = File(targetPath)
            if (destination.exists())
            {
                cleanupExtractDirectory(targetPath)
            }
            else
            {
                destination.mkdirs()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun extractZipFileIntoLocalImpl(targetPath: String, targetUri: Uri?, callback: IExtractProgress?) : Int
    {
        var fileCount = 0
        try
        {
            val destination = File(targetPath)

            if (targetUri == null)
            {
                activity.runOnUiThread {
                    callback?.finishExtractFiles(
                        false,
                        0,
                        activity.getString(R.string.failure_file_not_find)
                    )
                }
                return (0)
            }

            // ----- Zipファイルを展開する
            val resolver = activity.contentResolver
            val inputStream: InputStream? = resolver.openInputStream(targetUri)
            if (inputStream == null)
            {
                activity.runOnUiThread {
                    callback?.finishExtractFiles(
                        false,
                        0,
                        activity.getString(R.string.failure_file_not_find)
                    )
                }
                return (0)
            }
            val zipInputStream = ZipInputStream(inputStream)
            var entry: ZipEntry? = zipInputStream.nextEntry
            while (entry != null)
            {
                try
                {
                    fileCount++
                    val fileNameToExtract = entry.name
                    activity.runOnUiThread {
                        callback?.progressExtractFiles(fileCount, fileNameToExtract)
                    }
                    val filePath = "$destination/$fileNameToExtract"
                    val newFile = File(filePath)
                    if (entry.isDirectory)
                    {
                        newFile.mkdirs()
                    }
                    else
                    {
                        try
                        {
                            val fileOutputStream = FileOutputStream(newFile)
                            val buffer = ByteArray(2048)
                            var len: Int
                            while (zipInputStream.read(buffer).also { len = it } > 0)
                            {
                                fileOutputStream.write(buffer, 0, len)
                            }
                            fileOutputStream.close()
                        }
                        catch (eee: Exception)
                        {
                            eee.printStackTrace()
                        }
                    }
                    zipInputStream.closeEntry()
                    entry = zipInputStream.nextEntry
                }
                catch (ee: Exception)
                {
                    ee.printStackTrace()
                }
            }
            zipInputStream.close()
            inputStream.close()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        if (fileCount == 0)
        {
            // ---- 展開されたファイル数がゼロなので、、、ファイル無しで終了する
            activity.runOnUiThread {
                callback?.finishExtractFiles(
                    false,
                    0,
                    activity.getString(R.string.failure_file_not_find)
                )
            }
        }
        return (fileCount)
    }

    private fun checkIfJsonFileExists(targetPath: String, callback: IExtractProgress?)
    {
        try
        {
            Log.v(TAG, "checkIfJsonFileExists() : $targetPath")
            val destination = File(targetPath)
            if ((destination.exists())&&(destination.isDirectory))
            {
                val files = destination.listFiles()
                files?.forEach { file ->
                    try
                    {
                        if (file.isFile)
                        {
                            if (file.name.contains("json"))
                            {
                                // ファイル名にJSONという文字列を見つけたので記憶する
                                jsonFilePath = file.absolutePath
                            }
                        } else if (file.isDirectory) {
                            // 再帰的にサブディレクトリも探索
                            checkIfJsonFileExists(file.absolutePath, callback)
                        }
                    }
                    catch (ee: Exception)
                    {
                        ee.printStackTrace()
                    }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun cleanupExtractDirectory(dirPath: String)
    {
        try
        {
            Log.v(TAG, "cleanupExtractDirectory() : $dirPath")
            val destination = File(dirPath)
            if ((destination.exists())&&(destination.isDirectory))
            {
                val files = destination.listFiles()
                files?.forEach { file ->
                    try
                    {
                        if (file.isFile) {
                            val result = file.delete()
                            Log.v(TAG, "Delete File : ${file.name} ($result)")
                        } else if (file.isDirectory) {
                            // 再帰的にサブディレクトリも削除
                            cleanupExtractDirectory(file.absolutePath)
                            val result = file.delete()
                            Log.v(TAG, "Delete Dir. : ${file.name} ($result)")
                        }
                    }
                    catch (ee: Exception)
                    {
                        ee.printStackTrace()
                    }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    interface IExtractProgress
    {
        fun startExtractFiles()
        fun progressExtractFiles(count: Int, fileName: String)
        fun finishExtractFiles(result: Boolean, dataCount: Int, message: String)
    }

    interface IImportProgress
    {
        fun startImportFiles()
        fun progressImportFiles(count: Int, totalCount: Int)
        fun finishImportFiles(result: Boolean, dataCount: Int, message: String)
    }

    companion object
    {
        private val TAG = DataImporter::class.java.simpleName
    }
}
