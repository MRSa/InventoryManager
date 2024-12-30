package jp.osdn.gokigen.inventorymanager.ui.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.export.DataExporter
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeFromIsbn
import jp.osdn.gokigen.inventorymanager.storage.DataContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryViewModel: ViewModel(), DataExporter.IExportProgressCallback, RecognizeFromIsbn.RecognizeDataFromIsbnCallback
{
    private val storageDao = AppSingleton.db.storageDao()
    val dataList = mutableStateListOf<DataContent>()

    private val isUpdatingFromIsbn : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isUpdatingDataFromIsbn: LiveData<Boolean> = isUpdatingFromIsbn

    private val isRefreshing : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val refreshingData: LiveData<Boolean> = isRefreshing

    private val isExporting : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val dataExporting: LiveData<Boolean> = isExporting

    private val exportingProgress : MutableLiveData<Float> by lazy { MutableLiveData<Float>() }
    val exportingProgressPercent: LiveData<Float> = exportingProgress

    private val lastExportedFileCount : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val lastExportFileCount: LiveData<Int> = lastExportedFileCount

    private val lastTotalExportedFileCount : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val lastExportTotalFileCount: LiveData<Int> = lastTotalExportedFileCount

    private val listCount : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val dataListCount: LiveData<Int> = listCount

    private val filterInformation : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val listFilterInformation: LiveData<String> = filterInformation

    fun initializeViewModel()
    {
        try
        {
            isUpdatingFromIsbn.value = false
            isRefreshing.value = false
            isExporting.value = false
            exportingProgress.value = 0.0f
            lastExportedFileCount.value = 0
            lastTotalExportedFileCount.value = 0
            listCount.value = 0
            filterInformation.value = ""
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun update()
    {
        CoroutineScope(Dispatchers.Main).launch {
            val refresh = isRefreshing.value ?: false
            if (!refresh)
            {
                isRefreshing.value = true
                dataList.clear()
                withContext(Dispatchers.Default) {
                    storageDao.getAll().forEach { data ->
                        dataList.add(data)
                    }
                }
                isRefreshing.value = false
            }
            listCount.value = dataList.size
        }
    }

    fun refresh()
    {
        try
        {
            Log.v(TAG, "START REFRESH...")
            val refresh = isRefreshing.value ?: false
            if (!refresh)
            {
                update()
                Log.v(TAG, "DATA REFRESHED : ${dataList.count()}")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun startExportFile(fileName: String) {
        Log.v(TAG, "startExportFile(): $fileName")
        isExporting.value = true
        exportingProgress.value = 0.0f
    }

    override fun progressExportFile(currentFileCount: Int, totalFileCount: Int) {
        val percent = (currentFileCount.toFloat() / totalFileCount.toFloat()) * 100.0f
        Log.v(TAG, " progressExportFile() $currentFileCount/$totalFileCount (${String.format("%.1f", percent)} %)")
        exportingProgress.value = percent
        lastExportedFileCount.value = currentFileCount
        lastTotalExportedFileCount.value = totalFileCount
    }

    override fun finishExportFile(
        fileName: String,
        exportNG: Int,
        totalFile: Int,
        archiveOnlyOneFile: Boolean
    ) {
        Log.v(TAG, "finishExportFile : $fileName, NG:$exportNG (total:$totalFile) one file: $archiveOnlyOneFile")
        try
        {
            isExporting.value = false
            exportingProgress.value = 0.0f
            lastTotalExportedFileCount.value = totalFile
            lastExportedFileCount.value = totalFile - exportNG
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun startRecognizeFromIsbn()
    {
        isUpdatingFromIsbn.value = true
    }

    override fun finishRecognizeFromIsbn()
    {
        isUpdatingFromIsbn.value = false
    }

    companion object
    {
        private val TAG = InventoryViewModel::class.java.simpleName
    }
}
