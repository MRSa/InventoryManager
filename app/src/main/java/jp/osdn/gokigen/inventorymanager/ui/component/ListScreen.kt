package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jp.osdn.gokigen.inventorymanager.ui.theme.GokigenComposeAppsTheme


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier)
{
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview()
{
    GokigenComposeAppsTheme {
        Greeting("Android")
    }
}
