package jp.osdn.gokigen.inventorymanager.ui.component

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.ui.model.PreferenceViewModel
import kotlinx.coroutines.launch

@Composable
fun PreferenceScreen(navController: NavHostController, prefsModel: PreferenceViewModel)
{
    val padding = 6.dp

    MaterialTheme {
        val scrollState = rememberScrollState()
        Column(
            //modifier = Modifier.systemBarsPadding().fillMaxSize().verticalScroll(scrollState)
            modifier = Modifier
                // .padding(top = 48.dp)  // ベタすぎる...
                //.systemBarsPadding()    // 1回目の描画ではこの指定が効いていないみたい...
                .fillMaxSize()
                .verticalScroll(scrollState)
        )
        {
            //Spacer(Modifier.size(padding))
            HorizontalDivider(thickness = 1.dp)
            ReturnToMainScreen(navController)
            Spacer(Modifier.size(padding))
            HorizontalDivider(thickness = 1.dp)
            SwitchCheckInformationImmediately(prefsModel)
            Spacer(Modifier.size(padding))
            HorizontalDivider(thickness = 1.dp)
            SwitchOverwriteInformationFromIsbn(prefsModel)
            Spacer(Modifier.size(padding))
            HorizontalDivider(thickness = 1.dp)
            SwitchArchiveOnlyOneFile(prefsModel)
            Spacer(Modifier.size(padding))
            HorizontalDivider(thickness = 1.dp)
            ShowAboutGokigen()
            Spacer(Modifier.size(padding))
            HorizontalDivider(thickness = 1.dp)
            ShowGokigenPrivacyPolicy()
            Spacer(Modifier.size(padding))
            HorizontalDivider(thickness = 1.dp)
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
*/

@Composable
fun ReturnToMainScreen(navController: NavHostController)
{
    val density = LocalDensity.current
    Spacer(Modifier.size(12.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
            contentDescription = "Back",
            modifier = Modifier.clickable( onClick = {
                Log.v("BACK", "CURRENT SCREEN: ${navController.currentBackStackEntry?.destination?.route}")
                if (navController.currentBackStackEntry?.destination?.route == "PreferenceScreen") {
                    navController.popBackStack()
                }
            })
        )
        Text(text = stringResource(R.string.label_return_to_main_screen),
            fontSize = with(density) { 18.dp.toSp() },
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable( onClick = {
                Log.v("BACK", "CURRENT SCREEN: ${navController.currentBackStackEntry?.destination?.route}")
                if (navController.currentBackStackEntry?.destination?.route == "PreferenceScreen")
                {
                    navController.popBackStack()
                }
            })
        )
    }
}

@Composable
fun SwitchCheckInformationImmediately(prefsModel: PreferenceViewModel)
{
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val checkImmediately = prefsModel.checkIsbnImmediately.observeAsState()
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Switch(
            checked = checkImmediately.value?: false,
            onCheckedChange = {
                scope.launch {
                    prefsModel.setCheckIsbnImmediately(!(checkImmediately.value?: false))
                }
            })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.label_switch_data_check_immediately),
            fontSize = with(density) { 18.dp.toSp() },
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable( onClick = {
                scope.launch { prefsModel.setCheckIsbnImmediately(!(checkImmediately.value?: false)) }
            })
        )
    }
    Text(text = stringResource(R.string.description_switch_data_check_immediately),
        color = MaterialTheme.colorScheme.secondary,
        fontSize = with(density) { 14.dp.toSp() },)
}

@Composable
fun SwitchOverwriteInformationFromIsbn(prefsModel: PreferenceViewModel)
{
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val overwriteInfo = prefsModel.overwriteInfoFromIsbn.observeAsState()
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Switch(
            checked = overwriteInfo.value?: false,
            onCheckedChange = {
                scope.launch {
                    prefsModel.setOverwriteInformation(!(overwriteInfo.value?: false))
                }
            })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.label_switch_overwrite_information_from_isbn),
            fontSize = with(density) { 18.dp.toSp() },
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable( onClick = {
                scope.launch { prefsModel.setOverwriteInformation(!(overwriteInfo.value?: false)) }
            })
        )
    }
    Text(text = stringResource(R.string.description_switch_overwrite_information_from_isbn),
        color = MaterialTheme.colorScheme.secondary,
        fontSize = with(density) { 14.dp.toSp() },)
}

@Composable
fun SwitchArchiveOnlyOneFile(prefsModel: PreferenceViewModel)
{
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val archiveFileInfo = prefsModel.archiveOneFile.observeAsState()
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Switch(
            checked = archiveFileInfo.value?: false,
            onCheckedChange = {
                scope.launch {
                    prefsModel.setArchiveOneFile(!(archiveFileInfo.value?: false))
                }
            })
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.label_switch_exports_only_one_file),
            fontSize = with(density) { 18.dp.toSp() },
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable( onClick = {
                scope.launch { prefsModel.setArchiveOneFile(!(archiveFileInfo.value?: false)) }
            })
        )
    }
    Text(text = stringResource(R.string.description_switch_exports_only_one_file),
        color = MaterialTheme.colorScheme.secondary,
        fontSize = with(density) { 14.dp.toSp() },)
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
                color = MaterialTheme.colorScheme.primary,
                fontSize = with(density) { 18.dp.toSp() }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = openUri,
                color = MaterialTheme.colorScheme.secondary,
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
                color = MaterialTheme.colorScheme.primary,
                fontSize = with(density) { 18.dp.toSp() }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = openUri,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.clickable(onClick = { uriHandler.openUri(openUri) }),
                fontSize = with(density) { 14.dp.toSp() }
            )
        }
    }
}
