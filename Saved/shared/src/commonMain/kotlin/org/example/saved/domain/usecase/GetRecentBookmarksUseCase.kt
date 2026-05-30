package org.example.saved.domain.usecase

import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.repository.BookmarkRepository

class GetRecentBookmarksUseCase(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(): Result<List<Bookmark>> = repository.getRecentBookmarks()
}
