package jp.osdn.gokigen.inventorymanager.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel

@Composable
fun ListScreen(navController: NavHostController, viewModel : InventoryViewModel)
{
    val padding = 6.dp

    //viewModel.refresh()
    MaterialTheme {
        Scaffold(
            topBar = { MainTopBar(navController) },
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                //.background(MaterialTheme.colorScheme.background),
        ) {
            Modifier.padding(it).fillMaxWidth()
            CommandPanel(viewModel)
            Spacer(Modifier.size(padding))
            ReceivedContentList(navController, viewModel)
        }
    }
}

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

@Composable
fun CommandPanel(viewModel : InventoryViewModel)
{
    Row()
    {
        IconButton(
            modifier = Modifier,
            onClick = { })
        {
            Icon(
                painter = painterResource(R.drawable.baseline_filter_alt_24),
                contentDescription = "filter")
        }

        IconButton(
            modifier = Modifier,
            onClick = { viewModel.refresh() })
        {
            Icon(Icons.Filled.Refresh, contentDescription = "Information")
        }

        IconButton(
            modifier = Modifier,
            onClick = { })
        {
            Icon(
                painter = painterResource(R.drawable.baseline_import_export_24),
                contentDescription = "import/export")
        }
    }
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
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        state = listState
    ) {
        this.items(dataListModel.dataList) { data ->
            key(data.id) {
                DataItem(navController, data)
            }
            HorizontalDivider(thickness = 1.dp)
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
}
