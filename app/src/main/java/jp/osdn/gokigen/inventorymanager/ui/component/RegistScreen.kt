package jp.osdn.gokigen.inventorymanager.ui.component

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.liveview.IAnotherDrawer
import jp.osdn.gokigen.gokigenassets.liveview.LiveImageView
import jp.osdn.gokigen.gokigenassets.liveview.LiveViewOnTouchListener
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.RegisterInformationViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("ClickableViewAccessibility", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegistScreen(navController: NavHostController, cameraControl: ICameraControl, viewModel: RegisterInformationViewModel, prefsModel : InventoryViewModel, onTouchListener: LiveViewOnTouchListener, anotherDrawer: IAnotherDrawer?, name: String = "RegistScreen", modifier: Modifier = Modifier)
{
    var liveView0 : LiveImageView? = null

    val connectionStatus = viewModel.cameraConnectionStatus.observeAsState(initial = viewModel.cameraConnectionStatus.value ?: ICameraConnectionStatus.CameraConnectionStatus.UNKNOWN)
    val imageData1 = viewModel.registerInformationImage1.observeAsState()
    val imageData2 = viewModel.registerInformationImage2.observeAsState()
    val imageData3 = viewModel.registerInformationImage3.observeAsState()

    val area1 = viewModel.registerInformationLabel01.observeAsState()
    val area2 = viewModel.registerInformationLabel02.observeAsState()
    val area3 = viewModel.registerInformationLabel03.observeAsState()
    val area4 = viewModel.registerInformationLabel04.observeAsState()
    val area5 = viewModel.registerInformationLabel05.observeAsState()
    val area6 = viewModel.registerInformationLabel06.observeAsState()

    // ----- 戻るボタンを押したときの処理で、カメラの制御を止めてしまう
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    //val scope = rememberCoroutineScope()
    DisposableEffect(onBackPressedDispatcher) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 戻るボタンを押したときの処理
                try
                {
                    cameraControl.finishCamera(false) // カメラ処理を終了
                    Log.v("RegistScreen", "handleOnBackPressed")
                    navController.popBackStack()  // ひとつ戻る
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
    MaterialTheme {
        Scaffold(
            //modifier = Modifier.systemBarsPadding(),
/*
            topBar = {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        ),
                    //modifier = Modifier.systemBarsPadding(),
                    title = {
                        Text(text = stringResource(R.string.screen_title_registry))
                    },
                )
            },
*/
            content = {
                Column(modifier = Modifier.fillMaxSize())
                {
                    // 画像エリア
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            if (imageData1.value != null) {
                                Image(
                                    imageData1.value!!.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth().weight(1.0f).padding(all = 4.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.baseline_image_24),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                                )
                            }
                            if (imageData2.value != null) {
                                Image(
                                    imageData2.value!!.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth().weight(1.0f).padding(all = 4.dp)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.baseline_image_24),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                                )
                            }
                            if (imageData3.value != null) {
                                Image(
                                    imageData3.value!!.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth().weight(1.0f).padding(all = 4.dp)
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
                                    if ((liveView.width > 0) && (liveView.height > 0)) {
                                        liveView.invalidate()
                                        //liveView.apply { }
                                    }
                                    liveView.apply { }
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
                    // TextField を縦に並べる部分
                    Column(
                        modifier = Modifier
                            .weight(1f) // 画面の高さの1/3を占める
                            .fillMaxWidth()
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(

                                text = stringResource(R.string.label_register_item),
                                modifier = Modifier.weight(1f)
                            )
                            TextField(
                                enabled = true,
                                value = area1.value ?: "",
                                singleLine = true,
                                onValueChange = viewModel::setTextArea1,
                                modifier = Modifier.weight(5.0f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(

                                text = stringResource(R.string.label_register_item),
                                modifier = Modifier.weight(1f)
                            )
                            TextField(
                                enabled = true,
                                value = area2.value ?: "",
                                singleLine = true,
                                onValueChange = viewModel::setTextArea2,
                                modifier = Modifier.weight(5.0f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(

                                text = stringResource(R.string.label_register_item),
                                modifier = Modifier.weight(1f)
                            )
                            TextField(
                                enabled = true,
                                value = area3.value ?: "",
                                singleLine = true,
                                onValueChange = viewModel::setTextArea3,
                                modifier = Modifier.weight(5.0f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(

                                text = stringResource(R.string.label_register_item),
                                modifier = Modifier.weight(1f)
                            )
                            TextField(
                                enabled = true,
                                value = area4.value ?: "",
                                singleLine = true,
                                onValueChange = viewModel::setTextArea4,
                                modifier = Modifier.weight(5.0f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.label_register_text),
                                modifier = Modifier.weight(1f).height(48.dp)
                            )
                            TextField(
                                enabled = false,
                                value = area5.value ?: "",
                                singleLine = true,
                                onValueChange = viewModel::setTextReaderArea,
                                modifier = Modifier.weight(5.0f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.label_register_bcr),
                                modifier = Modifier.weight(1f)
                            )
                            TextField(
                                enabled = false,
                                value = area6.value ?: "",
                                singleLine = true,
                                onValueChange = viewModel::setBarcodeReaderArea,
                                modifier = Modifier.weight(5.0f)
                            )
                        }
                    }
                    // 操作ボタンを配置する部分 (その１）
                    Row(
                        modifier = Modifier.fillMaxWidth().height(50.dp),
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
                            onClick = { cameraControl.getCameraShutter(1)?.doShutter(1) },
                            enabled = true
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_looks_one_24),
                                contentDescription = "Capture"
                            )
                        }
                        IconButton(
                            onClick = { cameraControl.getCameraShutter(2)?.doShutter(2) },
                            enabled = true
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_looks_two_24),
                                contentDescription = "Capture"
                            )
                        }
                        IconButton(
                            onClick = { cameraControl.getCameraShutter(3)?.doShutter(3) },
                            enabled = true
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_looks_3_24),
                                contentDescription = "Capture"
                            )
                        }
                        Spacer(modifier = Modifier.weight(1.0f))
                        IconButton(onClick = { cameraControl.getCameraShutter(4)?.doShutter(4) }, enabled = true) {
                            val iconId = R.drawable.baseline_text_fields_24
                            Icon(
                                painter = painterResource(id = iconId),
                                contentDescription = "ConnectionStatus"
                            )
                        }
                        Spacer(modifier = Modifier.weight(1.0f))
                        IconButton(
                            onClick = { cameraControl.getCameraShutter(5)?.doShutter(5) },
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
                        modifier = Modifier.fillMaxWidth().padding(all = 8.dp),
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
                    Spacer(modifier = Modifier.weight(0.08f))
                }
            },
        )
    }
}
