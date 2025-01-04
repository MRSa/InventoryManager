package jp.osdn.gokigen.inventorymanager.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import jp.osdn.gokigen.inventorymanager.R

@Composable
fun RatingBar(
    value: Int = 0,
    onValueChange: (Int) -> Unit,
    numberOfStars: Int = 7,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(numberOfStars) { index ->
            Icon(
                painter = if (index < value) {
                    painterResource(R.drawable.baseline_star_24)
                } else {
                    painterResource(R.drawable.baseline_star_outline_24)
                },
                tint = if (index < value) { MaterialTheme.colorScheme.primary } else { MaterialTheme.colorScheme.secondary },
                contentDescription = "Star",
                modifier = Modifier
                    .clickable { onValueChange(index + 1) }
                    .size(24.dp)
            )
        }
    }
}
