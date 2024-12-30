package jp.osdn.gokigen.inventorymanager.ui.model

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.osdn.gokigen.inventorymanager.import.DataImporter

class DataImportViewModel: ViewModel(), DataImporter.IExtractProgress, DataImporter.IImportProgress
{
    private lateinit var contentResolver: ContentResolver

    private val targetUri : MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    val targetFileUri: LiveData<Uri> = targetUri

    private val isImporting : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val dataImporting: LiveData<Boolean> = isImporting

    private val readyToImportData : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val readyToImport: LiveData<Boolean> = readyToImportData

    private val dataCountToImport : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val importDataCount: LiveData<Int> = dataCountToImport

    private val currentImportDataCount : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val currentImportCount: LiveData<Int> = currentImportDataCount

    fun initializeViewModel(activity: AppCompatActivity)
    {
        try
        {
            Log.v(TAG, "DataImportViewModel::initializeViewModel()")
            contentResolver = activity.contentResolver
            isImporting.value = false
            readyToImportData.value = false
            dataCountToImport.value = 0
            currentImportDataCount.value = 0
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setTargetFileUri(uri: Uri)
    {
        try
        {
            try
            {
                // ファイルに対し永続的なアクセス権を取得する場合は指定するが、、、今回は不要か？
                // contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                targetUri.value = uri
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun cancelImport()
    {
        try
        {
            isImporting.value = false
            readyToImportData.value = false
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun startExtractFiles()
    {
        Log.v(TAG, "startExtractFiles()")
        readyToImportData.value = false
        dataCountToImport.value = 0
        isImporting.value = true
    }

    override fun progressExtractFiles(count: Int, fileName: String)
    {
        Log.v(TAG, "progressExtractFiles($count, $fileName)")
    }

    override fun finishExtractFiles(result: Boolean, dataCount: Int, message: String)
    {
        try
        {
            Log.v(TAG, "finishExtractFiles($result, $dataCount, $message)")
            if (!result)
            {
                targetUri.value = null
                readyToImportData.value = false
                dataCountToImport.value = 0
                currentImportDataCount.value = 0
                isImporting.value = false
                return
            }
            isImporting.value = false
            readyToImportData.value = true
            dataCountToImport.value = dataCount
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun startImportFiles()
    {
        Log.v(TAG, "startExtractFiles()")
        isImporting.value = true
        readyToImportData.value = false
        currentImportDataCount.value = 0
    }

    override fun progressImportFiles(count: Int, totalCount: Int)
    {
        Log.v(TAG, "progressImportFiles($count, $totalCount)")
        currentImportDataCount.value = count
    }

    override fun finishImportFiles(result: Boolean, dataCount: Int, message: String)
    {
        Log.v(TAG, "finishImportFiles($result, $dataCount, $message)")
        isImporting.value = false
        readyToImportData.value = false
        currentImportDataCount.value = dataCount
    }

    companion object
    {
        private val TAG = DataImportViewModel::class.java.simpleName
    }
}
