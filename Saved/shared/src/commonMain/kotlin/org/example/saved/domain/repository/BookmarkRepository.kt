package org.example.saved.domain.repository

import org.example.saved.domain.model.AnalyzeResult
import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder

interface BookmarkRepository {
    suspend fun getFolders(
        page: Int = 1,
        limit: Int = 20,
    ): Result<List<Folder>>

    suspend fun getRecentBookmarks(): Result<List<Bookmark>>

    suspend fun getBookmarks(
        folderId: String,
        page: Int = 1,
        limit: Int = 20,
    ): Result<List<Bookmark>>

    suspend fun createFolder(name: String): Result<Folder>

    suspend fun renameFolder(
        folderId: String,
        name: String,
    ): Result<Unit>

    suspend fun deleteFolder(folderId: String): Result<Unit>

    suspend fun analyzeUrl(url: String): Result<AnalyzeResult>

    suspend fun saveBookmark(
        url: String,
        folderId: String,
        title: String,
    ): Result<Bookmark>

    suspend fun updateBookmark(
        bookmarkId: String,
        title: String? = null,
        folderId: String? = null,
    ): Result<Unit>

    suspend fun deleteBookmark(bookmarkId: String): Result<Unit>

    suspend fun searchBookmarks(
        query: String,
        page: Int = 1,
        limit: Int = 20,
    ): Result<List<Bookmark>>
}
