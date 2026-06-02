package org.example.saved.ui.components.bookmarks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.example.saved.ui.theme.AccentBlue
import org.jetbrains.compose.resources.painterResource
import saved.composeapp.generated.resources.Res
import saved.composeapp.generated.resources.ic_search
import saved.composeapp.generated.resources.ic_send

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingInputBar(
    isAnalyzing: Boolean,
    onSendClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier =
                Modifier
                    .weight(1f)
                    .shadow(elevation = 8.dp, shape = CircleShape, spotColor = Color.Black.copy(alpha = 0.1f)),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier =
                        Modifier
                            .weight(1f)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                            },
                    placeholder = { Text("Type Here", color = Color.Gray) },
                    singleLine = true,
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                )

                Box(
                    modifier =
                        Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isAnalyzing -> Color.Gray

                                    text.isNotBlank() -> AccentBlue

                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                },
                            ).clickable(enabled = !isAnalyzing) {
                                if (text.isNotBlank()) {
                                    onSendClick(text)
                                    text = ""
                                    focusManager.clearFocus()
                                } else {
                                }
                            },
                    contentAlignment = Alignment.Center,
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else if (text.isNotBlank()) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_send),
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    } else {
                        Icon(
                            painter = painterResource(Res.drawable.ic_send),
                            contentDescription = "Mic",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = !isFocused,
            enter = fadeIn() + expandHorizontally(expandFrom = Alignment.End),
            exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End),
        ) {
            Surface(
                modifier =
                    Modifier
                        .size(56.dp)
                        .shadow(elevation = 8.dp, shape = CircleShape, spotColor = Color.Black.copy(alpha = 0.1f)),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
            ) {
                IconButton(onClick = { /* TODO: Действие поиска */ }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_search),
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}
