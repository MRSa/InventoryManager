package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.storage.DataContent

@Composable
fun DataItem(navController: NavHostController, data: DataContent)
{
    val dataId = data.id
    Row()
    {
        Icon(
            modifier =  Modifier.clickable(onClick = { navController.navigate("DetailScreen/$dataId") }),
            painter = painterResource(R.drawable.baseline_image_24),
            tint = if ((data.imageFile1?.isEmpty() != false)&&
                    (data.imageFile2?.isEmpty() != false)&&
                    (data.imageFile3?.isEmpty() != false))
                {
                    // 画像無し
                    if(isSystemInDarkTheme()) { Color.DarkGray } else { Color.LightGray }
                } else {
                    // 画像あり
                if(isSystemInDarkTheme()) { Color.LightGray } else { Color.DarkGray }
                },
            contentDescription = "ImageIsExist",
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clickable(onClick = { navController.navigate("DetailScreen/$dataId") })
        ) {
            data.title?.let {
                Text(
                    fontSize = 18.sp,
                    text = it,
                    color = if(isSystemInDarkTheme()) { Color.LightGray } else { Color.DarkGray },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)
                )
            }
            var secondText = "${data.author}   ${data.publisher}"
            if (data.isbn?.isNotEmpty() == true)
            {
                secondText += " ISBN:${data.isbn}"
            }
            if (data.level > 0)
            {
                secondText += "  (${stringResource(R.string.label_rating_star)}${data.level})"
            }
            Text(
                fontSize = 16.sp,
                text = secondText,
                color = if(isSystemInDarkTheme()) { Color.LightGray } else { Color.DarkGray },
                modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)
            )
        }
    }
}
