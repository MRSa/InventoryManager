package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel
import jp.osdn.gokigen.inventorymanager.ui.theme.GokigenComposeAppsTheme


@Composable
fun MainScreen(navController: NavHostController, prefsModel : InventoryViewModel, name: String = "MainScreen", modifier: Modifier = Modifier)
{
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
