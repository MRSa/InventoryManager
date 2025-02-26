package jp.osdn.gokigen.inventorymanager.ui.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.export.DataExporter
import jp.osdn.gokigen.inventorymanager.recognize.IRecognizeDataFromInternetCallback
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeDataProgress
import jp.osdn.gokigen.inventorymanager.storage.DataContent
import jp.osdn.gokigen.inventorymanager.storage.FilterState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewModel: ViewModel(), DataExporter.IExportProgressCallback,
    IRecognizeDataFromInternetCallback
{
    private val storageDao = AppSingleton.db.storageDao()
    val dataList = mutableStateListOf<DataContent>()

    private val categories = MutableLiveData<List<String>>()
    val categoryList: LiveData<List<String>> = categories

    private val filterStatus = MutableLiveData<FilterState>()
    val filterState: LiveData<FilterState> = filterStatus

    private val isUpdatingFromIsbn : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isUpdatingDataFromIsbn: LiveData<Boolean> = isUpdatingFromIsbn

    private val isRefreshing : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val refreshingData: LiveData<Boolean> = isRefreshing

    private val enableFilter : MutableLiveData<FilterDialogCondition> by lazy { MutableLiveData<FilterDialogCondition>() }
    val filterSetting: LiveData<FilterDialogCondition> = enableFilter

    private val isFilterApply : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isFilterApplying: LiveData<Boolean> = isFilterApply

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

    private val recognizeProgressStatus : MutableLiveData<RecognizeDataProgress> by lazy { MutableLiveData<RecognizeDataProgress>() }
    val recognizeStatus: LiveData<RecognizeDataProgress> = recognizeProgressStatus

    private val recognizeProgressCount : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val currentRecognizeProgressCount: LiveData<Int> = recognizeProgressCount

    private val totalRecognizeCount : MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val currentTotalRecognizeCount: LiveData<Int> = totalRecognizeCount

    fun initializeViewModel()
    {
        try
        {
            isUpdatingFromIsbn.value = false
            isRefreshing.value = false
            isExporting.value = false
            enableFilter.value = FilterDialogCondition.READY
            exportingProgress.value = 0.0f
            lastExportedFileCount.value = 0
            lastTotalExportedFileCount.value = 0
            listCount.value = 0
            isFilterApply.value = false
            filterStatus.value = FilterState()
            recognizeProgressCount.value = 0
            totalRecognizeCount.value = 0
            recognizeProgressStatus.value = RecognizeDataProgress.READY
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
                enableFilter.value = FilterDialogCondition.DISABLE
                dataList.clear()
                withContext(Dispatchers.Default) {
                    storageDao.getAllCreateLatest().forEach { data ->
                        dataList.add(data)
                    }
                }
                isRefreshing.value = false
                enableFilter.value = FilterDialogCondition.READY
            }
            listCount.value = dataList.size
            isFilterApply.value = false
            filterStatus.value = FilterState()
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

    fun setFilterDialogCondition(value: FilterDialogCondition)
    {
        enableFilter.value = value
        if (value == FilterDialogCondition.POSTPROCESSING)
        {
            // --- 仮に実装...フィルタ適用中マーキング
            isFilterApply.value = true
            enableFilter.value = FilterDialogCondition.READY
        }
    }

    fun prepareToShowFilterSettingDialog()
    {
        Thread {
            try
            {
                // ----- Roomは、別Thread で実行しないとダメ...
                val listOfCategory = storageDao.getCategories()
                CoroutineScope(Dispatchers.Main).launch {
                    // ---- これで値を設定できたはずだが...
                    categories.value = listOfCategory
                    enableFilter.value = FilterDialogCondition.SHOWING

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

    fun setFilterState(filterState: FilterState?)
    {
        if (filterState == null)
        {
            Log.v(TAG, "setFilterState(): NULL...")
            return
        }
        filterStatus.value = filterState.copy()
    }

    fun applyFilter(filterState: FilterState)
    {
        Log.v(TAG, "applyFilter() : $filterState")
        try
        {
            CoroutineScope(Dispatchers.Main).launch {
                enableFilter.value = FilterDialogCondition.POSTPROCESSING
                val refresh = isRefreshing.value ?: false
                if (!refresh)
                {
                    isRefreshing.value = true
                    enableFilter.value = FilterDialogCondition.DISABLE
                    dataList.clear()
                    withContext(Dispatchers.Default) {
                        storageDao.getDataListWithFilter(filterState).forEach { data ->
                            dataList.add(data)
                        }
                    }
                    isRefreshing.value = false
                }
                listCount.value = dataList.size
                isFilterApply.value = true
                enableFilter.value = FilterDialogCondition.READY
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
        enableFilter.value = FilterDialogCondition.DISABLE
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
            enableFilter.value = FilterDialogCondition.READY
            exportingProgress.value = 0.0f
            lastTotalExportedFileCount.value = totalFile
            lastExportedFileCount.value = totalFile - exportNG
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun startRecognizeFromInternet()
    {
        try
        {
            isUpdatingFromIsbn.value = true
            enableFilter.value = FilterDialogCondition.DISABLE
            recognizeProgressCount.value = 0
            totalRecognizeCount.value = 0
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun progressRecognizeFromInternet(status: RecognizeDataProgress, count: Int, totalCount: Int)
    {
        try
        {
            recognizeProgressCount.value = count
            totalRecognizeCount.value = totalCount
            recognizeProgressStatus.value = status
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun finishRecognizeFromInternet()
    {
        isUpdatingFromIsbn.value = false
        enableFilter.value = FilterDialogCondition.READY

        recognizeProgressCount.value = 0
        totalRecognizeCount.value = 0
        recognizeProgressStatus.value = RecognizeDataProgress.READY

        update() // 情報の更新をしたときには、一覧の情報を更新する (フィルタも強制解除)
    }

    enum class FilterDialogCondition {
        DISABLE, READY, PREPARING, SHOWING, POSTPROCESSING
    }

    companion object
    {
        private val TAG = ListViewModel::class.java.simpleName
    }
}
