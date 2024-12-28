package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.export.DataExporter
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeFromIsbn
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel

@Composable
fun ListScreen(navController: NavHostController, viewModel : InventoryViewModel, exporter: DataExporter, recognizer: RecognizeFromIsbn)
{
    // 画面遷移時にデータを取得
    rememberNavController()
    LaunchedEffect(key1 = Unit) {
        viewModel.refresh()
    }

    val padding = 6.dp
    MaterialTheme {
/*
        Scaffold(
            topBar = { MainTopBar(navController) },
            modifier = Modifier
                .fillMaxSize(),
                // .padding(),
                //.background(MaterialTheme.colorScheme.background),
            //contentWindowInsets = WindowInsets.Companion.systemBars,
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CommandPanel(navController, viewModel, exporter, recognizer)
                    HorizontalDivider(thickness = 1.dp)
                    Spacer(Modifier.size(padding))
                    ReceivedContentList(navController, viewModel)
                }
            }
        )
*/
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CommandPanel(navController, viewModel, exporter, recognizer)
            HorizontalDivider(thickness = 1.dp)
            Spacer(Modifier.size(padding))
            ReceivedContentList(navController, viewModel)
        }
    }
}
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(navController: NavHostController)
{
    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MRSa/InventoryManager/blob/master/docs/Readme.md")) }
    TopAppBar(
        title = {
            Text(stringResource(id = R.string.app_name))
        },
        //modifier = Modifier.padding(top = WindowInsets.systemBars.top),
        actions = {
            IconButton(
                enabled = false,
                onClick = { navController.navigate("PreferenceScreen") }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
            IconButton(
                onClick = { context.startActivity(intent) /* Open the Web Page */ })
            {
                Icon(Icons.Filled.Info, contentDescription = "Information")
            }
        }
    )
}
*/

@Composable
fun CommandPanel(navController: NavHostController, dataListModel : InventoryViewModel, exporter: DataExporter, recognizer: RecognizeFromIsbn)
{
    val exporting = dataListModel.dataExporting.observeAsState()
    Row()
    {
        IconButton(
            enabled = true,
            modifier = Modifier,
            onClick = { navController.popBackStack() })
        {
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_back_24),
                contentDescription = "back to main screen")
        }
        // Spacer(modifier = Modifier.weight(1f))

/*
        IconButton(
            enabled = false,
            modifier = Modifier,
            onClick = { })
        {
            Icon(
                painter = painterResource(R.drawable.baseline_filter_alt_24),
                contentDescription = "filter")
        }
*/
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier,
            onClick = { dataListModel.refresh() })
        {
            Icon(Icons.Filled.Refresh, contentDescription = "Information")
        }
        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            modifier = Modifier,
            onClick = {
                recognizer.doRecognizeAllFromIsbn()
            })
        {
            Icon(
                painter = painterResource(R.drawable.baseline_menu_book_24),
                contentDescription = "Update from ISBN")
        }
        Spacer(modifier = Modifier.weight(1f))

        /*
                IconButton(
                    enabled = false,
                    modifier = Modifier,
                    onClick = { })
                {
                    Icon(
                        painter = painterResource(R.drawable.baseline_import_export_24),
                        contentDescription = "import/export")
                }
        */
        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            modifier = Modifier,
            onClick = {
                exporter.doExport()
            })
        {
            Icon(
                painter = painterResource(R.drawable.baseline_save_alt_24),
                contentDescription = "export")
        }
    }

    if (exporting.value == true)
    {
        // ----- ビジーダイアログを表示する
        ShowExportingDialog(dataListModel)
    }
}

@Composable
fun ShowExportingDialog(dataListModel: InventoryViewModel)
{
    val fileName = dataListModel.exportingFileName.observeAsState()
    AlertDialog(
        onDismissRequest = {  },
        title = { Text("${stringResource(R.string.label_data_exporting)} ${fileName.value}") },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        },
        confirmButton = {  },
        dismissButton = null
    )
}

@Composable
fun ReceivedContentList(navController: NavHostController, dataListModel: InventoryViewModel)
{
    val information = dataListModel.refreshingData.observeAsState()
    val listState = rememberLazyListState()
    if (information.value == true)
    {
        Row()
        {
            Icon(Icons.Default.Warning, "Warning", tint = MaterialTheme.colorScheme.onTertiary)
            Text(
                fontSize = 14.sp,
                text = stringResource(R.string.data_updating),
                color = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)
            )
        }
    }
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
