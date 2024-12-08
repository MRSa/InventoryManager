package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import jp.osdn.gokigen.inventorymanager.R

@Composable
fun LayoutChecker()
{
    
    val informationMessage = stringResource(R.string.label_explain_register_next)
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
                Image(
                    painter = painterResource(id = R.drawable.baseline_looks_one_24),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
                Image(
                    painter = painterResource(id = R.drawable.baseline_looks_two_24),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
                Image(
                    painter = painterResource(id = R.drawable.baseline_looks_3_24),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().weight(1.0f)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_image_24),
                    contentDescription = "Preview Area",
                    modifier = Modifier.fillMaxSize()
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
            ShowRegisterTextFieldArea(stringResource(R.string.label_register_text), stringResource(R.string.label_register_text))
            ShowRegisterTextFieldArea(stringResource(R.string.label_register_bcr), stringResource(R.string.label_register_bcr))
        }
        // 操作ボタンを配置する部分 (その１）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        )
        {
            IconButton(onClick = { }, enabled = false) {
                val iconId = R.drawable.baseline_cloud_done_24
                Icon(painter = painterResource(id = iconId), contentDescription = "ConnectionStatus")
            }


            Spacer(modifier = Modifier.weight(2.0f))

            IconButton(
                onClick = {  },
                enabled = true
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_looks_one_24),
                    contentDescription = "Capture"
                )
            }
            IconButton(
                onClick = {  },
                enabled = true
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_looks_two_24),
                    contentDescription = "Capture"
                )
            }
            IconButton(
                onClick = {  },
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
                Icon(painter = painterResource(id = iconId), contentDescription = "ConnectionStatus")
            }
            Spacer(modifier = Modifier.weight(1.0f))
            IconButton(
                onClick = {  },
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
}

@Composable
fun ShowRegisterTextFieldArea(title : String, label: String)
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



@Preview
@Composable
fun LayoutCheckerPreview()
{
    LayoutChecker()
}
