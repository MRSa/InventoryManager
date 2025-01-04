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

class ListViewModel: ViewModel(), DataExporter.IExportProgressCallback, RecognizeFromIsbn.RecognizeDataFromIsbnCallback
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
        val updateFilterState = FilterState()
        updateFilterState.isCategoryChecked = filterState.isCategoryChecked
        updateFilterState.selectedCategory = filterState.selectedCategory
        updateFilterState.sortOrderDirection = filterState.sortOrderDirection
        updateFilterState.isOperatorChecked = filterState.isOperatorChecked
        updateFilterState.selectedOperatorIndex = filterState.selectedOperatorIndex
        updateFilterState.selectedFilterRating = filterState.selectedFilterRating
        filterStatus.value = updateFilterState
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
                        if ((!filterState.isCategoryChecked)&&(!filterState.isOperatorChecked))
                        {
                            applySortOrder(filterState)
                        }
                        else if ((filterState.isCategoryChecked)&&(filterState.isOperatorChecked))
                        {
                            applyFilterCategoryAndRating(filterState)
                        }
                        else if (filterState.isCategoryChecked) // &&(!filterState.isOperatorChecked)
                        {
                            applyFilterCategory(filterState)
                        }
                        else // if ((!filterState.isCategoryChecked)&&(filterState.isOperatorChecked))
                        {
                            applyFilterRating(filterState)
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

    private fun applySortOrder(filterState: FilterState)
    {
        try
        {
            when (filterState.sortOrderDirection) {
                SortOrderDirection.CREATE_OLDEST -> {
                    storageDao.getAllOrderBy(isUpdateDate = false, isAscending = true)
                        .forEach { data ->
                            dataList.add(data)
                        }
                }

                SortOrderDirection.CREATE_NEWEST -> {
                    storageDao.getAllOrderBy(isUpdateDate = false, isAscending = false)
                        .forEach { data ->
                            dataList.add(data)
                        }
                }

                SortOrderDirection.UPDATE_OLDEST -> {
                    storageDao.getAllOrderBy(isUpdateDate = true, isAscending = true)
                        .forEach { data ->
                            dataList.add(data)
                        }
                }

                SortOrderDirection.UPDATE_NEWEST -> {
                    storageDao.getAllOrderBy(isUpdateDate = true, isAscending = true)
                        .forEach { data ->
                            dataList.add(data)
                        }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun applyFilterCategory(filterState: FilterState)
    {
        try
        {
            when (filterState.sortOrderDirection) {
                SortOrderDirection.CREATE_OLDEST -> {
                    storageDao.findByCategoryOrderBy(
                        category = filterState.selectedCategory,
                        isUpdateDate = false,
                        isAscending = true
                    ).forEach { data ->
                        dataList.add(data)
                    }
                }

                SortOrderDirection.CREATE_NEWEST -> {
                    storageDao.findByCategoryOrderBy(
                        category = filterState.selectedCategory,
                        isUpdateDate = false,
                        isAscending = false
                    ).forEach { data ->
                        dataList.add(data)
                    }
                }

                SortOrderDirection.UPDATE_OLDEST -> {
                    storageDao.findByCategoryOrderBy(
                        category = filterState.selectedCategory,
                        isUpdateDate = true,
                        isAscending = true
                    ).forEach { data ->
                        dataList.add(data)
                    }
                }

                SortOrderDirection.UPDATE_NEWEST -> {
                    storageDao.findByCategoryOrderBy(
                        category = filterState.selectedCategory,
                        isUpdateDate = true,
                        isAscending = true
                    ).forEach { data ->
                        dataList.add(data)
                    }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun applyFilterRating(filterState: FilterState)
    {
        try
        {
            when (filterState.sortOrderDirection) {
                SortOrderDirection.CREATE_OLDEST -> {
                    when (filterState.selectedOperatorIndex) {
                        0 -> { // Operator : =
                            storageDao.findByRatingEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        1 -> { // Operator : !=
                            storageDao.findByRatingNotEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        2 -> { // Operator : >=
                            storageDao.findByRatingOverOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        3 -> { // Operator : <=
                            storageDao.findByRatingUnderOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        else -> {  // Operator : =
                            storageDao.findByRatingEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }
                    }
                }

                SortOrderDirection.CREATE_NEWEST -> {
                    when (filterState.selectedOperatorIndex) {
                        0 -> { // Operator : =
                            storageDao.findByRatingEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        1 -> { // Operator : !=
                            storageDao.findByRatingNotEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        2 -> { // Operator : >=
                            storageDao.findByRatingOverOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        3 -> { // Operator : <=
                            storageDao.findByRatingUnderOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        else -> {  // Operator : =
                            storageDao.findByRatingEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }
                    }
                }

                SortOrderDirection.UPDATE_OLDEST -> {
                    when (filterState.selectedOperatorIndex) {
                        0 -> { // Operator : =
                            storageDao.findByRatingEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        1 -> { // Operator : !=
                            storageDao.findByRatingNotEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        2 -> { // Operator : >=
                            storageDao.findByRatingOverOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        3 -> { // Operator : <=
                            storageDao.findByRatingUnderOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        else -> {  // Operator : =
                            storageDao.findByRatingEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }
                    }
                }

                SortOrderDirection.UPDATE_NEWEST -> {
                    when (filterState.selectedOperatorIndex) {
                        0 -> { // Operator : =
                            storageDao.findByRatingEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        1 -> { // Operator : !=
                            storageDao.findByRatingNotEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        2 -> { // Operator : >=
                            storageDao.findByRatingOverOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        3 -> { // Operator : <=
                            storageDao.findByRatingUnderOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        else -> {  // Operator : =
                            storageDao.findByRatingEqualOrderBy(
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }
                    }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun applyFilterCategoryAndRating(filterState: FilterState)
    {
        try
        {
            when (filterState.sortOrderDirection) {
                SortOrderDirection.CREATE_OLDEST -> {
                    when (filterState.selectedOperatorIndex) {
                        0 -> { // Operator : =
                            storageDao.findByCategoryWithRatingEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        1 -> { // Operator : !=
                            storageDao.findByCategoryWithRatingNotEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        2 -> { // Operator : >=
                            storageDao.findByCategoryWithRatingOverOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        3 -> { // Operator : <=
                            storageDao.findByCategoryWithRatingUnderOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        else -> {  // Operator : =
                            storageDao.findByCategoryWithRatingEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }
                    }
                }

                SortOrderDirection.CREATE_NEWEST -> {
                    when (filterState.selectedOperatorIndex) {
                        0 -> { // Operator : =
                            storageDao.findByCategoryWithRatingEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        1 -> { // Operator : !=
                            storageDao.findByCategoryWithRatingNotEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        2 -> { // Operator : >=
                            storageDao.findByCategoryWithRatingOverOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        3 -> { // Operator : <=
                            storageDao.findByCategoryWithRatingUnderOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        else -> {  // Operator : =
                            storageDao.findByCategoryWithRatingEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = false,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }
                    }
                }

                SortOrderDirection.UPDATE_OLDEST -> {
                    when (filterState.selectedOperatorIndex) {
                        0 -> { // Operator : =
                            storageDao.findByCategoryWithRatingEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        1 -> { // Operator : !=
                            storageDao.findByCategoryWithRatingNotEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        2 -> { // Operator : >=
                            storageDao.findByCategoryWithRatingOverOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        3 -> { // Operator : <=
                            storageDao.findByCategoryWithRatingUnderOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        else -> {  // Operator : =
                            storageDao.findByCategoryWithRatingEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = true
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }
                    }
                }

                SortOrderDirection.UPDATE_NEWEST -> {
                    when (filterState.selectedOperatorIndex) {
                        0 -> { // Operator : =
                            storageDao.findByCategoryWithRatingEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        1 -> { // Operator : !=
                            storageDao.findByCategoryWithRatingNotEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        2 -> { // Operator : >=
                            storageDao.findByCategoryWithRatingOverOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        3 -> { // Operator : <=
                            storageDao.findByCategoryWithRatingUnderOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }

                        else -> {  // Operator : =
                            storageDao.findByCategoryWithRatingEqualOrderBy(
                                category = filterState.selectedCategory,
                                rating = filterState.selectedFilterRating,
                                isUpdateDate = true,
                                isAscending = false
                            ).forEach { data ->
                                dataList.add(data)
                            }
                        }
                    }
                }
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

    override fun startRecognizeFromIsbn()
    {
        isUpdatingFromIsbn.value = true
        enableFilter.value = FilterDialogCondition.DISABLE
    }

    override fun finishRecognizeFromIsbn()
    {
        isUpdatingFromIsbn.value = false
        enableFilter.value = FilterDialogCondition.READY
    }

    data class FilterState(
        var isCategoryChecked: Boolean = false,
        var selectedCategory: String = "",
        var isOperatorChecked: Boolean = false,
        var selectedOperatorIndex: Int = 0,
        var selectedFilterRating: Int = 0,
        var sortOrderDirection: SortOrderDirection = SortOrderDirection.CREATE_NEWEST
    )

    enum class SortOrderDirection {
        CREATE_NEWEST, CREATE_OLDEST, UPDATE_NEWEST, UPDATE_OLDEST
    }

    enum class FilterDialogCondition {
        DISABLE, READY, PREPARING, SHOWING, POSTPROCESSING
    }

    companion object
    {
        private val TAG = ListViewModel::class.java.simpleName
    }
}
