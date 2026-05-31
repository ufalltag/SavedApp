package org.example.saved.data.repository

import org.example.saved.data.network.ApiException
import org.example.saved.data.network.BookmarkApiService
import org.example.saved.data.network.model.toDomain
import org.example.saved.domain.model.AnalyzeResult
import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder
import org.example.saved.domain.repository.BookmarkRepository

class BookmarkRepositoryImpl(
    private val api: BookmarkApiService
) : BookmarkRepository {

    override suspend fun getFolders(page: Int, limit: Int): Result<List<Folder>> =
        api.getFolders(page, limit).map { dto -> dto.folders.orEmpty().map { it.toDomain() } }

    override suspend fun getRecentBookmarks(): Result<List<Bookmark>> =
        api.getRecentBookmarks().map { dto -> dto.bookmarks.orEmpty().map { it.toDomain() } }

    override suspend fun createFolder(name: String): Result<Folder> =
        api.createFolder(name).map { it.folder.toDomain() }

    override suspend fun renameFolder(folderId: String, name: String): Result<Unit> =
        api.renameFolder(folderId, name)

    override suspend fun deleteFolder(folderId: String): Result<Unit> =
        api.deleteFolder(folderId)

    override suspend fun getBookmarks(folderId: String, page: Int, limit: Int): Result<List<Bookmark>> =
        api.getBookmarks(folderId, page, limit).map { dto -> dto.bookmarks.orEmpty().map { it.toDomain() } }

    override suspend fun saveBookmark(url: String, folderId: String, title: String): Result<Bookmark> =
        api.createBookmark(url, folderId.toInt(), title).map { it.bookmark.toDomain() }

    override suspend fun updateBookmark(bookmarkId: String, title: String?, folderId: String?): Result<Unit> =
        api.updateBookmark(bookmarkId, title, folderId?.toInt())

    override suspend fun deleteBookmark(bookmarkId: String): Result<Unit> =
        api.deleteBookmark(bookmarkId)

    override suspend fun analyzeUrl(url: String): Result<AnalyzeResult> =
        api.analyze(url).map { dto ->
            AnalyzeResult(
                url = dto.url,
                title = dto.title,
                suggestedFolder = dto.suggestedFolder,
                isNewFolder = dto.isNewFolder,
                confidence = dto.confidence
            )
        }
}
