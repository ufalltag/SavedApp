package org.example.saved.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.saved.ui.theme.AvatarBackground
import org.example.saved.ui.theme.AvatarText

@Composable
fun ProfileHeader(
    email: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 32.dp, bottom = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(AvatarBackground),
            contentAlignment = Alignment.Center
        ) {
            val initial = email.take(1).uppercase()
            Text(
                text = initial.ifBlank { "?" },
                color = AvatarText,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = email,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}
