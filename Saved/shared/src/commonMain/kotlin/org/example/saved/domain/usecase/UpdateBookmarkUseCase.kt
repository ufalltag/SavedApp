package org.example.saved.domain.usecase

import org.example.saved.domain.repository.BookmarkRepository

/**
 * Обновление закладки: используется и для переименования (title),
 * и для перемещения в другую папку (folderId).
 */
class UpdateBookmarkUseCase(
    private val repository: BookmarkRepository,
) {
    suspend operator fun invoke(
        bookmarkId: String,
        title: String? = null,
        folderId: String? = null,
    ): Result<Unit> = repository.updateBookmark(bookmarkId, title, folderId)
}
