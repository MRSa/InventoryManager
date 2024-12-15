package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel


@Composable
fun DetailScreen(navController: NavHostController, prefsModel : InventoryViewModel, id : Int)
{
    Text(
        text = "Hello Data No. $id!",
        fontSize = 28.sp,
        textAlign = TextAlign.Center,
    )
}
