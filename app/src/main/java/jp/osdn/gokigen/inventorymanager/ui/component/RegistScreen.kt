package jp.osdn.gokigen.inventorymanager.ui.component

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import jp.osdn.gokigen.inventorymanager.ui.model.RegisterInformationViewModel
import jp.osdn.gokigen.inventorymanager.ui.theme.GokigenComposeAppsTheme

@SuppressLint("ClickableViewAccessibility")
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegistScreen(navController: NavHostController, cameraControl: ICameraControl, viewModel: RegisterInformationViewModel, prefsModel : InventoryViewModel, onTouchListener: LiveViewOnTouchListener, anotherDrawer: IAnotherDrawer?, name: String = "RegistScreen", modifier: Modifier = Modifier)
{
    val context = LocalContext.current
    var liveView0 : LiveImageView? = null
    val connectionStatus = viewModel.cameraConnectionStatus.observeAsState(initial = viewModel.cameraConnectionStatus.value ?: ICameraConnectionStatus.CameraConnectionStatus.UNKNOWN)

    // 戻るボタンを押したときの処理で、カメラの制御を止めてしまう
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val scope = rememberCoroutineScope()
    DisposableEffect(onBackPressedDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 戻るボタンを押したときの処理
                try
                {
                    cameraControl.finishCamera(false) // カメラ処理を終了
                    Log.v("RegistScreen", "handleOnBackPressed")
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

    val informationMessage = stringResource(R.string.label_explain_register_next)
    GokigenComposeAppsTheme {
        TopAppBar(
            colors = topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(text = stringResource(R.string.app_name) + " (" + stringResource(R.string.main_title_label_message) + ")")
            }
        )
        Column {
            // タイトルバーの領域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.screen_title_registry),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 画像エリア
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (viewModel.registerInformationImage1.value != null) {
                        Image(
                            viewModel.registerInformationImage1.value!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().weight(1.0f)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_image_24),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().weight(1.0f)
                        )
                    }
                    if (viewModel.registerInformationImage2.value != null) {
                        Image(
                            viewModel.registerInformationImage2.value!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().weight(1.0f)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_image_24),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().weight(1.0f)
                        )
                    }
                    if (viewModel.registerInformationImage3.value != null) {
                        Image(
                            viewModel.registerInformationImage3.value!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().weight(1.0f)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_image_24),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth().weight(1.0f)
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    AndroidView(
                        factory = { context ->
                            // Creates live-view screen
                            val liveView = LiveImageView(context).also {
                                it.clipToOutline =
                                    true  // https://issuetracker.google.com/issues/242463987
                            }
                            liveView0 = liveView
                            cameraControl.setRefresher(0, liveView, liveView, liveView)
                            liveView.setAnotherDrawer(null, anotherDrawer)
                            liveView.injectDisplay(cameraControl)
                            liveView.setOnTouchListener(onTouchListener)
                            Log.v(
                                "LiveViewScreen",
                                "-=-=-=-=-=-=- width:${liveView.width}, height:${liveView.height} -=-=-=-=-=-=-"
                            )
                            //liveView.invalidate()
                            if ((liveView.width > 0) && (liveView.height > 0))
                            {
                                liveView.invalidate()
                                //liveView.apply { }
                            }
                            liveView.apply { }
/**/
                        },
                        update = { view ->
                            liveView0 = view
                        },
                        modifier = Modifier
                            .pointerInteropFilter {
                                if (liveView0 == null) {
                                    Log.v("LiveView", "liveView0 is null...")
                                    false
                                } else {
                                    Log.v(
                                        "LiveView",
                                        "-=-=-=-=-=-=- width:${liveView0?.width}, height:${liveView0?.height} -=-=-=-=-=-=-"
                                    )
                                    onTouchListener.onTouch(liveView0, it)
                                }
                            }
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        cameraControl.onLongClickReceiver(0)
                                            .onLongClick(null) /* Called on Long Press */
                                    },
                                )
                            }
                    )
                }
            }
            // TextField を縦に5つ並べる部分
            Column(
                modifier = Modifier
                    .weight(1f) // 画面の高さの1/3を占める
                    .fillMaxWidth()
            ) {
                ShowRegisterTextFieldArea(stringResource(R.string.label_register_item), "Item 1")
                ShowRegisterTextFieldArea(stringResource(R.string.label_register_item), "Item 2")
                ShowRegisterTextFieldArea(stringResource(R.string.label_register_item), "Item 3")
                ShowRegisterTextFieldArea(stringResource(R.string.label_register_item), "Item 4")
                ShowRegisterTextFieldArea(
                    stringResource(R.string.label_register_text),
                    stringResource(R.string.label_register_text)
                )
                ShowRegisterTextFieldArea(
                    stringResource(R.string.label_register_bcr),
                    stringResource(R.string.label_register_bcr)
                )
            }
            // 操作ボタンを配置する部分 (その１）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            )
            {
                IconButton(onClick = { }, enabled = false) {
                    val iconId = R.drawable.baseline_cloud_done_24
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = "ConnectionStatus"
                    )
                }


                Spacer(modifier = Modifier.weight(2.0f))

                IconButton(
                    onClick = { },
                    enabled = true
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_looks_one_24),
                        contentDescription = "Capture"
                    )
                }
                IconButton(
                    onClick = { },
                    enabled = true
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_looks_two_24),
                        contentDescription = "Capture"
                    )
                }
                IconButton(
                    onClick = { },
                    enabled = true
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_looks_3_24),
                        contentDescription = "Capture"
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
                IconButton(onClick = { }, enabled = true) {
                    val iconId = R.drawable.baseline_text_fields_24
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = "ConnectionStatus"
                    )
                }
                Spacer(modifier = Modifier.weight(1.0f))
                IconButton(
                    onClick = { },
                    enabled = true
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_barcode_reader_24),
                        contentDescription = "Capture"
                    )
                }
                Spacer(modifier = Modifier.weight(3.0f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = informationMessage
                )
                Spacer(modifier = Modifier.weight(4.0f))
                Button(
                    onClick = { /* ボタンクリック時の処理 */ },
                    modifier = Modifier.align(Alignment.Bottom)
                ) {
                    Text(stringResource(R.string.button_label_register_next))
                }
            }
        }


        /*
    var isGrid: Boolean by remember { mutableStateOf(false) }
    val connectionStatus = prefsModel.cameraConnectionStatus.observeAsState(initial = prefsModel.cameraConnectionStatus.value ?: ICameraConnectionStatus.CameraConnectionStatus.UNKNOWN)

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
*/
    }
}


@Composable
fun RegisterScreenTextFieldArea(title : String, label: String)
{
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(

            text = " $title ",
            modifier = Modifier.weight(1f)
        )
        TextField(
            value = "($label)",
            onValueChange = { },
            modifier = Modifier.weight(5.0f)
        )
    }
}
