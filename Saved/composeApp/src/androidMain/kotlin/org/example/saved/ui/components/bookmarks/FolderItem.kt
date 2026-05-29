package org.example.saved.ui.components.bookmarks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.saved.ui.theme.AccentBlue
import org.jetbrains.compose.resources.painterResource
import saved.composeapp.generated.resources.Res
import saved.composeapp.generated.resources.folder

@Composable
fun FolderItem(
    title: String,
    linksCount: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAddButton = linksCount == null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (isAddButton) Color.White else Color.Transparent
                    ),
            contentAlignment = Alignment.Center,
        ) {
            if (isAddButton) {
                Text(
                    text = "+",
                    color = AccentBlue,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 40.sp),
                )
            } else {
                Image(
                    painter = painterResource(Res.drawable.folder),
                    contentDescription = null,
                    modifier = Modifier.size(70.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )

        if (!isAddButton) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$linksCount links",
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
