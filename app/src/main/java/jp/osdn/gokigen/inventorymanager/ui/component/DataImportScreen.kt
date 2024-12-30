package jp.osdn.gokigen.inventorymanager.ui.component

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.import.DataImporter
import jp.osdn.gokigen.inventorymanager.import.GetPickFilePermission
import jp.osdn.gokigen.inventorymanager.ui.model.DataImportViewModel
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun DataImportScreen(navController: NavHostController, viewModel: DataImportViewModel, dataImporter: DataImporter)
{
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            DataImportCommandPanel(navController, viewModel, dataImporter)
            Spacer(modifier = Modifier.weight(1.0f))
            Spacer(modifier = Modifier.weight(1.0f))
            FilePickerForTargetFile(viewModel)
            Spacer(modifier = Modifier.weight(1.0f))
            UnderConstructionMessage()  // 仮に表示する
        }
    }
}

@Composable
fun DataImportCommandPanel(navController: NavHostController, viewModel: DataImportViewModel, dataImporter: DataImporter)
{
    val importing = viewModel.dataImporting.observeAsState()
    val buttonEnable = (importing.value != true)
    Row()
    {
        IconButton(
            enabled = buttonEnable,
            modifier = Modifier,
            onClick = { navController.popBackStack() })
        {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_24),
                contentDescription = "back to main screen")
        }
        //Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(id = R.string.button_label_data_import),
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterVertically)
                .clickable { navController.popBackStack() }
        )
    }
}


@Composable
fun FilePickerForTargetFile(viewModel: DataImportViewModel)
{
    val density = LocalDensity.current
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
            text = message,
            modifier = Modifier.clickable { filePickerLauncher.launch("Application/zip") },
            fontSize = with(density) { 18.dp.toSp() }
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
        HorizontalDivider(thickness = 1.dp)
        Button(
            enabled = (targetUri.value != null),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 16.dp),
            onClick = {  }
        ) {
            Text(stringResource(R.string.label_analyze_file))
        }
        // Spacer(modifier = Modifier.weight(1f))
    }
}
