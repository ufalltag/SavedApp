package org.example.saved.domain.usecase

import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.repository.BookmarkRepository

class SaveAnalyzedBookmarkUseCase(
    private val bookmarkRepository: BookmarkRepository,
) {
    suspend operator fun invoke(url: String, folderId: String, title: String): Result<Bookmark> =
        bookmarkRepository.saveBookmark(url = url, folderId = folderId, title = title)
}
