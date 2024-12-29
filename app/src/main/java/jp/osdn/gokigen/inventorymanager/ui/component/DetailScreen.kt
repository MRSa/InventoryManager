package jp.osdn.gokigen.inventorymanager.ui.component

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.export.InOutExportImage
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeFromIsbn
import jp.osdn.gokigen.inventorymanager.ui.model.DetailInventoryViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.TextFieldId
import java.util.Date

@Composable
fun DetailScreen(navController: NavHostController, viewModel : DetailInventoryViewModel, id : Long, recognizer: RecognizeFromIsbn)
{
    viewModel.initializeData(id)
    val detail = viewModel.detailData.observeAsState()
    val padding = 6.dp

    MaterialTheme {
        if (detail.value == null)
        {
            // ----- 「データがない」と表示する
            Text(
                text = "{stringResource(id = R.string.no_data)} (id:$id)",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
        }
        else
        {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
            ) {
                ReturnToMainScreen(navController)
                Spacer(Modifier.size(padding))
                // HorizontalDivider(thickness = 1.dp)

                val imageFile1 = detail.value?.imageFile1 ?: ""
                val imageFile2 = detail.value?.imageFile2 ?: ""
                val imageFile3 = detail.value?.imageFile3 ?: ""
                if ((imageFile1.isNotEmpty()) || (imageFile2.isNotEmpty()) || (imageFile3.isNotEmpty())) {
                    // 画像の表示
                    ShowCapturedImage(id, imageFile1, imageFile2, imageFile3)
                }
                Spacer(Modifier.size(padding))
                ShowTextInputData(
                    id,
                    TextFieldId.TITLE,
                    stringResource(R.string.label_title),
                    detail.value?.title ?: "",
                    true,
                    viewModel)
                Spacer(Modifier.size(padding))
                ShowTextInputData(
                    id,
                    TextFieldId.SUBTITLE,
                    stringResource(R.string.label_subtitle),
                    detail.value?.subTitle ?: "",
                    true,
                    viewModel)
                Spacer(Modifier.size(padding))
                ShowTextInputData(
                    id,
                    TextFieldId.AUTHOR,
                    stringResource(R.string.label_author),
                    detail.value?.author ?: "",
                    true,
                    viewModel)
                Spacer(Modifier.size(padding))
                ShowTextInputData(
                    id,
                    TextFieldId.PUBLISHER,
                    stringResource(R.string.label_publisher),
                    detail.value?.publisher ?: "",
                    true,
                    viewModel)
                Spacer(Modifier.size(padding))
                ShowTextInputData(
                    id,
                    TextFieldId.ISBN,
                    stringResource(R.string.label_isbn),
                    detail.value?.isbn ?: "",
                    true,
                    viewModel)
                Spacer(Modifier.size(padding))
                ShowTextInputData(
                    id,
                    TextFieldId.CATEGORY,
                    stringResource(R.string.label_category),
                    detail.value?.category ?: "",
                    true,
                    viewModel)
                Spacer(Modifier.size(padding))
                if ((detail.value?.note ?: "").isNotEmpty()) {
                    HorizontalDivider(thickness = 1.dp)
                    Spacer(Modifier.size(padding))
                    ShowTextInputData(
                        id,
                        TextFieldId.TEXT,
                        stringResource(R.string.label_text_recognition),
                        detail.value?.note ?: "",
                        false,
                        viewModel
                    )
                    Spacer(Modifier.size(padding))
                }
                Spacer(Modifier.size(padding))
                Spacer(Modifier.size(padding))

                val context = LocalContext.current
                Row(
                    modifier = Modifier.fillMaxWidth().padding(all = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val canQuery = viewModel.isEnableQuery.observeAsState()
                    IconButton(
                        enabled = canQuery.value?: true,
                        modifier = Modifier,
                        onClick = {
                            // 応答あるまでボタンは無効化
                            viewModel.updateButtonEnable(
                                isEnableUpdate = false,
                                isEnableQuery = false
                            )
                            AppSingleton.vibrator.vibrate(context, IVibrator.VibratePattern.SIMPLE_SHORT)
                            recognizer.doRecognizeFromIsbn(id, viewModel)
                        })
                    {
                        Icon(
                            painter = painterResource(R.drawable.baseline_menu_book_24),
                            contentDescription = "Update from ISBN")
                    }
                    Spacer(modifier = Modifier.weight(4.0f))
                    Text(
                        text = ""
                    )
                    Spacer(modifier = Modifier.weight(4.0f))

                    val isUpdate = viewModel.dataIsUpdate.observeAsState()
                    val dataUpdatedMessage = stringResource(R.string.label_data_updated)
                    Button(
                        enabled = isUpdate.value ?: false,
                        onClick = {
                            // --- 「更新」ボタンを押すと、表示している内容でデータベースの中身を更新する
                            val title = detail.value?.title ?: ""
                            val subTitle = detail.value?.subTitle ?: ""
                            val author = detail.value?.author ?: ""
                            val publisher = detail.value?.publisher ?: ""
                            val isbn = detail.value?.isbn ?: ""
                            val category = detail.value?.category ?: ""
                            val currentDate = Date()
                            viewModel.updateButtonEnable(
                                isEnableUpdate = false,
                                isEnableQuery = true
                            )
                            Thread {
                                try
                                {
                                    AppSingleton.db.storageDao().updateContentWithIsbn(
                                        id = id,
                                        title = title,
                                        subTitle = subTitle,
                                        author = author,
                                        publisher = publisher,
                                        isbn = isbn,
                                        category = category,
                                        updateDate = currentDate
                                    )
                                }
                                catch (e: Exception)
                                {
                                    e.printStackTrace()
                                }
                            }.start()
                            AppSingleton.vibrator.vibrate(context, IVibrator.VibratePattern.SIMPLE_MIDDLE)
                            Toast.makeText(context, "$dataUpdatedMessage : $title", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.align(Alignment.Bottom)
                    ) {
                        Text(stringResource(R.string.button_label_update))
                    }
                }
            }
        }
    }
}

@Composable
fun ShowTextInputData(id: Long, itemId: TextFieldId, label: String, value: String, isSingleLine: Boolean, viewModel : DetailInventoryViewModel)
{
    val isSubtitleEditing = viewModel.isSubtitleEditing.observeAsState()
    val isIsbnEditing = viewModel.isIsbnEditing.observeAsState()
    val isCategoryEditing = viewModel.isCategoryEditing.observeAsState()
    Row(
        modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp
        )
        TextField(
            enabled = when (itemId) {
                TextFieldId.TITLE -> { false }
                TextFieldId.SUBTITLE -> { isSubtitleEditing.value ?: false }
                TextFieldId.AUTHOR -> { false }
                TextFieldId.PUBLISHER -> {false }
                TextFieldId.ISBN -> { isIsbnEditing.value ?: false }
                TextFieldId.CATEGORY -> { isCategoryEditing.value ?: false }
                TextFieldId.TEXT -> { false }
            },
            value = value,
            singleLine = isSingleLine,
            onValueChange = {
                when (itemId) {
                    TextFieldId.TITLE -> {  }
                    TextFieldId.SUBTITLE -> { viewModel.updateValueSingle(id, TextFieldId.SUBTITLE, it) }
                    TextFieldId.AUTHOR -> {  }
                    TextFieldId.PUBLISHER -> { }
                    TextFieldId.ISBN -> { viewModel.updateValueSingle(id, TextFieldId.ISBN, it) }
                    TextFieldId.CATEGORY -> { viewModel.updateValueSingle(id, TextFieldId.CATEGORY, it) }
                    TextFieldId.TEXT -> {  }
                }
            },
            modifier = Modifier.weight(5.0f),
            textStyle = TextStyle(fontSize = 16.sp),
            keyboardOptions = when (itemId) {
                TextFieldId.SUBTITLE -> { KeyboardOptions(keyboardType = KeyboardType.Text) }
                TextFieldId.ISBN -> { KeyboardOptions(keyboardType = KeyboardType.Number) }
                TextFieldId.CATEGORY -> { KeyboardOptions(keyboardType = KeyboardType.Text) }
                else -> { KeyboardOptions() }
            },
            visualTransformation = VisualTransformation.None
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Button(
            contentPadding = PaddingValues(8.dp),
            enabled = when (itemId) {
                TextFieldId.TITLE -> { false }
                TextFieldId.SUBTITLE -> { true }
                TextFieldId.AUTHOR -> { false }
                TextFieldId.PUBLISHER -> { false }
                TextFieldId.ISBN -> { true }
                TextFieldId.CATEGORY -> { true }
                TextFieldId.TEXT -> { false }
            },
            onClick = {
                when (itemId)
                {
                    TextFieldId.TITLE -> { }
                    TextFieldId.SUBTITLE -> {
                        viewModel.toggleEditButtonStatus(TextFieldId.SUBTITLE, isSubtitleEditing.value ?: false)
                    }
                    TextFieldId.AUTHOR -> { }
                    TextFieldId.PUBLISHER -> { }
                    TextFieldId.ISBN -> {
                        viewModel.toggleEditButtonStatus(TextFieldId.ISBN, isIsbnEditing.value ?: false)
                    }
                    TextFieldId.CATEGORY -> {
                        viewModel.toggleEditButtonStatus(TextFieldId.CATEGORY, isCategoryEditing.value ?: false)
                    }
                    TextFieldId.TEXT -> { }
                }
            },
            colors = when (itemId)
            {
                TextFieldId.SUBTITLE -> {
                    if (isSubtitleEditing.value == true)
                    {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    else
                    {
                        ButtonDefaults.buttonColors()
                    }
                }
                TextFieldId.ISBN -> {
                    if (isIsbnEditing.value == true)
                    {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                    }
                    else
                    {
                        ButtonDefaults.buttonColors()
                    }
                }
                TextFieldId.CATEGORY -> {
                    if (isCategoryEditing.value == true)
                    {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    else
                    {
                        ButtonDefaults.buttonColors()
                    }
                }
                else -> ButtonDefaults.buttonColors()
            }
        )
        {
            Text(
                when (itemId)
                {
                    TextFieldId.TITLE -> { stringResource(R.string.button_label_edit)  }
                    TextFieldId.SUBTITLE -> {
                        if (isSubtitleEditing.value == true)
                        {
                            // --- 編集中の時には、ボタンを「確定」にする
                            stringResource(R.string.button_label_set)
                        }
                        else
                        {
                            // --- 通常は、「編集」ボタン
                            stringResource(R.string.button_label_edit)
                        }
                    }
                    TextFieldId.AUTHOR -> { stringResource(R.string.button_label_edit) }
                    TextFieldId.PUBLISHER -> {stringResource(R.string.button_label_edit)  }
                    TextFieldId.ISBN -> {
                        if (isIsbnEditing.value == true)
                        {
                            // --- 編集中の時には、ボタンを「確定」にする
                            stringResource(R.string.button_label_set)
                        }
                        else
                        {
                            // --- 通常は、「編集」ボタン
                            stringResource(R.string.button_label_edit)
                        }
                    }
                    TextFieldId.CATEGORY -> {
                        if (isCategoryEditing.value == true)
                        {
                            // --- 編集中の時には、ボタンを「確定」にする
                            stringResource(R.string.button_label_set)
                        }
                        else
                        {
                            // --- 通常は、「編集」ボタン
                            stringResource(R.string.button_label_edit)
                        }
                    }
                    TextFieldId.TEXT -> { stringResource(R.string.button_label_edit) }
                }
            )
        }
        //Spacer(modifier = Modifier.padding(2.dp))
    }
}

@Composable
fun ShowCapturedImage(id: Long, imageFile1: String, imageFile2: String, imageFile3: String)
{
    val imageIn = InOutExportImage(LocalContext.current)
    Row (verticalAlignment = Alignment.CenterVertically) {
        val picture1 = imageIn.getImageLocal(id, imageFile1)
        val picture2 = imageIn.getImageLocal(id, imageFile2)
        val picture3 = imageIn.getImageLocal(id, imageFile3)

        if (picture1 != null) {
            Image(picture1.asImageBitmap(), "image1", Modifier.weight(1.0f))
        }
        else
        {
            Icon(painter = painterResource(id = R.drawable.baseline_image_24), null, Modifier.weight(1.0f), tint = MaterialTheme.colorScheme.background)
        }
        Spacer(Modifier.size(2.dp))

        if (picture2 != null) {
            Image(picture2.asImageBitmap(), "image2", Modifier.weight(1.0f))
        }
        else
        {
            Icon(painter = painterResource(id = R.drawable.baseline_image_24), null, Modifier.weight(1.0f), tint = MaterialTheme.colorScheme.background)
        }
        Spacer(Modifier.size(2.dp))

        if (picture3 != null) {
            Image(picture3.asImageBitmap(), "image3", Modifier.weight(1.0f))
        }
        else
        {
            Icon(painter = painterResource(id = R.drawable.baseline_image_24), null, Modifier.weight(1.0f), tint = MaterialTheme.colorScheme.background)
        }
    }
}
