package org.example.saved.domain.usecase

import org.example.saved.domain.model.Folder
import org.example.saved.domain.repository.BookmarkRepository

class GetFoldersUseCase(
    private val repository: BookmarkRepository,
) {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 20,
    ): Result<List<Folder>> = repository.getFolders(page, limit)
}
