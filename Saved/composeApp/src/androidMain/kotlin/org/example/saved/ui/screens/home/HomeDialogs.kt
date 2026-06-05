package org.example.saved.ui.screens.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.example.saved.R
import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder
import org.example.saved.presentation.home.HomeSideEffect

@Composable
fun HomeDialogs(
    // State
    showCreateFolderDialog: Boolean,
    bookmarkPendingDelete: Bookmark?,
    folderPendingDelete: Folder?,
    folderPendingRename: Folder?,
    aiFolderSuggestion: HomeSideEffect.RequireFolderSelection?,
    // Callbacks
    onCreateFolderDismiss: () -> Unit,
    onCreateFolderConfirm: (String) -> Unit,
    onDeleteBookmarkDismiss: () -> Unit,
    onDeleteBookmarkConfirm: () -> Unit,
    onDeleteFolderDismiss: () -> Unit,
    onDeleteFolderConfirm: () -> Unit,
    onRenameFolderDismiss: () -> Unit,
    onRenameFolderConfirm: (String) -> Unit,
    onAiSuggestionDismiss: () -> Unit,
    onAiSuggestionConfirm: (url: String, folderName: String, title: String) -> Unit,
) {
    if (showCreateFolderDialog) {
        var newFolderName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = onCreateFolderDismiss,
            title = { Text(stringResource(R.string.dialog_create_folder_title)) },
            text = {
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text(stringResource(R.string.dialog_create_folder_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFolderName.isNotBlank()) onCreateFolderConfirm(newFolderName)
                    },
                    enabled = newFolderName.isNotBlank(),
                ) { Text(stringResource(R.string.dialog_create_folder_confirm)) }
            },
            dismissButton = {
                TextButton(
                    onClick = onCreateFolderDismiss,
                ) { Text(stringResource(R.string.dialog_create_folder_dismiss)) }
            },
        )
    }

    bookmarkPendingDelete?.let { bookmark ->
        AlertDialog(
            onDismissRequest = onDeleteBookmarkDismiss,
            title = { Text(stringResource(R.string.dialog_delete_bookmark_title)) },
            text = { Text(stringResource(R.string.dialog_delete_bookmark_message, bookmark.title)) },
            confirmButton = {
                TextButton(onClick = onDeleteBookmarkConfirm) {
                    Text(stringResource(R.string.dialog_action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDeleteBookmarkDismiss) { Text(stringResource(R.string.dialog_action_cancel)) }
            },
        )
    }

    folderPendingDelete?.let { folder ->
        AlertDialog(
            onDismissRequest = onDeleteFolderDismiss,
            title = { Text(stringResource(R.string.dialog_delete_folder_title)) },
            text = { Text(stringResource(R.string.dialog_delete_folder_message, folder.name)) },
            confirmButton = {
                TextButton(onClick = onDeleteFolderConfirm) {
                    Text(stringResource(R.string.dialog_action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDeleteFolderDismiss) { Text(stringResource(R.string.dialog_action_cancel)) }
            },
        )
    }

    folderPendingRename?.let { folder ->
        var renameText by remember(folder.name) { mutableStateOf(folder.name) }
        AlertDialog(
            onDismissRequest = onRenameFolderDismiss,
            title = { Text(stringResource(R.string.dialog_rename_folder_title)) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text(stringResource(R.string.dialog_rename_folder_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                Button(
                    onClick = { onRenameFolderConfirm(renameText) },
                    enabled = renameText.isNotBlank() && renameText != folder.name,
                ) { Text(stringResource(R.string.dialog_action_save)) }
            },
            dismissButton = {
                TextButton(onClick = onRenameFolderDismiss) { Text(stringResource(R.string.dialog_action_cancel)) }
            },
        )
    }

    aiFolderSuggestion?.let { suggestion ->
        val fallbackFolder = stringResource(R.string.ai_folder_fallback_name)
        val folderName = suggestion.suggestedFolderName ?: fallbackFolder
        AlertDialog(
            onDismissRequest = onAiSuggestionDismiss,
            title = { Text(stringResource(R.string.dialog_ai_folder_title)) },
            text = { Text(stringResource(R.string.dialog_ai_folder_message, folderName)) },
            confirmButton = {
                Button(
                    onClick = { onAiSuggestionConfirm(suggestion.url, folderName, suggestion.bookmarkTitle) },
                ) { Text(stringResource(R.string.dialog_ai_folder_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = onAiSuggestionDismiss) { Text(stringResource(R.string.dialog_action_cancel)) }
            },
        )
    }
}
