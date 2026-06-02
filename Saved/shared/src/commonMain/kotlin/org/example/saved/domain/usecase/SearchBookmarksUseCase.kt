package org.example.saved.domain.usecase

import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.repository.BookmarkRepository

class SearchBookmarksUseCase(private val repository: BookmarkRepository) {
    suspend operator fun invoke(query: String, page: Int = 1): Result<List<Bookmark>> =
        repository.searchBookmarks(query, page = page, limit = 20)
}
