package org.example.saved.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
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
import org.example.saved.data.network.model.toDomain
import org.example.saved.domain.model.AnalyzeResult
import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder
import org.example.saved.domain.repository.BookmarkRepository

class BookmarkRepositoryImpl(
    private val client: HttpClient
) : BookmarkRepository {

    override suspend fun getFolders(): Result<List<Folder>> {
        return try {
            val response = client.get("folders")
            if (response.status.isSuccess()) {
                val responseDto = response.body<FoldersListResponseDto>()
                Result.success(responseDto.folders.map { it.toDomain() })
            } else {
                Result.failure(Exception("HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createFolder(name: String): Result<Folder> {
        return try {
            val response = client.post("folders") {
                setBody(CreateFolderRequestDto(name))
            }
            if (response.status.isSuccess()) {
                val responseDto = response.body<SingleFolderResponseDto>()
                Result.success(responseDto.folder.toDomain())
            } else {
                Result.failure(Exception("HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun renameFolder(folderId: String, name: String): Result<Unit> {
        return try {
            val response = client.put("folders/$folderId") {
                setBody(UpdateFolderRequestDto(name))
            }
            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFolder(folderId: String): Result<Unit> {
        return try {
            val response = client.delete("folders/$folderId")
            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBookmark(bookmarkId: String, title: String?, folderId: String?): Result<Unit> {
        return try {
            val response = client.put("bookmarks/$bookmarkId") {
                setBody(UpdateBookmarkRequestDto(title = title, folderId = folderId?.toInt()))
            }
            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBookmark(bookmarkId: String): Result<Unit> {
        return try {
            val response = client.delete("bookmarks/$bookmarkId")
            if (response.status.isSuccess()) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun analyzeUrl(url: String): Result<AnalyzeResult> {
        return try {
            val response = client.post("analyze") {
                setBody(AnalyzeRequestDto(url))
            }
            if (response.status.isSuccess()) {
                val dto = response.body<AnalyzeResponseDto>()
                val result = AnalyzeResult(
                    url = dto.url,
                    title = dto.title,
                    suggestedFolder = dto.suggestedFolder,
                    isNewFolder = dto.isNewFolder,
                    confidence = dto.confidence
                )
                Result.success(result)
            } else {
                Result.failure(Exception("Ошибка нейросети: HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Сетевая ошибка при анализе: ${e.message}"))
        }
    }

    override suspend fun saveBookmark(
        url: String,
        folderId: String,
        title: String
    ): Result<Bookmark> {
        return try {
            val response = client.post("bookmarks") {
                setBody(CreateBookmarkRequestDto(url, folderId.toInt(), title))
            }
            if (response.status.isSuccess()) {
                val responseDto = response.body<SingleBookmarkResponseDto>()
                Result.success(responseDto.bookmark.toDomain())
            } else {
                Result.failure(Exception("HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBookmarks(folderId: String): Result<List<Bookmark>> {
        return try {
            val response = client.get("folders/$folderId/bookmarks")
            if (response.status.isSuccess()) {
                val responseDto = response.body<BookmarksListResponseDto>()
                Result.success(responseDto.bookmarks.map { it.toDomain() })
            } else {
                Result.failure(Exception("HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
