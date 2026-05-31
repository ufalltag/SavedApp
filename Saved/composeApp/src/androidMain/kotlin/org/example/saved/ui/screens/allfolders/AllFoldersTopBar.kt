package org.example.saved.ui.screens.allfolders

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import saved.composeapp.generated.resources.Res
import saved.composeapp.generated.resources.ic_arrow_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllFoldersTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Все папки") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_back),
                    contentDescription = "Назад"
                )
            }
        }
    )
}
