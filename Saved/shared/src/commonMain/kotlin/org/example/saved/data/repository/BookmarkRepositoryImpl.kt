package org.example.saved.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import org.example.saved.data.network.model.*
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
                val dtos = response.body<List<FolderDto>>()
                Result.success(dtos.map { it.toDomain() })
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
                val dto = response.body<FolderDto>()
                Result.success(dto.toDomain())
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

    override suspend fun saveBookmark(url: String, folderId: String, title: String): Result<Bookmark> {
        return try {
            val response = client.post("bookmarks") {
                setBody(CreateBookmarkRequestDto(url, folderId, title))
            }
            if (response.status.isSuccess()) {
                val dto = response.body<BookmarkDto>()
                Result.success(dto.toDomain())
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
                val dtos = response.body<List<BookmarkDto>>()
                Result.success(dtos.map { it.toDomain() })
            } else {
                Result.failure(Exception("HTTP ${response.status.value}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
