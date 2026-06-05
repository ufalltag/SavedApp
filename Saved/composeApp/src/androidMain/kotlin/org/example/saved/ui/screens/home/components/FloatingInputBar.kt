package org.example.saved.ui.screens.home.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.example.saved.R
import org.example.saved.ui.theme.AccentBlue
import org.jetbrains.compose.resources.painterResource
import saved.composeapp.generated.resources.Res
import saved.composeapp.generated.resources.ic_arrow_back
import saved.composeapp.generated.resources.ic_search
import saved.composeapp.generated.resources.ic_send

@Composable
fun FloatingInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    isAnalyzing: Boolean,
    isSearchMode: Boolean,
    onSendClick: (String) -> Unit,
    onSearchToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        InputSurface(
            text = text,
            isAnalyzing = isAnalyzing,
            isSearchMode = isSearchMode,
            onTextChange = onTextChange,
            onFocusChange = { isFocused = it },
            onActionClick = {
                if (text.isNotBlank()) {
                    if (isSearchMode) {
                        onTextChange("")
                        focusManager.clearFocus()
                        onSearchToggle()
                    } else {
                        onSendClick(text)
                        onTextChange("")
                        focusManager.clearFocus()
                    }
                }
            },
            modifier = Modifier.weight(1f),
        )

        SearchToggleButton(
            isVisible = !isSearchMode && !isFocused,
            onClick = onSearchToggle,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputSurface(
    text: String,
    isAnalyzing: Boolean,
    isSearchMode: Boolean,
    onTextChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.shadow(elevation = 8.dp, shape = CircleShape, spotColor = Color.Black.copy(alpha = 0.1f)),
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
                onValueChange = onTextChange,
                modifier =
                    Modifier
                        .weight(1f)
                        .onFocusChanged { onFocusChange(it.isFocused) },
                placeholder = {
                    Text(
                        text =
                            if (isSearchMode) {
                                stringResource(
                                    R.string.search_placeholder,
                                )
                            } else {
                                stringResource(R.string.input_placeholder)
                            },
                        color = Color.Gray,
                    )
                },
                singleLine = true,
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
            )

            ActionIconButton(
                text = text,
                isAnalyzing = isAnalyzing,
                isSearchMode = isSearchMode,
                onClick = onActionClick,
            )
        }
    }
}

@Composable
private fun ActionIconButton(
    text: String,
    isAnalyzing: Boolean,
    isSearchMode: Boolean,
    onClick: () -> Unit,
) {
    val isActive = text.isNotBlank()
    val bgColor =
        when {
            isAnalyzing -> Color.Gray
            isActive -> AccentBlue
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        }

    Box(
        modifier =
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(bgColor)
                .clickable(enabled = !isAnalyzing, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isAnalyzing -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            }

            isActive -> {
                Icon(
                    painter = painterResource(if (isSearchMode) Res.drawable.ic_arrow_back else Res.drawable.ic_send),
                    contentDescription = if (isSearchMode) "Clear" else "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }

            else -> {
                Icon(
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = "Mic placeholder",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun SearchToggleButton(
    isVisible: Boolean,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
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
            IconButton(onClick = onClick) {
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
