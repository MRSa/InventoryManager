package jp.osdn.gokigen.inventorymanager.ui.component

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.import.DataImporter
import jp.osdn.gokigen.inventorymanager.import.GetPickFilePermission
import jp.osdn.gokigen.inventorymanager.ui.model.DataMaintenanceViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale.US

@Composable
fun DataImportScreen(navController: NavHostController, viewModel: DataMaintenanceViewModel, dataImporter: DataImporter)
{
    val scrollState = rememberScrollState()
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            HorizontalDivider(thickness = 1.dp)
            DataImportCommandPanel(navController, viewModel)
            HorizontalDivider(thickness = 1.dp)
            FilePickerForImportTargetFile(viewModel, dataImporter)
            HorizontalDivider(thickness = 1.dp)
            BatchRenameCategoryName(viewModel)
            HorizontalDivider(thickness = 1.dp)
            Spacer(modifier = Modifier.weight(1.0f))
            ShowImportReadyDialog(viewModel, dataImporter)
            ShowImportFinishDialog(viewModel)
        }
    }
}

@Composable
fun DataImportCommandPanel(navController: NavHostController, viewModel: DataMaintenanceViewModel)
{
    val importing = viewModel.dataImporting.observeAsState()
    val buttonEnable = (importing.value != true)
    Row()
    {
        IconButton(
            enabled = buttonEnable,
            modifier = Modifier,
            onClick = {
                if (navController.currentBackStackEntry?.destination?.route == "DataMaintenanceScreen")
                {
                    navController.popBackStack()
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_24),
                contentDescription = "back to main screen")
        }
        //Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(id = R.string.label_return_to_main_screen),
            fontSize = 22.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clickable {
                    if (navController.currentBackStackEntry?.destination?.route == "DataMaintenanceScreen")
                    {
                        navController.popBackStack()
                    }
                }
        )
    }
}


@Composable
fun FilePickerForImportTargetFile(viewModel: DataMaintenanceViewModel, dataImporter: DataImporter)
{
    val scope = rememberCoroutineScope()
    val filePickerLauncher = rememberLauncherForActivityResult(GetPickFilePermission()) { modelUri ->
        if (modelUri != null)
        {
            Log.v("File Pick", "Picked file  URI: $modelUri")
            scope.launch {
                viewModel.setTargetFileUri(modelUri)
            }
        }
    }

    val targetUri = viewModel.targetFileUri.observeAsState()
    val message = if (targetUri.value != null) {
        val path = (targetUri.value)?.path
        if (path != null)
        {
            "${stringResource(R.string.label_import_file_name)} ${File(path).name}"
        } else {
            // ----- ちょっと適当にファイル名っぽいところを表示...
            "${stringResource(R.string.label_import_file_name)} ${targetUri.value.toString().substring(targetUri.value.toString().lastIndexOf("2F") + 2, targetUri.value.toString().length)}"
        }
    } else {
        "${stringResource(R.string.label_import_file_name)} ${stringResource(R.string.label_no_import_file_name)}"
    }

    Column(
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
    ) {
        Text(
            text = stringResource(R.string.label_data_import),
            modifier = Modifier.padding(start = 0.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = message,
            modifier = Modifier
                .padding(start = 6.dp)
                .clickable { filePickerLauncher.launch("Application/zip") },
            fontSize = 16.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
                onClick = { filePickerLauncher.launch("Application/zip") }
            ) {
                Text(stringResource(R.string.label_select_file))
            }
        }
        Spacer(modifier = Modifier.padding(2.dp))
        Button(
            enabled = (targetUri.value != null),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
            onClick = {
                Thread { dataImporter.extractZipFileIntoLocal(targetUri.value, viewModel, viewModel) }.start()
            }
        ) {
            Text(stringResource(R.string.label_analyze_file))
        }
    }
}

@Composable
fun BatchRenameCategoryName(viewModel: DataMaintenanceViewModel)
{
    val scope = rememberCoroutineScope()
    val originalCategory = viewModel.renameOriginalCategory.observeAsState()
    val newCategory = viewModel.renameNewCategory.observeAsState()
    var isCategoryExpanded by remember { mutableStateOf(false) }
    val categoryList = viewModel.categoryList.observeAsState()

    var changeCategoryConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
    ) {
        Text(
            text = stringResource(R.string.label_data_batch_rename_category),
            modifier = Modifier.padding(start = 0.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.label_original_category),
                modifier = Modifier
                    .padding(start = 6.dp)
                    .clickable {
                        scope.launch { viewModel.initializeCategories() }
                        isCategoryExpanded = true
                    },
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.padding((4.dp)))
            Text(
                text = originalCategory.value ?: "",
                modifier = Modifier
                    .clickable {
                        scope.launch { viewModel.initializeCategories() }
                        isCategoryExpanded = true
                    }
                    .border(
                        width = Dp.Hairline,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(size = 1.dp)
                    )
                    .padding(6.dp)
            )
            DropdownMenu(
                expanded = isCategoryExpanded,
                onDismissRequest = { isCategoryExpanded = false },
            ) {
                categoryList.value?.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(text = category, fontSize = 14.sp) },
                        onClick = {
                            viewModel.setRenameOriginalCategory(category)
                            isCategoryExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.label_new_category),
                modifier = Modifier
                    .padding(start = 6.dp)
                    .clickable {
                        scope.launch { viewModel.initializeCategories() }
                        isCategoryExpanded = true
                    },
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.padding((4.dp)))
            TextField(
                enabled = true,
                value = newCategory.value ?: "",
                singleLine = true,
                onValueChange = { value -> viewModel.setRenameNewCategory(value) },
            )
        }
        Spacer(modifier = Modifier.padding(2.dp))
        Button(
            enabled = (originalCategory.value != newCategory.value),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
            onClick = { changeCategoryConfirm = true }
        ) {
            Text(stringResource(R.string.label_change_category))
        }
        Spacer(modifier = Modifier.padding(2.dp))
    }

    if (changeCategoryConfirm) {
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { changeCategoryConfirm = false },
            title = { Text(text = stringResource(R.string.dialog_title_start_rename_category)) },
            text = { Text(text = "${stringResource(R.string.dialog_message_start_rename_category_1)} ${originalCategory.value ?: ""} -> ${newCategory.value ?: ""} ${stringResource(R.string.dialog_message_start_rename_category_2)}") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.executeChangeCategory(context,originalCategory.value ?: "", newCategory.value ?: "")
                        changeCategoryConfirm = false
                    }
                ) {
                    Text(text = stringResource(R.string.dialog_button_ok))
                }
            },
            dismissButton = {
                Button(onClick = { changeCategoryConfirm = false }) {
                    Text(text = stringResource(R.string.dialog_button_cancel))
                }
            }
        )
    }
}

@Composable
fun ShowImportReadyDialog(viewModel: DataMaintenanceViewModel, dataImporter: DataImporter)
{
    val isReadyToImport = viewModel.readyToImport.observeAsState()
    val dataCount = viewModel.importDataCount.observeAsState()
    if (isReadyToImport.value == true)
    {
        AlertDialog(
            onDismissRequest = { Thread { dataImporter.postProcessImport(viewModel) }.start() },
            title = { Text(text = stringResource(R.string.dialog_title_start_import)) },
            text = { Text(text = "${stringResource(R.string.dialog_message_start_import_1)} ${dataCount.value} ${stringResource(R.string.dialog_message_start_import_2)}") },
            confirmButton = {
                Button(onClick = { Thread { dataImporter.doImport(viewModel, viewModel) }.start() }) {
                    Text(text = stringResource(R.string.dialog_button_ok))
                }
            },
            dismissButton = {
                Button(onClick = { Thread { dataImporter.postProcessImport(viewModel) }.start() }) {
                    Text(text = stringResource(R.string.dialog_button_cancel))
                }
            }
        )
    }

    val isImporting = viewModel.dataImporting.observeAsState()
    val currentImportCount = viewModel.currentImportCount.observeAsState()
    val currentImportProcess = viewModel.currentExecutingProcess.observeAsState()
    val totalCount = dataCount.value ?: 0
    val currentCount = currentImportCount.value ?: 0
    if (isImporting.value == true)
    {
        val statusMessage = when (currentImportProcess.value) {
            DataImporter.ImportProcess.IDLE -> { stringResource(R.string.dialog_idle_proceed) }
            DataImporter.ImportProcess.PREPARE -> { stringResource(R.string.dialog_prepare_proceed) }
            DataImporter.ImportProcess.IMPORT -> { stringResource(R.string.dialog_import_proceed) }
            DataImporter.ImportProcess.POSTPROCESS -> { stringResource(R.string.dialog_postprocess_proceed) }
            else -> { "" }
        }

        // ---- 実行中ダイアログの表示
        val message = if ((currentCount == 0)||(totalCount == 0)) {
            // ----- 処理カウントがゼロの場合
            "$statusMessage ${stringResource(R.string.dialog_progress_proceed)}"
        } else {
            // ----- カウントがわかる場合
            val progressPercent = (currentCount.toFloat() / totalCount.toFloat()) * 100.0f
            "$statusMessage ${stringResource(R.string.dialog_progress_proceed)} $currentCount / $totalCount (${String.format(US, "%.1f", progressPercent)} %)"
        }
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
}

@Composable
fun ShowImportFinishDialog(viewModel: DataMaintenanceViewModel)
{
    val importProcess = viewModel.currentExecutingProcess.observeAsState()
    if (importProcess.value == DataImporter.ImportProcess.FINISH_SUCCESS)
    {
        AlertDialog(
            onDismissRequest = {  },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Success"
                )
            },
            title = { Text(text = stringResource(R.string.dialog_title_finish_import)) },
            text = { Text(text = stringResource(R.string.dialog_message_finish_import)) },
            confirmButton = {
                Button(onClick = { viewModel.dismissImportProcess() }) {
                    Text(text = stringResource(R.string.dialog_button_ok))
                }
            }
        )
    } else if (importProcess.value == DataImporter.ImportProcess.FINISH_FAILURE)
    {
        AlertDialog(
            onDismissRequest = {  },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Warning"
                )
            },
            title = { Text(text = stringResource(R.string.dialog_title_finish_failure_import)) },
            text = { Text(text = stringResource(R.string.dialog_message_finish_failure_import)) },
            confirmButton = {
                Button(onClick = { viewModel.dismissImportProcess() }) {
                    Text(text = stringResource(R.string.dialog_button_dismiss))
                }
            }
        )
    }
}