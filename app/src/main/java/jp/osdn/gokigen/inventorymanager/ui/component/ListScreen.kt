package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.export.DataExporter
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeDataProgress
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeFromInternet
import jp.osdn.gokigen.inventorymanager.storage.SortOrderDirection
import jp.osdn.gokigen.inventorymanager.ui.model.ListViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.ListViewModel.FilterDialogCondition
import java.util.Locale.US

@Composable
fun ListScreen(navController: NavHostController, viewModel : ListViewModel, exporter: DataExporter, recognizer: RecognizeFromInternet)
{
    // 画面遷移時にデータを取得
    rememberNavController()
    LaunchedEffect(key1 = Unit) {
        viewModel.refresh()
    }

    val padding = 6.dp
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CommandPanel(navController, viewModel, exporter, recognizer)
            HorizontalDivider(thickness = 1.dp)
            Spacer(Modifier.size(padding))
            ReceivedContentList(navController, viewModel)
            ShowBusyDialogsAtListScreen(viewModel)
            ShowFilterConditionSettingDialog(viewModel)
        }
    }
}

@Composable
fun CommandPanel(navController: NavHostController, dataListModel : ListViewModel, exporter: DataExporter, recognizer: RecognizeFromInternet)
{
    // ----- 表示メッセージを生成する
    val listCount = dataListModel.dataListCount.observeAsState()
    val filterInfo = dataListModel.filterState.observeAsState()
    val isFilterApplying = dataListModel.isFilterApplying.observeAsState()

    val message = if (isFilterApplying.value == true) {
        val category = if (filterInfo.value?.isCategoryChecked == true) { " (${filterInfo.value?.selectedCategory})" } else { "" }
        val filterTitle = if (filterInfo.value?.isTitleChecked == true) { " [${filterInfo.value?.targetTitle}]" } else { "" }
        val ratingOperator = if (filterInfo.value?.isOperatorChecked == true) {
            when (filterInfo.value?.selectedOperatorIndex) {
                0 -> { stringResource(R.string.filter_operation_equal) }
                1 -> { stringResource(R.string.filter_operation_not_equal) }
                2 -> { stringResource(R.string.filter_operation_over) }
                3 -> { stringResource(R.string.filter_operation_under) }
                else -> { "" }
            }
        }
        else { "" }
        val rating = if (filterInfo.value?.isOperatorChecked == true) { " $ratingOperator ${stringResource(R.string.label_rating_star)}${filterInfo.value?.selectedFilterRating}" } else { "" }
        val direction = when (filterInfo.value?.sortOrderDirection) {
            SortOrderDirection.CREATE_NEWEST -> { stringResource(R.string.dialog_info_sort_order_newest) }
            SortOrderDirection.CREATE_OLDEST -> { stringResource(R.string.dialog_info_sort_order_oldest) }
            SortOrderDirection.UPDATE_NEWEST -> { stringResource(R.string.dialog_info_sort_order_newest) }
            SortOrderDirection.UPDATE_OLDEST -> { stringResource(R.string.dialog_info_sort_order_oldest) }
            SortOrderDirection.TITLE_DESCENDING -> { stringResource(R.string.dialog_info_sort_order_newest) }
            SortOrderDirection.TITLE_ASCENDING -> { stringResource(R.string.dialog_info_sort_order_oldest) }
            SortOrderDirection.AUTHOR_DESCENDING -> { stringResource(R.string.dialog_info_sort_order_newest) }
            SortOrderDirection.AUTHOR_ASCENDING -> { stringResource(R.string.dialog_info_sort_order_oldest) }
            SortOrderDirection.PUBLISHER_DESCENDING -> { stringResource(R.string.dialog_info_sort_order_newest) }
            SortOrderDirection.PUBLISHER_ASCENDING -> { stringResource(R.string.dialog_info_sort_order_oldest) }
            else -> { stringResource(R.string.dialog_info_sort_order_newest) }
        }
        "${stringResource(R.string.label_data_count)} ${listCount.value} $direction$category$filterTitle $rating"
    }
    else
    {
        "${stringResource(R.string.label_data_count)} ${listCount.value} "
    }
    Row()
    {
        // ----- 前画面に戻る
        IconButton(
            enabled = true,
            modifier = Modifier,
            onClick = {
                if (navController.currentBackStackEntry?.destination?.route == "ListScreen")
                {
                    navController.popBackStack()
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_24),
                contentDescription = "back to main screen")
        }

        // ----- 件数等の表示 (フィルター設定時には強調表示）
        val isFilterApply = dataListModel.isFilterApplying.observeAsState()
        Text(
            color = if (isFilterApply.value == true) { MaterialTheme.colorScheme.primary } else { MaterialTheme.colorScheme.secondary },
            fontWeight = if (isFilterApply.value == true) { FontWeight.Bold } else { FontWeight.Medium },
            textDecoration = if (isFilterApply.value == true) { TextDecoration.Underline } else { TextDecoration.None } ,
            text = message,
            modifier = Modifier.align(Alignment.CenterVertically).clickable {
                dataListModel.setFilterDialogCondition(FilterDialogCondition.PREPARING)
                dataListModel.prepareToShowFilterSettingDialog()
            }
        )

        // ----- 検索用フィルタ（設定）
        val isEnableFilter = dataListModel.filterSetting.observeAsState()
        IconButton(
            enabled = (isEnableFilter.value == FilterDialogCondition.READY),
            modifier = Modifier,
            onClick = {
                dataListModel.setFilterDialogCondition(FilterDialogCondition.PREPARING)
                dataListModel.prepareToShowFilterSettingDialog()
            })
        {
            Icon(
                painter = painterResource(R.drawable.baseline_filter_alt_24),
                tint = if (isFilterApply.value == true) { MaterialTheme.colorScheme.primary } else { MaterialTheme.colorScheme.secondary },
                contentDescription = "filter"
            )
        }
        IconButton(
            modifier = Modifier,
            onClick = { dataListModel.refresh() })
        {
            Icon(Icons.Filled.Refresh, contentDescription = "Information")
        }

        // ----- ちょっとすきまをあける
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.weight(1f))

        // ----- ISBNデータから題名等を取得
        IconButton(
            modifier = Modifier,
            onClick = {
                recognizer.doRecognizeAllFromInternet(dataListModel)
            })
        {
            Icon(
                painter = painterResource(R.drawable.baseline_menu_book_24),
                contentDescription = "Update from ISBN")
        }
        Spacer(modifier = Modifier.weight(1f))

        // ----- 表示データのエクスポート
        IconButton(
            modifier = Modifier,
            onClick = {
                exporter.doExport(callback = dataListModel)
            })
        {
            Icon(
                painter = painterResource(R.drawable.baseline_save_alt_24),
                contentDescription = "export")
        }
    }
}

@Composable
fun ShowExportingDialog(dataListModel: ListViewModel)
{
    val percent = dataListModel.exportingProgressPercent.observeAsState()
    val fileCount = dataListModel.lastExportFileCount.observeAsState()
    val totalCount = dataListModel.lastExportTotalFileCount.observeAsState()
    val message = "${stringResource(R.string.label_data_exporting)} \n   ${fileCount.value}/${totalCount.value} (${String.format(US, "%.1f", percent.value)} %)"
    AlertDialog(
        onDismissRequest = { },
        title = { Text(message) },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        },
        confirmButton = { },
        dismissButton = null
    )
}

@Composable
fun ShowUpdatingDialog(message: String)
{
    AlertDialog(
        onDismissRequest = { },
        title = { Text(message) },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        },
        confirmButton = { },
        dismissButton = null
    )
}

@Composable
fun ShowBusyDialogsAtListScreen(dataListModel: ListViewModel)
{
    // ----- エクスポート中ダイアログを表示する
    val exporting = dataListModel.dataExporting.observeAsState()
    if (exporting.value == true)
    {
        ShowExportingDialog(dataListModel)
    }

    // ----- 更新中ダイアログを表示する (ISBNを使ってインターネット経由で更新）
    val updateContentFromIsbn = dataListModel.isUpdatingDataFromIsbn.observeAsState()
    val progressCount = dataListModel.currentRecognizeProgressCount.observeAsState()
    val totalCount = dataListModel.currentTotalRecognizeCount.observeAsState()
    val currentStatus = dataListModel.recognizeStatus.observeAsState()
    val currentStatusMessage = when (currentStatus.value) {
        RecognizeDataProgress.CHECK_ISBN -> stringResource(R.string.label_data_updating_record)
        else -> stringResource(R.string.label_data_updating_record)
    }
    if (updateContentFromIsbn.value == true)
    {
        ShowUpdatingDialog("$currentStatusMessage (${progressCount.value} / ${totalCount.value})")
    }

    // ----- データ更新中ダイアログを表示する （データベースを取得する）
    val information = dataListModel.refreshingData.observeAsState()
    if (information.value == true)
    {
        ShowUpdatingDialog(stringResource(R.string.data_updating))
    }
}


@Composable
fun ReceivedContentList(navController: NavHostController, dataListModel: ListViewModel)
{
    val listState = rememberLazyListState()
    if (dataListModel.dataList.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(
                text = stringResource(id = R.string.data_empty),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
    else
    {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            contentPadding = PaddingValues(bottom = 80.dp), // 末尾に 80dpの余白を設ける
            state = listState
        ) {
            this.items(dataListModel.dataList) { data ->
                key(data.id) {
                    DataItem(navController, data)
                }
                HorizontalDivider(thickness = 1.dp)
            }
        }
    }
}

@Composable
fun ShowFilterConditionSettingDialog(viewModel : ListViewModel)
{
    val isEnableFilter = viewModel.filterSetting.observeAsState()
    when (isEnableFilter.value) {
        FilterDialogCondition.PREPARING -> {
            // ----- フィルター条件を出すための準備中
            val message = stringResource(R.string.dialog_title_filtering_preparing)
            AlertDialog(
                onDismissRequest = { },
                title = { Text(message) },
                text = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                confirmButton = { },
                dismissButton = null
            )
        }
        FilterDialogCondition.SHOWING -> {
            // ----- フィルタ条件を設定する
            ListFilterDialog(
                listViewModel = viewModel,
                onApply = { newState -> viewModel.applyFilter(newState) },
                onDismissRequest = { viewModel.setFilterDialogCondition(FilterDialogCondition.READY) }
            )
        }
        FilterDialogCondition.POSTPROCESSING -> {
            // ----- フィルター条件を反映中
            val message = stringResource(R.string.dialog_title_filter_applying)
            AlertDialog(
                onDismissRequest = { },
                title = { Text(message) },
                text = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                confirmButton = { },
                dismissButton = null
            )
        }
        else -> { }
    }
}
