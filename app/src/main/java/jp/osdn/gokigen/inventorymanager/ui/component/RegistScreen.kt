package jp.osdn.gokigen.inventorymanager.ui.component

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.liveview.IAnotherDrawer
import jp.osdn.gokigen.gokigenassets.liveview.LiveImageView
import jp.osdn.gokigen.gokigenassets.liveview.LiveViewOnTouchListener
import jp.osdn.gokigen.inventorymanager.AppSingleton.Companion.vibrator
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegistScreen(navController: NavHostController, cameraControl: ICameraControl, prefsModel : InventoryViewModel, onTouchListener: LiveViewOnTouchListener, anotherDrawer: IAnotherDrawer?, name: String = "RegistScreen", modifier: Modifier = Modifier)
{
    var liveView0 : LiveImageView? = null
    var isGrid: Boolean by remember { mutableStateOf(false) }
    val connectionStatus = prefsModel.cameraConnectionStatus.observeAsState(initial = prefsModel.cameraConnectionStatus.value ?: ICameraConnectionStatus.CameraConnectionStatus.UNKNOWN)
    val context = LocalContext.current
    Column()
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        )
        {
            Row(modifier = Modifier.align(Alignment.TopStart)) {
                IconButton(
                    onClick = { cameraControl.getCameraShutter()?.doShutter() },
                    enabled = true
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_camera_24),
                        contentDescription = "Capture"
                    )
                }
                IconToggleButton(checked = isGrid, onCheckedChange = {
                    isGrid = it
                    Log.v("LiveViewScreen", "isGrid: $isGrid  $liveView0")
                    liveView0?.showGridFrame(isGrid, android.graphics.Color.WHITE)
                }) {
                    if (isGrid) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_grid_on_24),
                            contentDescription = "Grid On"
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_grid_off_24),
                            contentDescription = "Grid Off"
                        )
                    }
                }
            }
            Row(modifier = Modifier.align(Alignment.TopEnd)) {

                if (cameraControl.getZoomControl()?.canZoom() == true)
                {
                    IconButton(
                        onClick = { cameraControl.getZoomControl()?.driveZoomLens(true) },
                        enabled = true
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_zoom_in_24),
                            contentDescription = "ZoomIn"
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { cameraControl.getZoomControl()?.driveZoomLens(false) },
                        enabled = true,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_zoom_out_24),
                            contentDescription = "ZoomOut"
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                IconButton(onClick = { }, enabled = false) {
                    val iconId = when (connectionStatus.value) {
                        ICameraConnectionStatus.CameraConnectionStatus.DISCONNECTED -> { R.drawable.baseline_cloud_off_24 }
                        ICameraConnectionStatus.CameraConnectionStatus.UNKNOWN -> { R.drawable.baseline_cloud_off_24 }
                        ICameraConnectionStatus.CameraConnectionStatus.CONNECTING -> { R.drawable.baseline_cloud_queue_24 }
                        ICameraConnectionStatus.CameraConnectionStatus.CONNECTED -> { R.drawable.baseline_cloud_done_24 }
                    }
                    Icon(painter = painterResource(id = iconId), contentDescription = "ConnectionStatus")
                }
                IconButton(onClick = {
                    vibrator.vibrate(context, IVibrator.VibratePattern.SIMPLE_SHORT)
                    navController.navigate("PreferenceScreen")
                }, enabled = true) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_settings_24),
                        contentDescription = "Preferences"
                    )
                }
            }
        }
        AndroidView(
            factory = { context ->
                // Creates live-view screen
                Log.v("LiveViewScreen", "$isGrid")
                val liveView = LiveImageView(context).also {
                    it.clipToOutline = true  // https://issuetracker.google.com/issues/242463987
                }
                liveView0 = liveView
                cameraControl.setRefresher(0, liveView, liveView, liveView)
                liveView.setAnotherDrawer(null, anotherDrawer)
                liveView.injectDisplay(cameraControl)
                //liveView.setOnTouchListener(onTouchListener)
                //Log.v("LiveViewScreen", "-=-=-=-=-=-=- width:${liveView.width}, height:${liveView.height} isGrid:$isGrid -=-=-=-=-=-=-")

                liveView.invalidate()
                liveView.apply {  }
            },
            update = { view ->
                liveView0 = view
            },
            modifier = Modifier.pointerInteropFilter {
                if (liveView0 == null)
                {
                    Log.v("LiveView", "liveView0 is null...")
                    false
                }
                else
                {
                    Log.v("LiveView", "-=-=-=-=-=-=- width:${liveView0?.width}, height:${liveView0?.height} isGrid:$isGrid -=-=-=-=-=-=-")
                    onTouchListener.onTouch(liveView0, it)
                }
            }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { cameraControl.onLongClickReceiver(0).onLongClick(null) /* Called on Long Press */ },
                    )
                }
        )
    }
}
