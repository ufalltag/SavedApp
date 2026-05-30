package org.example.saved.data.network.model

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder

@Serializable
data class PageMetaDto(
    @SerialName("page") val page: Int,
    @SerialName("limit") val limit: Int,
    @SerialName("total") val total: Long,
    @SerialName("total_pages") val totalPages: Int
)

@Serializable
data class FoldersListResponseDto(
    // nullable: Go отдаёт nil-срез как JSON null, а не []
    @SerialName("folders") val folders: List<FolderDto>? = null,
    @SerialName("meta") val meta: PageMetaDto? = null
)

@Serializable
data class SingleFolderResponseDto(
    @SerialName("folder") val folder: FolderDto
)

@Serializable
data class FolderDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("bookmarks_count") val bookmarksCount: Long = 0
)

@Serializable
data class CreateFolderRequestDto(
    @SerialName("name") val name: String
)

fun FolderDto.toDomain(): Folder = Folder(
    id = id.toString(),
    name = name,
    bookmarksCount = bookmarksCount.toInt()
)


@Serializable
data class BookmarksListResponseDto(
    // nullable: пустая папка приходит как {"bookmarks": null} (nil-срез Go)
    @SerialName("bookmarks") val bookmarks: List<BookmarkDto>? = null,
    @SerialName("meta") val meta: PageMetaDto? = null
)

@Serializable
data class SingleBookmarkResponseDto(
    @SerialName("bookmark") val bookmark: BookmarkDto
)

@OptIn(ExperimentalObjCName::class)
@Serializable
data class BookmarkDto(
    @SerialName("id") val id: Int,
    @SerialName("folder_id") val folderId: Int,
    @SerialName("url") val url: String,
    @SerialName("title") val title: String,
    @property:ObjCName("bookmarkDescription") @SerialName("description") val description: String? = null
)

@OptIn(ExperimentalObjCName::class)
@Serializable
data class CreateBookmarkRequestDto(
    @SerialName("url") val url: String,
    @SerialName("folder_id") val folderId: Int,
    @SerialName("title") val title: String,
    @property:ObjCName("bookmarkDescription") @SerialName("description") val description: String? = null
)

fun BookmarkDto.toDomain(): Bookmark = Bookmark(
    id = id.toString(),
    folderId = folderId.toString(),
    url = url,
    title = title,
    description = description
)

@Serializable
data class UpdateFolderRequestDto(
    @SerialName("name") val name: String
)

@Serializable
data class UpdateBookmarkRequestDto(
    @SerialName("title") val title: String? = null,
    @SerialName("folder_id") val folderId: Int? = null
)
