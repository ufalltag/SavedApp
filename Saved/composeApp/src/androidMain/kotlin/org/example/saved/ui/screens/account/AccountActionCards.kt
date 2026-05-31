package org.example.saved.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import saved.composeapp.generated.resources.Res
import saved.composeapp.generated.resources.ic_exit
import saved.composeapp.generated.resources.ic_lock

@Composable
fun AccountActionCards(
    isDarkMode: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {

        // 1. Секция: Внешний вид (Тумблер)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            SettingsRow(
                title = "Тёмная тема",
                // Используем стандартную иконку настроек или любую другую
                icon = {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_preferences),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                // При клике на саму строку тоже меняем тему (улучшает UX)
                onClick = { onThemeToggle(!isDarkMode) },
                trailingContent = {
                    androidx.compose.material3.Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onThemeToggle(it) } // Переключаем стейт
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Секция: Пароль
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

        // 3. Секция: Выход
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
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Внутренний отступ карточки
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Расталкиваем края
        ) {
            // Левая часть (Иконка + Текст)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f) // Занимает всё доступное место
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }

            // Правая часть (Тумблер)
            if (trailingContent != null) {
                // Оборачиваем в Box для надежного позиционирования
                Box(modifier = Modifier.padding(start = 16.dp)) {
                    trailingContent()
                }
            }
        }
    }
}
