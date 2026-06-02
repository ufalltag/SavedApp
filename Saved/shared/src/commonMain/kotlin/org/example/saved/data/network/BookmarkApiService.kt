package org.example.saved.data.network

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import org.example.saved.data.network.model.AnalyzeRequestDto
import org.example.saved.data.network.model.AnalyzeResponseDto
import org.example.saved.data.network.model.BookmarksListResponseDto
import org.example.saved.data.network.model.CreateBookmarkRequestDto
import org.example.saved.data.network.model.CreateFolderRequestDto
import org.example.saved.data.network.model.FoldersListResponseDto
import org.example.saved.data.network.model.SingleBookmarkResponseDto
import org.example.saved.data.network.model.SingleFolderResponseDto
import org.example.saved.data.network.model.UpdateBookmarkRequestDto
import org.example.saved.data.network.model.UpdateFolderRequestDto

/**
 * Транспортный слой для эндпоинтов папок/закладок.
 *
 * Отвечает ТОЛЬКО за HTTP-вызовы и DTO. Никакого маппинга в domain-модели,
 * никакой бизнес-логики — этим занимается [BookmarkRepository].
 *
 * Берёт клиент через [HttpClientProvider], чтобы после логина (recreate)
 * автоматически использовать свежий клиент с актуальным токеном.
 */
class BookmarkApiService(
    private val clientProvider: HttpClientProvider,
) {
    private val client get() = clientProvider.client

    suspend fun getFolders(
        page: Int = 1,
        limit: Int = 20,
    ): Result<FoldersListResponseDto> =
        safeApiCall {
            client.get("folders") {
                parameter("page", page)
                parameter("limit", limit)
            }
        }

    suspend fun getRecentBookmarks(): Result<BookmarksListResponseDto> = safeApiCall { client.get("bookmarks/recent") }

    suspend fun createFolder(name: String): Result<SingleFolderResponseDto> =
        safeApiCall { client.post("folders") { setBody(CreateFolderRequestDto(name)) } }

    suspend fun renameFolder(
        folderId: String,
        name: String,
    ): Result<Unit> = safeApiCallNoContent { client.put("folders/$folderId") { setBody(UpdateFolderRequestDto(name)) } }

    suspend fun deleteFolder(folderId: String): Result<Unit> =
        safeApiCallNoContent { client.delete("folders/$folderId") }

    suspend fun getBookmarks(
        folderId: String,
        page: Int = 1,
        limit: Int = 20,
    ): Result<BookmarksListResponseDto> =
        safeApiCall {
            client.get("folders/$folderId/bookmarks") {
                parameter("page", page)
                parameter("limit", limit)
            }
        }

    suspend fun createBookmark(
        url: String,
        folderId: Int,
        title: String,
    ): Result<SingleBookmarkResponseDto> =
        safeApiCall { client.post("bookmarks") { setBody(CreateBookmarkRequestDto(url, folderId, title)) } }

    suspend fun updateBookmark(
        bookmarkId: String,
        title: String?,
        folderId: Int?,
    ): Result<Unit> =
        safeApiCallNoContent {
            client.put(
                "bookmarks/$bookmarkId",
            ) { setBody(UpdateBookmarkRequestDto(title = title, folderId = folderId)) }
        }

    suspend fun deleteBookmark(bookmarkId: String): Result<Unit> =
        safeApiCallNoContent { client.delete("bookmarks/$bookmarkId") }

    suspend fun searchBookmarks(q: String, page: Int = 1, limit: Int = 20): Result<BookmarksListResponseDto> =
        safeApiCall {
            client.get("bookmarks/search") {
                parameter("q", q)
                parameter("page", page)
                parameter("limit", limit)
            }
        }

    suspend fun analyze(url: String): Result<AnalyzeResponseDto> =
        safeApiCall { client.post("analyze") { setBody(AnalyzeRequestDto(url)) } }

    suspend fun searchBookmarks(
        query: String,
        page: Int = 1,
        limit: Int = 20
    ): Result<BookmarksListResponseDto> = safeApiCall {
        client.get("/bookmarks/search") {
            parameter("q", query)
            parameter("page", page)
            parameter("limit", limit)
        }
    }
}
