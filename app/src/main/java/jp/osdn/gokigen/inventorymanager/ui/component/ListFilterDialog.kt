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
    val sortOrderSelection = arrayListOf(stringResource(R.string.dialog_filter_sort_order_create), stringResource(R.string.dialog_filter_sort_order_update))

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.dialog_label_sort_order))
        Text(
            text = if (filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_OLDEST) {
                stringResource(R.string.dialog_filter_sort_order_create)
            } else {
                stringResource(R.string.dialog_filter_sort_order_update)
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
                            val sortOrderDirection = if (index == 0)
                            {
                                // ---- Create Date
                                if (filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == SortOrderDirection.UPDATE_NEWEST) {
                                    SortOrderDirection.CREATE_NEWEST
                                }
                                else
                                {
                                    SortOrderDirection.CREATE_OLDEST
                                }
                            }
                            else
                            {
                                // ---- Update Date
                                if (filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == SortOrderDirection.UPDATE_NEWEST) {
                                    SortOrderDirection.UPDATE_NEWEST
                                }
                                else
                                {
                                    SortOrderDirection.UPDATE_OLDEST
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
                SortOrderDirection.UPDATE_NEWEST
            ),
            onClick = {
                listViewModel.setFilterState(
                    filterState.value?.copy(
                        sortOrderDirection = if (filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_OLDEST) {
                            SortOrderDirection.CREATE_NEWEST
                        } else {
                            SortOrderDirection.UPDATE_NEWEST
                        }
                    )
                )
            }
        )
        Text(
            text = stringResource(R.string.dialog_filter_sort_order_newest),
            modifier = Modifier.clickable {
                listViewModel.setFilterState(
                    filterState.value?.copy(
                        sortOrderDirection = if (filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_OLDEST) {
                            SortOrderDirection.CREATE_NEWEST
                        } else {
                            SortOrderDirection.UPDATE_NEWEST
                        }
                    )
                )
            }
        )
        RadioButton(
            selected = filterState.value?.sortOrderDirection in setOf(
                SortOrderDirection.CREATE_OLDEST,
                SortOrderDirection.UPDATE_OLDEST
            ),
            onClick = {
                listViewModel.setFilterState(
                    filterState.value?.copy(
                        sortOrderDirection = if (filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_OLDEST) {
                            SortOrderDirection.CREATE_OLDEST
                        } else {
                            SortOrderDirection.UPDATE_OLDEST
                        }
                    )
                )
            }
        )
        Text(
            text = stringResource(R.string.dialog_filter_sort_order_oldest),
            modifier = Modifier.clickable {
                listViewModel.setFilterState(
                    filterState.value?.copy(
                        sortOrderDirection = if (filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == SortOrderDirection.CREATE_OLDEST) {
                            SortOrderDirection.CREATE_OLDEST
                        } else {
                            SortOrderDirection.UPDATE_OLDEST
                        }
                    )
                )
            }
        )
    }
}
