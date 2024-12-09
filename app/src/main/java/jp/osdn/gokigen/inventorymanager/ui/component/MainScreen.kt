package jp.osdn.gokigen.inventorymanager.ui.component

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel
import jp.osdn.gokigen.inventorymanager.ui.theme.GokigenComposeAppsTheme


@Composable
fun MainScreen(navController: NavHostController, cameraControl: ICameraControl, prefsModel : InventoryViewModel, name: String = "MainScreen", modifier: Modifier = Modifier)
{
/*
    // ----- 戻るボタンを押したときの処理 @ MainScreen
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    //val scope = rememberCoroutineScope()
    DisposableEffect(onBackPressedDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 戻るボタンを押したときの処理
                try
                {
                    Log.v("MainScreen", "handleOnBackPressed")
                    navController.navigate("MainScreen")  // MainScreen に遷移する
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
        }
        onBackPressedDispatcher?.addCallback(callback)
        onDispose {
            callback.remove() // 通常はいらないらしい
        }
    }
*/
    MaterialTheme {
/*
        TopAppBar(
            colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(text = stringResource(R.string.app_name) + " (" + stringResource(R.string.main_title_label_message) + ")")
            }
        )
*/
        Column(
            modifier = modifier.fillMaxSize().width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(vertical = 12.dp),
                text = stringResource(R.string.label_main_screen_message)
            )
            Button(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).fillMaxWidth(),
                onClick = { navController.navigate("ListScreen") }
            ) {
                Text(stringResource(R.string.button_label_list))
            }
            Button(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).fillMaxWidth(),
                onClick = {
                    navController.navigate("RegistScreen") {
                        Log.v("MainScreen", "Navigate to RegistScreen.")
                        cameraControl.initialize()
                        cameraControl.connectToCamera()
                        cameraControl.startCamera(isPreviewView = false)
                    }
                }
            ) {
                Text(stringResource(R.string.button_label_register))
            }
            Button(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp).fillMaxWidth(),
                onClick = { navController.navigate("PreferenceScreen") }
            ) {
                Text(stringResource(R.string.button_label_preference))
            }
        }
    }
}
