package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.ui.model.ListViewModel
import kotlinx.coroutines.launch

@Composable
fun ListFilterDialog(
    listViewModel: ListViewModel,
    onDismissRequest: () -> Unit,
    onApply: (ListViewModel.FilterState) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val categories = listViewModel.categoryList.observeAsState()
    val filterState = listViewModel.filterState.observeAsState()
    var isCategoryExpanded by remember { mutableStateOf(false) }
    var isSortOrderExpanded by remember { mutableStateOf(false) }
    val sortOrderSelection = arrayListOf(stringResource(R.string.dialog_filter_sort_order_create), stringResource(R.string.dialog_filter_sort_order_update))

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.dialog_title_filtering_setting)) },
        text = {
            Column {
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
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.dialog_label_sort_order))
                    Text(
                        text = if (filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_OLDEST) {
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
                                            if (filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.UPDATE_NEWEST) {
                                                ListViewModel.SortOrderDirection.CREATE_NEWEST
                                            }
                                            else
                                            {
                                                ListViewModel.SortOrderDirection.CREATE_OLDEST
                                            }
                                        }
                                        else
                                        {
                                            // ---- Update Date
                                            if (filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.UPDATE_NEWEST) {
                                                ListViewModel.SortOrderDirection.UPDATE_NEWEST
                                            }
                                            else
                                            {
                                                ListViewModel.SortOrderDirection.UPDATE_OLDEST
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = filterState.value?.sortOrderDirection in setOf(
                            ListViewModel.SortOrderDirection.CREATE_NEWEST,
                            ListViewModel.SortOrderDirection.UPDATE_NEWEST
                        ),
                        onClick = {
                            listViewModel.setFilterState(
                                filterState.value?.copy(
                                    sortOrderDirection = if (filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_OLDEST) {
                                        ListViewModel.SortOrderDirection.CREATE_NEWEST
                                    } else {
                                        ListViewModel.SortOrderDirection.UPDATE_NEWEST
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
                                    sortOrderDirection = if (filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_OLDEST) {
                                        ListViewModel.SortOrderDirection.CREATE_NEWEST
                                    } else {
                                        ListViewModel.SortOrderDirection.UPDATE_NEWEST
                                    }
                                )
                            )
                        }
                    )
                    RadioButton(
                        selected = filterState.value?.sortOrderDirection in setOf(
                            ListViewModel.SortOrderDirection.CREATE_OLDEST,
                            ListViewModel.SortOrderDirection.UPDATE_OLDEST
                        ),
                        onClick = {
                            listViewModel.setFilterState(
                                filterState.value?.copy(
                                    sortOrderDirection = if (filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_OLDEST) {
                                        ListViewModel.SortOrderDirection.CREATE_OLDEST
                                    } else {
                                        ListViewModel.SortOrderDirection.UPDATE_OLDEST
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
                                    sortOrderDirection = if (filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_NEWEST || filterState.value?.sortOrderDirection == ListViewModel.SortOrderDirection.CREATE_OLDEST) {
                                        ListViewModel.SortOrderDirection.CREATE_OLDEST
                                    } else {
                                        ListViewModel.SortOrderDirection.UPDATE_OLDEST
                                    }
                                )
                            )
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApply(filterState.value ?: ListViewModel.FilterState())
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
