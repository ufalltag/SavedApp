package org.example.saved.domain.usecase

import org.example.saved.domain.repository.BookmarkRepository

class DeleteFolderUseCase(
    private val repository: BookmarkRepository,
) {
    suspend operator fun invoke(folderId: String): Result<Unit> = repository.deleteFolder(folderId)
}
