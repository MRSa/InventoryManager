package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel
import jp.osdn.gokigen.inventorymanager.ui.theme.GokigenComposeAppsTheme

@Composable
fun PreferenceScreen(navController: NavHostController, prefsModel: InventoryViewModel)
{
    val padding = 2.dp

    GokigenComposeAppsTheme {
        Column {
            //PreferenceScreenTitle()
            Spacer(Modifier.size(padding))
            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
/*
            ShowWifiSetting()
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            CaptureBothLiveViewAndCamera(prefsModel)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
            FilePickerForObjectDetectionModel(prefsModel)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
            FilePickerForObjectDetectionModel2(prefsModel)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(Modifier.size(padding))
            CameraConnectionMethodDropdown(prefsModel, vibrator)
            Spacer(Modifier.size(padding))
            Divider(color = Color.LightGray, thickness = 1.dp)
*/
            Spacer(Modifier.size(padding))
            //ShowAboutGokigen()
            Spacer(Modifier.size(padding))
            //ShowGokigenPrivacyPolicy()
            Spacer(Modifier.size(padding))
        }
    }
}

/*
@Composable
fun PreferenceScreenTitle()
{
    val density = LocalDensity.current
    TopAppBar()
    {
        Text(text = stringResource(id = R.string.pref_cat_application_settings),
            fontSize = with(density) { 24.dp.toSp() },
            modifier = Modifier.padding(all = 6.dp))
    }
}


@Composable
fun ShowAboutGokigen()
{
    val density = LocalDensity.current
    val uriHandler = LocalUriHandler.current
    val openUri = stringResource(R.string.pref_instruction_manual_url)
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = stringResource(R.string.pref_instruction_manual),
                color = MaterialTheme.colors.primaryVariant,
                fontSize = with(density) { 18.dp.toSp() }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = openUri,
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.clickable(onClick = { uriHandler.openUri(openUri) }),
                fontSize = with(density) { 14.dp.toSp() }
            )
        }
    }
}

@Composable
fun ShowGokigenPrivacyPolicy()
{
    val density = LocalDensity.current
    val uriHandler = LocalUriHandler.current
    val openUri = stringResource(R.string.pref_privacy_policy_url)
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = stringResource(R.string.pref_privacy_policy),
                color = MaterialTheme.colors.primaryVariant,
                fontSize = with(density) { 18.dp.toSp() }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = openUri,
                color = MaterialTheme.colors.primaryVariant,
                modifier = Modifier.clickable(onClick = { uriHandler.openUri(openUri) }),
                fontSize = with(density) { 14.dp.toSp() }
            )
        }
    }
}

*/