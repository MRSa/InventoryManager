package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.storage.FilterState
import jp.osdn.gokigen.inventorymanager.storage.SortOrderDirection
import jp.osdn.gokigen.inventorymanager.ui.model.ListViewModel
import kotlinx.coroutines.launch

@Composable
fun ListFilterDialog(
    listViewModel: ListViewModel,
    onDismissRequest: () -> Unit,
    onApply: (FilterState) -> Unit
) {
    val filterState = listViewModel.filterState.observeAsState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.dialog_title_filtering_setting)) },
        text = {
            Column {
                ShowFilterCategoryArea(listViewModel)
                Spacer(modifier = Modifier.height(4.dp))
                ShowFilterTitleInputArea(listViewModel)
                Spacer(modifier = Modifier.height(6.dp))
                ShowFilterRatingArea(listViewModel)
                Spacer(modifier = Modifier.height(16.dp))
                ShowFilterOrderArea(listViewModel)
                Spacer(modifier = Modifier.height(0.dp))
                ShowFilterDirectionArea(listViewModel)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(filterState.value ?: FilterState())
                }
            ) {
                Text(stringResource(R.string.dialog_label_button_filter_apply))
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text(stringResource(R.string.dialog_label_button_filter_cancel))
            }
        }
    )
}

@Composable
fun ShowFilterCategoryArea(listViewModel: ListViewModel)
{
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val categories = listViewModel.categoryList.observeAsState()
    val filterState = listViewModel.filterState.observeAsState()
    var isCategoryExpanded by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = filterState.value?.isCategoryChecked ?: false,
            onCheckedChange = {
                listViewModel.setFilterState(
                    filterState.value?.copy(isCategoryChecked = it)
                )
            }
        )
        Text(
            text = stringResource(R.string.dialog_label_filter_category),
            modifier = Modifier.clickable { isCategoryExpanded = true }
        )
        Spacer(modifier = Modifier.padding((4.dp)))
        Text(
            text = if (filterState.value?.isCategoryChecked == true) {
                listViewModel.filterState.value?.selectedCategory ?: ""
            } else {
                stringResource(R.string.dialog_filter_sort_not_specified)
            },
            modifier = Modifier.clickable { isCategoryExpanded = true }
                .border(width = Dp.Hairline, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(size = 1.dp))
                .padding(6.dp)
        )
        DropdownMenu(
            expanded = isCategoryExpanded,
            onDismissRequest = { isCategoryExpanded = false },
        ) {
            categories.value?.forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category, fontSize = with(density) { 16.dp.toSp() }) },
                    onClick = {
                        scope.launch {
                            listViewModel.setFilterState(
                                filterState.value?.copy(
                                    selectedCategory = category,
                                    isCategoryChecked = true
                                )
                            )
                            isCategoryExpanded = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ShowFilterTitleInputArea(listViewModel: ListViewModel)
{
    val filterState = listViewModel.filterState.observeAsState()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = filterState.value?.isTitleChecked ?: false,
            onCheckedChange = {
                listViewModel.setFilterState(
                    filterState.value?.copy(isTitleChecked = it)
                )
            }
        )
        Text(
            text = "${stringResource(R.string.label_title)} : ",
            modifier = Modifier.clickable {
                val checked = filterState.value?.isTitleChecked ?: false
                listViewModel.setFilterState(
                    filterState.value?.copy(isTitleChecked = !checked)
                )
            }
        )
        Spacer(modifier = Modifier.padding((4.dp)))
        TextField(
            enabled = true,
            value = filterState.value?.targetTitle ?: "",
            singleLine = true,
            onValueChange = { value -> listViewModel.setFilterState(filterState.value?.copy(isTitleChecked = true, targetTitle = value)) },
        )
    }
}



@Composable
fun ShowFilterRatingArea(listViewModel: ListViewModel)
{
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val filterState = listViewModel.filterState.observeAsState()
    var isOperatorExpanded by remember { mutableStateOf(false) }

    val operatorSelection = arrayListOf(
        stringResource(R.string.label_operation_equal),
        stringResource(R.string.label_operation_not_equal),
        stringResource(R.string.label_operation_over),
        stringResource(R.string.label_operation_under),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = filterState.value?.isOperatorChecked ?: false,
            onCheckedChange = {
                listViewModel.setFilterState(
                    filterState.value?.copy(isOperatorChecked = it)
                )
            }
        )
        Text(
            text = "${stringResource(R.string.label_rating)} : ",
            modifier = Modifier.clickable {
                val checked = filterState.value?.isOperatorChecked ?: false
                listViewModel.setFilterState(
                    filterState.value?.copy(isOperatorChecked = !checked)
                )
            }
        )
        Spacer(modifier = Modifier.padding((2.dp)))
        IconButton(
            onClick = {
                val rating = listViewModel.filterState.value?.selectedFilterRating ?: 0
                if (rating > 0) {
                    listViewModel.setFilterState(
                        filterState.value?.copy(
                            selectedFilterRating = rating - 1,
                            isOperatorChecked = true
                        )
                    )
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_exposure_neg_1_24),
                contentDescription = "-1",
                modifier = Modifier
                    .border(1.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(1.dp))
                    .padding(3.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.size(2.dp))
        Text(
            text = "${stringResource(R.string.label_rating_star)}${listViewModel.filterState.value?.selectedFilterRating ?: 0}",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.size(2.dp))
        IconButton(
            onClick = {
                val rating = listViewModel.filterState.value?.selectedFilterRating ?: 0
                if (rating < 7) {
                    listViewModel.setFilterState(
                        filterState.value?.copy(
                            selectedFilterRating = rating + 1,
                            isOperatorChecked = true
                        )
                    )
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_exposure_plus_1_24),
                contentDescription = "+1",
                modifier = Modifier
                    .border(1.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(1.dp))
                    .padding(3.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.size(2.dp))

        Box {
            Text(
                text = if (filterState.value?.isOperatorChecked == true) {
                    when (listViewModel.filterState.value?.selectedOperatorIndex) {
                        0 -> { stringResource(R.string.label_operation_equal) }
                        1 -> { stringResource(R.string.label_operation_not_equal) }
                        2 -> { stringResource(R.string.label_operation_over) }
                        3 -> { stringResource(R.string.label_operation_under) }
                        else -> { stringResource(R.string.label_operation_none) }
                    }
                } else {
                    stringResource(R.string.label_operation_none)
                },
                modifier = Modifier
                    .clickable { isOperatorExpanded = true }
                    .border(width = Dp.Hairline, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(size = 1.dp))
                    .padding(6.dp)
            )
            DropdownMenu(
                expanded = isOperatorExpanded,
                onDismissRequest = { isOperatorExpanded = false },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                operatorSelection.forEachIndexed { index, operator ->
                    DropdownMenuItem(
                        text = { Text(text = operator, fontSize = with(density) { 16.dp.toSp() }) },
                        onClick = {
                            scope.launch {
                                listViewModel.setFilterState(
                                    filterState.value?.copy(
                                        selectedOperatorIndex = index,
                                        isOperatorChecked = true
                                    )
                                )
                                isOperatorExpanded = false
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ShowFilterOrderArea(listViewModel: ListViewModel)
{
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val filterState = listViewModel.filterState.observeAsState()
    var isSortOrderExpanded by remember { mutableStateOf(false) }
    val sortOrderSelection = arrayListOf(
        stringResource(R.string.dialog_filter_sort_order_create),
        stringResource(R.string.dialog_filter_sort_order_update),
        stringResource(R.string.dialog_filter_sort_order_title),
        stringResource(R.string.dialog_filter_sort_order_author),
        stringResource(R.string.dialog_filter_sort_order_publisher),
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.dialog_label_sort_order))
        Text(
            text = when (filterState.value?.sortOrderDirection) {
                SortOrderDirection.CREATE_NEWEST -> { stringResource(R.string.dialog_filter_sort_order_create) }
                SortOrderDirection.CREATE_OLDEST -> { stringResource(R.string.dialog_filter_sort_order_create) }
                SortOrderDirection.UPDATE_NEWEST -> { stringResource(R.string.dialog_filter_sort_order_update) }
                SortOrderDirection.UPDATE_OLDEST -> { stringResource(R.string.dialog_filter_sort_order_update) }
                SortOrderDirection.TITLE_DESCENDING -> { stringResource(R.string.dialog_filter_sort_order_title) }
                SortOrderDirection.TITLE_ASCENDING -> { stringResource(R.string.dialog_filter_sort_order_title) }
                SortOrderDirection.AUTHOR_DESCENDING -> { stringResource(R.string.dialog_filter_sort_order_author) }
                SortOrderDirection.AUTHOR_ASCENDING -> { stringResource(R.string.dialog_filter_sort_order_author) }
                SortOrderDirection.PUBLISHER_DESCENDING -> { stringResource(R.string.dialog_filter_sort_order_publisher) }
                SortOrderDirection.PUBLISHER_ASCENDING -> { stringResource(R.string.dialog_filter_sort_order_publisher) }
                else -> { stringResource(R.string.dialog_filter_sort_order_update) }
            },
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable { isSortOrderExpanded = true }
                .border(width = Dp.Hairline, color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(size = 1.dp))
                .padding(6.dp)
        )
        DropdownMenu(
            expanded = isSortOrderExpanded,
            onDismissRequest = { isSortOrderExpanded = false },
        ) {
            sortOrderSelection.forEachIndexed {index, selection ->
                DropdownMenuItem(
                    text = { Text(text = selection, fontSize = with(density) { 16.dp.toSp() }) },
                    onClick = {
                        scope.launch {
                            val sortOrderDirection = when (index)
                            {
                                0 -> {   // CREATE DATE
                                    when (filterState.value?.sortOrderDirection) {
                                        SortOrderDirection.CREATE_NEWEST -> { SortOrderDirection.CREATE_NEWEST }
                                        SortOrderDirection.CREATE_OLDEST -> { SortOrderDirection.CREATE_OLDEST }
                                        SortOrderDirection.UPDATE_NEWEST -> { SortOrderDirection.CREATE_NEWEST }
                                        SortOrderDirection.UPDATE_OLDEST -> { SortOrderDirection.CREATE_OLDEST }
                                        SortOrderDirection.TITLE_DESCENDING -> { SortOrderDirection.CREATE_NEWEST }
                                        SortOrderDirection.TITLE_ASCENDING -> { SortOrderDirection.CREATE_OLDEST }
                                        SortOrderDirection.AUTHOR_DESCENDING -> { SortOrderDirection.CREATE_NEWEST }
                                        SortOrderDirection.AUTHOR_ASCENDING -> { SortOrderDirection.CREATE_OLDEST }
                                        SortOrderDirection.PUBLISHER_DESCENDING -> { SortOrderDirection.CREATE_NEWEST }
                                        SortOrderDirection.PUBLISHER_ASCENDING -> { SortOrderDirection.CREATE_OLDEST }
                                        else -> { SortOrderDirection.CREATE_NEWEST }
                                    }
                                }
                                1 -> {   // UPDATE DATE
                                    when (filterState.value?.sortOrderDirection) {
                                        SortOrderDirection.CREATE_NEWEST -> { SortOrderDirection.UPDATE_NEWEST }
                                        SortOrderDirection.CREATE_OLDEST -> { SortOrderDirection.UPDATE_OLDEST }
                                        SortOrderDirection.UPDATE_NEWEST -> { SortOrderDirection.UPDATE_NEWEST }
                                        SortOrderDirection.UPDATE_OLDEST -> { SortOrderDirection.UPDATE_OLDEST }
                                        SortOrderDirection.TITLE_DESCENDING -> { SortOrderDirection.UPDATE_NEWEST }
                                        SortOrderDirection.TITLE_ASCENDING -> { SortOrderDirection.UPDATE_OLDEST }
                                        SortOrderDirection.AUTHOR_DESCENDING -> { SortOrderDirection.UPDATE_NEWEST }
                                        SortOrderDirection.AUTHOR_ASCENDING -> { SortOrderDirection.UPDATE_OLDEST }
                                        SortOrderDirection.PUBLISHER_DESCENDING -> { SortOrderDirection.UPDATE_NEWEST }
                                        SortOrderDirection.PUBLISHER_ASCENDING -> { SortOrderDirection.UPDATE_OLDEST }
                                        else -> { SortOrderDirection.UPDATE_NEWEST }
                                    }
                                }
                                2 -> {   // TITLE
                                    when (filterState.value?.sortOrderDirection) {
                                        SortOrderDirection.CREATE_NEWEST -> { SortOrderDirection.TITLE_DESCENDING }
                                        SortOrderDirection.CREATE_OLDEST -> { SortOrderDirection.TITLE_ASCENDING }
                                        SortOrderDirection.UPDATE_NEWEST -> { SortOrderDirection.TITLE_DESCENDING }
                                        SortOrderDirection.UPDATE_OLDEST -> { SortOrderDirection.TITLE_ASCENDING }
                                        SortOrderDirection.TITLE_DESCENDING -> { SortOrderDirection.TITLE_DESCENDING }
                                        SortOrderDirection.TITLE_ASCENDING -> { SortOrderDirection.TITLE_ASCENDING }
                                        SortOrderDirection.AUTHOR_DESCENDING -> { SortOrderDirection.TITLE_DESCENDING }
                                        SortOrderDirection.AUTHOR_ASCENDING -> { SortOrderDirection.TITLE_ASCENDING }
                                        SortOrderDirection.PUBLISHER_DESCENDING -> { SortOrderDirection.TITLE_DESCENDING }
                                        SortOrderDirection.PUBLISHER_ASCENDING -> { SortOrderDirection.TITLE_ASCENDING }
                                        else -> { SortOrderDirection.TITLE_DESCENDING }
                                    }
                                }
                                3 -> {   // AUTHOR
                                    when (filterState.value?.sortOrderDirection) {
                                        SortOrderDirection.CREATE_NEWEST -> { SortOrderDirection.AUTHOR_DESCENDING }
                                        SortOrderDirection.CREATE_OLDEST -> { SortOrderDirection.AUTHOR_ASCENDING }
                                        SortOrderDirection.UPDATE_NEWEST -> { SortOrderDirection.AUTHOR_DESCENDING }
                                        SortOrderDirection.UPDATE_OLDEST -> { SortOrderDirection.AUTHOR_ASCENDING }
                                        SortOrderDirection.TITLE_DESCENDING -> { SortOrderDirection.AUTHOR_DESCENDING }
                                        SortOrderDirection.TITLE_ASCENDING -> { SortOrderDirection.AUTHOR_ASCENDING }
                                        SortOrderDirection.AUTHOR_DESCENDING -> { SortOrderDirection.AUTHOR_DESCENDING }
                                        SortOrderDirection.AUTHOR_ASCENDING -> { SortOrderDirection.AUTHOR_ASCENDING }
                                        SortOrderDirection.PUBLISHER_DESCENDING -> { SortOrderDirection.AUTHOR_DESCENDING }
                                        SortOrderDirection.PUBLISHER_ASCENDING -> { SortOrderDirection.AUTHOR_ASCENDING }
                                        else -> { SortOrderDirection.AUTHOR_DESCENDING }
                                    }
                                }
                                4 -> {   // PUBLISHER
                                    when (filterState.value?.sortOrderDirection) {
                                        SortOrderDirection.CREATE_NEWEST -> { SortOrderDirection.PUBLISHER_DESCENDING}
                                        SortOrderDirection.CREATE_OLDEST -> { SortOrderDirection.PUBLISHER_ASCENDING }
                                        SortOrderDirection.UPDATE_NEWEST -> { SortOrderDirection.PUBLISHER_DESCENDING }
                                        SortOrderDirection.UPDATE_OLDEST -> { SortOrderDirection.PUBLISHER_ASCENDING }
                                        SortOrderDirection.TITLE_DESCENDING -> { SortOrderDirection.PUBLISHER_DESCENDING }
                                        SortOrderDirection.TITLE_ASCENDING -> { SortOrderDirection.PUBLISHER_ASCENDING }
                                        SortOrderDirection.AUTHOR_DESCENDING -> { SortOrderDirection.PUBLISHER_DESCENDING }
                                        SortOrderDirection.AUTHOR_ASCENDING -> { SortOrderDirection.PUBLISHER_ASCENDING }
                                        SortOrderDirection.PUBLISHER_DESCENDING -> { SortOrderDirection.PUBLISHER_DESCENDING }
                                        SortOrderDirection.PUBLISHER_ASCENDING -> { SortOrderDirection.PUBLISHER_ASCENDING }
                                        else -> { SortOrderDirection.PUBLISHER_DESCENDING }
                                    }
                                }
                                else -> {  // OTHER (CREATE DATE)
                                    when (filterState.value?.sortOrderDirection) {
                                        SortOrderDirection.CREATE_NEWEST -> { SortOrderDirection.CREATE_NEWEST }
                                        SortOrderDirection.CREATE_OLDEST -> { SortOrderDirection.CREATE_OLDEST }
                                        SortOrderDirection.UPDATE_NEWEST -> { SortOrderDirection.CREATE_NEWEST }
                                        SortOrderDirection.UPDATE_OLDEST -> { SortOrderDirection.CREATE_OLDEST }
                                        SortOrderDirection.TITLE_DESCENDING -> { SortOrderDirection.CREATE_NEWEST }
                                        SortOrderDirection.TITLE_ASCENDING -> { SortOrderDirection.CREATE_OLDEST }
                                        SortOrderDirection.AUTHOR_DESCENDING -> { SortOrderDirection.CREATE_NEWEST }
                                        SortOrderDirection.AUTHOR_ASCENDING -> { SortOrderDirection.CREATE_OLDEST }
                                        SortOrderDirection.PUBLISHER_DESCENDING -> { SortOrderDirection.CREATE_NEWEST }
                                        SortOrderDirection.PUBLISHER_ASCENDING -> { SortOrderDirection.CREATE_OLDEST }
                                        else -> { SortOrderDirection.CREATE_NEWEST }
                                    }
                                }
                            }
                            listViewModel.setFilterState(
                                filterState.value?.copy(
                                    sortOrderDirection = sortOrderDirection
                                )
                            )
                            isSortOrderExpanded = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ShowFilterDirectionArea(listViewModel: ListViewModel)
{
    val filterState = listViewModel.filterState.observeAsState()
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = filterState.value?.sortOrderDirection in setOf(
                SortOrderDirection.CREATE_NEWEST,
                SortOrderDirection.UPDATE_NEWEST,
                SortOrderDirection.TITLE_DESCENDING,
                SortOrderDirection.AUTHOR_DESCENDING,
                SortOrderDirection.PUBLISHER_DESCENDING,
            ),
            onClick = {
                listViewModel.setFilterState(
                    filterState.value?.copy(
                        sortOrderDirection = when (filterState.value?.sortOrderDirection) {
                            SortOrderDirection.CREATE_NEWEST -> { SortOrderDirection.CREATE_NEWEST }
                            SortOrderDirection.CREATE_OLDEST -> { SortOrderDirection.CREATE_NEWEST }
                            SortOrderDirection.UPDATE_NEWEST -> { SortOrderDirection.UPDATE_NEWEST }
                            SortOrderDirection.UPDATE_OLDEST -> { SortOrderDirection.UPDATE_NEWEST }
                            SortOrderDirection.TITLE_DESCENDING -> { SortOrderDirection.TITLE_DESCENDING }
                            SortOrderDirection.TITLE_ASCENDING -> { SortOrderDirection.TITLE_DESCENDING }
                            SortOrderDirection.AUTHOR_DESCENDING -> { SortOrderDirection.AUTHOR_DESCENDING }
                            SortOrderDirection.AUTHOR_ASCENDING -> { SortOrderDirection.AUTHOR_DESCENDING }
                            SortOrderDirection.PUBLISHER_DESCENDING -> { SortOrderDirection.PUBLISHER_DESCENDING }
                            SortOrderDirection.PUBLISHER_ASCENDING -> { SortOrderDirection.PUBLISHER_DESCENDING }
                            else -> { SortOrderDirection.CREATE_NEWEST }
                        }
                    )
                )
            }
        )
        Text(
            text = when (filterState.value?.sortOrderDirection) {
                SortOrderDirection.CREATE_NEWEST -> { stringResource(R.string.dialog_filter_sort_order_newest) }
                SortOrderDirection.CREATE_OLDEST -> { stringResource(R.string.dialog_filter_sort_order_newest) }
                SortOrderDirection.UPDATE_NEWEST -> { stringResource(R.string.dialog_filter_sort_order_newest) }
                SortOrderDirection.UPDATE_OLDEST -> { stringResource(R.string.dialog_filter_sort_order_newest) }
                SortOrderDirection.TITLE_DESCENDING -> { stringResource(R.string.dialog_filter_sort_order_descending) }
                SortOrderDirection.TITLE_ASCENDING -> { stringResource(R.string.dialog_filter_sort_order_descending) }
                SortOrderDirection.AUTHOR_DESCENDING -> { stringResource(R.string.dialog_filter_sort_order_descending) }
                SortOrderDirection.AUTHOR_ASCENDING -> { stringResource(R.string.dialog_filter_sort_order_descending) }
                SortOrderDirection.PUBLISHER_DESCENDING -> { stringResource(R.string.dialog_filter_sort_order_descending) }
                SortOrderDirection.PUBLISHER_ASCENDING -> { stringResource(R.string.dialog_filter_sort_order_descending) }
                else -> { stringResource(R.string.dialog_filter_sort_order_newest) }
            },
            modifier = Modifier.clickable {
                listViewModel.setFilterState(
                    filterState.value?.copy(
                        sortOrderDirection = when (filterState.value?.sortOrderDirection) {
                            SortOrderDirection.CREATE_NEWEST -> { SortOrderDirection.CREATE_NEWEST }
                            SortOrderDirection.CREATE_OLDEST -> { SortOrderDirection.CREATE_NEWEST }
                            SortOrderDirection.UPDATE_NEWEST -> { SortOrderDirection.UPDATE_NEWEST }
                            SortOrderDirection.UPDATE_OLDEST -> { SortOrderDirection.UPDATE_NEWEST }
                            SortOrderDirection.TITLE_DESCENDING -> { SortOrderDirection.TITLE_DESCENDING }
                            SortOrderDirection.TITLE_ASCENDING -> { SortOrderDirection.TITLE_DESCENDING }
                            SortOrderDirection.AUTHOR_DESCENDING -> { SortOrderDirection.AUTHOR_DESCENDING }
                            SortOrderDirection.AUTHOR_ASCENDING -> { SortOrderDirection.AUTHOR_DESCENDING }
                            SortOrderDirection.PUBLISHER_DESCENDING -> { SortOrderDirection.PUBLISHER_DESCENDING }
                            SortOrderDirection.PUBLISHER_ASCENDING -> { SortOrderDirection.PUBLISHER_DESCENDING }
                            else -> { SortOrderDirection.CREATE_NEWEST }
                        }
                    )
                )
            }
        )
        RadioButton(
            selected = filterState.value?.sortOrderDirection in setOf(
                SortOrderDirection.CREATE_OLDEST,
                SortOrderDirection.UPDATE_OLDEST,
                SortOrderDirection.TITLE_ASCENDING,
                SortOrderDirection.AUTHOR_ASCENDING,
                SortOrderDirection.PUBLISHER_ASCENDING,
            ),
            onClick = {
                listViewModel.setFilterState(
                    filterState.value?.copy(
                        sortOrderDirection = when (filterState.value?.sortOrderDirection) {
                            SortOrderDirection.CREATE_NEWEST -> { SortOrderDirection.CREATE_OLDEST }
                            SortOrderDirection.CREATE_OLDEST -> { SortOrderDirection.CREATE_OLDEST }
                            SortOrderDirection.UPDATE_NEWEST -> { SortOrderDirection.UPDATE_OLDEST }
                            SortOrderDirection.UPDATE_OLDEST -> { SortOrderDirection.UPDATE_OLDEST }
                            SortOrderDirection.TITLE_DESCENDING -> { SortOrderDirection.TITLE_ASCENDING }
                            SortOrderDirection.TITLE_ASCENDING -> { SortOrderDirection.TITLE_ASCENDING }
                            SortOrderDirection.AUTHOR_DESCENDING -> { SortOrderDirection.AUTHOR_ASCENDING }
                            SortOrderDirection.AUTHOR_ASCENDING -> { SortOrderDirection.AUTHOR_ASCENDING }
                            SortOrderDirection.PUBLISHER_DESCENDING -> { SortOrderDirection.PUBLISHER_ASCENDING }
                            SortOrderDirection.PUBLISHER_ASCENDING -> { SortOrderDirection.PUBLISHER_ASCENDING }
                            else -> { SortOrderDirection.CREATE_OLDEST }
                        }
                    )
                )
            }
        )
        Text(
            text = when (filterState.value?.sortOrderDirection) {
                SortOrderDirection.CREATE_NEWEST -> { stringResource(R.string.dialog_filter_sort_order_oldest) }
                SortOrderDirection.CREATE_OLDEST -> { stringResource(R.string.dialog_filter_sort_order_oldest) }
                SortOrderDirection.UPDATE_NEWEST -> { stringResource(R.string.dialog_filter_sort_order_oldest) }
                SortOrderDirection.UPDATE_OLDEST -> { stringResource(R.string.dialog_filter_sort_order_oldest) }
                SortOrderDirection.TITLE_DESCENDING -> { stringResource(R.string.dialog_filter_sort_order_ascending) }
                SortOrderDirection.TITLE_ASCENDING -> { stringResource(R.string.dialog_filter_sort_order_ascending) }
                SortOrderDirection.AUTHOR_DESCENDING -> { stringResource(R.string.dialog_filter_sort_order_ascending) }
                SortOrderDirection.AUTHOR_ASCENDING -> { stringResource(R.string.dialog_filter_sort_order_ascending) }
                SortOrderDirection.PUBLISHER_DESCENDING -> { stringResource(R.string.dialog_filter_sort_order_ascending) }
                SortOrderDirection.PUBLISHER_ASCENDING -> { stringResource(R.string.dialog_filter_sort_order_ascending) }
                else -> { stringResource(R.string.dialog_filter_sort_order_oldest) }
            },
            modifier = Modifier.clickable {
                listViewModel.setFilterState(
                    filterState.value?.copy(
                        sortOrderDirection = when (filterState.value?.sortOrderDirection) {
                            SortOrderDirection.CREATE_NEWEST -> { SortOrderDirection.CREATE_OLDEST }
                            SortOrderDirection.CREATE_OLDEST -> { SortOrderDirection.CREATE_OLDEST }
                            SortOrderDirection.UPDATE_NEWEST -> { SortOrderDirection.UPDATE_OLDEST }
                            SortOrderDirection.UPDATE_OLDEST -> { SortOrderDirection.UPDATE_OLDEST }
                            SortOrderDirection.TITLE_DESCENDING -> { SortOrderDirection.TITLE_ASCENDING }
                            SortOrderDirection.TITLE_ASCENDING -> { SortOrderDirection.TITLE_ASCENDING }
                            SortOrderDirection.AUTHOR_DESCENDING -> { SortOrderDirection.AUTHOR_ASCENDING }
                            SortOrderDirection.AUTHOR_ASCENDING -> { SortOrderDirection.AUTHOR_ASCENDING }
                            SortOrderDirection.PUBLISHER_DESCENDING -> { SortOrderDirection.PUBLISHER_ASCENDING }
                            SortOrderDirection.PUBLISHER_ASCENDING -> { SortOrderDirection.PUBLISHER_ASCENDING }
                            else -> { SortOrderDirection.CREATE_OLDEST }
                        }
                    )
                )
            }
        )
    }
}
