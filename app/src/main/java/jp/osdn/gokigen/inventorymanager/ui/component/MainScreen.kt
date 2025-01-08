package jp.osdn.gokigen.inventorymanager.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.inventorymanager.R

@Composable
fun MainScreen(navController: NavHostController, cameraControl: ICameraControl)
{
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
            modifier = Modifier.fillMaxSize().width(IntrinsicSize.Max),
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
                onClick = { navController.navigate("DataImportScreen") }
            ) {
                Text(stringResource(R.string.button_label_data_maintenance))
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
