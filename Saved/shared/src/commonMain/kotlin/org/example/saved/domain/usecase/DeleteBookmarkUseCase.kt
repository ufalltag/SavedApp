package org.example.saved.domain.usecase

import org.example.saved.domain.repository.BookmarkRepository

class DeleteBookmarkUseCase(
    private val repository: BookmarkRepository
) {
    suspend operator fun invoke(bookmarkId: String): Result<Unit> =
        repository.deleteBookmark(bookmarkId)
}
