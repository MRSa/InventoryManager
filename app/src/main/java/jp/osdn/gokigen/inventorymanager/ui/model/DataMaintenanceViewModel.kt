package jp.osdn.gokigen.inventorymanager.ui.model

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.import.DataImporter
import jp.osdn.gokigen.inventorymanager.import.DataImporter.ImportProcess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class DataMaintenanceViewModel: ViewModel(), DataImporter.IExtractProgress, DataImporter.IImportProgress, DataImporter.IExtractPostProcess
{
    private lateinit var contentResolver: ContentResolver

    private val storageDao = AppSingleton.db.storageDao()

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

    private val currentImportProcess : MutableLiveData<ImportProcess> by lazy { MutableLiveData<ImportProcess>() }
    val currentExecutingProcess: LiveData<ImportProcess> = currentImportProcess

    private val orgCategory : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val renameOriginalCategory : LiveData<String> = orgCategory

    private val newCategory : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val renameNewCategory : LiveData<String> = newCategory

    private val categories = MutableLiveData<List<String>>()
    val categoryList: LiveData<List<String>> = categories

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

            orgCategory.value = ""
            newCategory.value = ""
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

    fun initializeCategories()
    {
        Thread {
            try
            {
                // ----- Roomは、別Thread で実行しないとダメ...
                val listOfCategory = storageDao.getCategories()
                CoroutineScope(Dispatchers.Main).launch {
                    // ---- これで値を設定できたはずだが...
                    categories.value = listOfCategory
                    Log.v(TAG, " _____ Categories: ${categories.value?.size}")
                    val categoryList = categories.value ?: ArrayList()
                    for (category in categoryList)
                    {
                        Log.v(TAG, "  category: $category")
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }.start()
    }

    fun setRenameOriginalCategory(target: String)
    {
        try
        {
            orgCategory.value = target
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setRenameNewCategory(target: String)
    {
        try
        {
            newCategory.value = target
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun executeChangeCategory(context: Context, orgCategory: String, newCategory: String)
    {
        Thread {
            try
            {
                // ---- Roomなので別スレッドで実行する
                storageDao.updateCategory(orgCategory, newCategory, Date())
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "${context.getString(R.string.result_update_category)} $orgCategory -> $newCategory", Toast.LENGTH_SHORT).show()
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }.start()
    }

    companion object
    {
        private val TAG = DataMaintenanceViewModel::class.java.simpleName
    }
}
