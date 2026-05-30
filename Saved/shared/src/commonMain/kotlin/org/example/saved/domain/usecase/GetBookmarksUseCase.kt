package org.example.saved.domain.usecase

import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.repository.BookmarkRepository

class GetBookmarksUseCase(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(folderId: String, page: Int = 1, limit: Int = 20): Result<List<Bookmark>> =
        repository.getBookmarks(folderId, page, limit)
}
