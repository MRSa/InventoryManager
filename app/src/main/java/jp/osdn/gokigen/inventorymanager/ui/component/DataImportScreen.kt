package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.import.DataImporter
import jp.osdn.gokigen.inventorymanager.ui.model.DataImportViewModel

@Composable
fun DataImportScreen(navController: NavHostController, viewModel: DataImportViewModel, dataImporter: DataImporter)
{
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            DataImportCommandPanel(navController, viewModel, dataImporter)

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
        Spacer(modifier = Modifier.weight(1f))
    }
}
