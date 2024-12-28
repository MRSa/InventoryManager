package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.export.InOutExportImage
import jp.osdn.gokigen.inventorymanager.liaison.DetailModel
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel

@Composable
fun DetailScreen(navController: NavHostController, viewModel : InventoryViewModel, id : Long)
{
    val model = DetailModel(id)
    val padding = 6.dp

    MaterialTheme {
        val data = model.getData()
        if (data == null)
        {
            Text(
                text = "{stringResource(id = R.string.data_empty)} (id:$id)",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
        }
        else
        {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier
                //.systemBarsPadding()
                .fillMaxSize()
                .verticalScroll(scrollState)
            ) {
                ReturnToMainScreen(navController)
                Spacer(Modifier.size(padding))
                // HorizontalDivider(thickness = 1.dp)

                val imageFile1 = data.imageFile1 ?: ""
                val imageFile2 = data.imageFile2 ?: ""
                val imageFile3 = data.imageFile3 ?: ""
                if ((imageFile1.isNotEmpty()) || (imageFile2.isNotEmpty()) || (imageFile3.isNotEmpty())) {
                    ShowCapturedImage(id, imageFile1, imageFile2, imageFile3)
                }
                Spacer(Modifier.size(padding))
                ShowTextInputData(stringResource(R.string.label_title), data.title ?: "", true)
                Spacer(Modifier.size(padding))
                ShowTextInputData(
                    stringResource(R.string.label_subtitle),
                    data.subTitle ?: "",
                    true,
                )
                Spacer(Modifier.size(padding))
                ShowTextInputData(stringResource(R.string.label_author), data.author ?: "", true)
                Spacer(Modifier.size(padding))
                ShowTextInputData(
                    stringResource(R.string.label_publisher),
                    data.publisher ?: "",
                    true,
                )
                Spacer(Modifier.size(padding))
                ShowTextInputData(
                    stringResource(R.string.label_isbn),
                    data.isbn ?: "",
                    true,
                )
                Spacer(Modifier.size(padding))
                if ((data.note ?: "").isNotEmpty()) {
                    HorizontalDivider(thickness = 1.dp)
                    Spacer(Modifier.size(padding))
                    ShowTextInputData(
                        stringResource(R.string.label_text_recognition),
                        data.note ?: "",
                        false,
                    )
                    Spacer(Modifier.size(padding))
                }
            }
        }
    }
}


@Composable
fun ShowTextInputData(label: String, value: String, isSingleLine: Boolean, isEditEnable: Boolean = false)
{
    Row(
        modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp
        )
        TextField(
            enabled = isEditEnable,
            value = value,
            singleLine = isSingleLine,
            onValueChange = { },
            modifier = Modifier.weight(5.0f),
            textStyle = TextStyle(fontSize = 16.sp),
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Button(
            contentPadding = PaddingValues(6.dp),
            enabled = false,
            onClick = { },
        )
        {
            Text(stringResource(R.string.button_label_edit))
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
