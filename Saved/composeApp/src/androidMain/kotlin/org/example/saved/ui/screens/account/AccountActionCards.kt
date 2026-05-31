package org.example.saved.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import saved.composeapp.generated.resources.Res
import saved.composeapp.generated.resources.ic_exit
import saved.composeapp.generated.resources.ic_lock

@Composable
fun AccountActionCards(
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        // Секция безопасности
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            SettingsRow(
                title = "Сменить пароль",
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_lock),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = onChangePasswordClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Секция выхода
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.errorContainer
        ) {
            SettingsRow(
                title = "Выйти из аккаунта",
                textColor = MaterialTheme.colorScheme.onErrorContainer,
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_exit),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                },
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) { icon() }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
