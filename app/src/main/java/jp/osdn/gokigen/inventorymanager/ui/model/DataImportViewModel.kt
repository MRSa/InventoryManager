package jp.osdn.gokigen.inventorymanager.ui.model

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.osdn.gokigen.inventorymanager.import.DataImporter
import jp.osdn.gokigen.inventorymanager.import.DataImporter.ImportProcess

class DataImportViewModel: ViewModel(), DataImporter.IExtractProgress, DataImporter.IImportProgress, DataImporter.IExtractPostProcess
{
    private lateinit var contentResolver: ContentResolver

    private val targetUri : MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    val targetFileUri: LiveData<Uri> = targetUri

    private val isImporting : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val dataImporting: LiveData<Boolean> = isImporting

    private val readyToImportData : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val readyToImport: LiveData<Boolean> = readyToImportData

    //private val finishedImportProcess : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    //val finishedImport: LiveData<Boolean> = finishedImportProcess

    private val dataCountToImport : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val importDataCount: LiveData<Int> = dataCountToImport

    private val currentImportDataCount : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val currentImportCount: LiveData<Int> = currentImportDataCount

    private val currentImportProcess : MutableLiveData<ImportProcess> by lazy { MutableLiveData<ImportProcess>() }
    val currentExecutingProcess: LiveData<ImportProcess> = currentImportProcess

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

    fun dismissImportProcess()
    {
        targetUri.value = null
        readyToImportData.value = false
        dataCountToImport.value = 0
        currentImportDataCount.value = 0
        isImporting.value = false
        currentImportProcess.value = ImportProcess.IDLE
    }

    override fun startExtractFiles(status: ImportProcess)
    {
        Log.v(TAG, "startExtractFiles()")
        readyToImportData.value = false
        dataCountToImport.value = 0
        isImporting.value = true
        currentImportProcess.value = status
    }

    override fun progressExtractFiles(count: Int, fileName: String)
    {
        Log.v(TAG, "progressExtractFiles($count, $fileName)")
    }

    override fun finishExtractFiles(result: Boolean, dataCount: Int, status: ImportProcess)
    {
        try
        {
            Log.v(TAG, "finishExtractFiles($result, $dataCount)")
            if (!result)
            {
                targetUri.value = null
                readyToImportData.value = false
                dataCountToImport.value = 0
                currentImportDataCount.value = 0
                isImporting.value = false
                currentImportProcess.value = status
                return
            }
            isImporting.value = false
            readyToImportData.value = true
            dataCountToImport.value = dataCount
            currentImportProcess.value = status
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun startImportPostProcess(status: ImportProcess)
    {
        Log.v(TAG, "startImportPostProcess()")
        isImporting.value = true
        currentImportProcess.value = status
    }

    override fun finishImportPostProcess(result: Boolean, status: ImportProcess)
    {
        Log.v(TAG, "finishImportPostProcess()")
        isImporting.value = false
        readyToImportData.value = false
        currentImportDataCount.value = 0
        dataCountToImport.value = 0
        targetUri.value = null
        currentImportProcess.value = status
    }

    override fun finishImportAllProcess(status: ImportProcess)
    {
        Log.v(TAG, "finishImportAllProcess(status: $status)")
        currentImportProcess.value = status
    }

    override fun startImportFiles(status: ImportProcess)
    {
        Log.v(TAG, "startExtractFiles()")
        isImporting.value = true
        readyToImportData.value = false
        currentImportDataCount.value = 0
        currentImportProcess.value = status
    }

    override fun progressImportFiles(count: Int, totalCount: Int)
    {
        Log.v(TAG, "progressImportFiles($count, $totalCount)")
        currentImportDataCount.value = count
    }

    override fun finishImportFiles(result: Boolean, dataCount: Int, status: ImportProcess)
    {
        Log.v(TAG, "finishImportFiles($result, $dataCount, $status)")
        isImporting.value = false
        readyToImportData.value = false
        currentImportDataCount.value = dataCount
        currentImportProcess.value = status
    }

    companion object
    {
        private val TAG = DataImportViewModel::class.java.simpleName
    }
}
