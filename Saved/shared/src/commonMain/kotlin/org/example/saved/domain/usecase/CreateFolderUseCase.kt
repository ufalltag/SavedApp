package org.example.saved.domain.usecase

import org.example.saved.domain.model.Folder
import org.example.saved.domain.repository.BookmarkRepository

class CreateFolderUseCase(
    private val repository: BookmarkRepository,
) {
    suspend operator fun invoke(name: String): Result<Folder> = repository.createFolder(name)
}
