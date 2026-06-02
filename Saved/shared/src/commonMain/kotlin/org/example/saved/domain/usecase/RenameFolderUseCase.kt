package org.example.saved.domain.usecase

import org.example.saved.domain.repository.BookmarkRepository

class RenameFolderUseCase(
    private val repository: BookmarkRepository,
) {
    suspend operator fun invoke(
        folderId: String,
        name: String,
    ): Result<Unit> = repository.renameFolder(folderId, name)
}
