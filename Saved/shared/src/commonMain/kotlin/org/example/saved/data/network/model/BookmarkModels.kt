package org.example.saved.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder

@Serializable
data class FoldersListResponseDto(
    @SerialName("folders") val folders: List<FolderDto>
)

@Serializable
data class SingleFolderResponseDto(
    @SerialName("folder") val folder: FolderDto
)

@Serializable
data class FolderDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)

@Serializable
data class CreateFolderRequestDto(
    @SerialName("name") val name: String
)

fun FolderDto.toDomain(): Folder = Folder(
    id = id.toString(),
    name = name
)


@Serializable
data class BookmarksListResponseDto(
    @SerialName("bookmarks") val bookmarks: List<BookmarkDto>
)

@Serializable
data class SingleBookmarkResponseDto(
    @SerialName("bookmark") val bookmark: BookmarkDto
)

@Serializable
data class BookmarkDto(
    @SerialName("id") val id: Int,
    @SerialName("folderid") val folderId: Int,
    @SerialName("url") val url: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null
)

@Serializable
data class CreateBookmarkRequestDto(
    @SerialName("url") val url: String,
    @SerialName("folderid") val folderId: Int,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null
)

fun BookmarkDto.toDomain(): Bookmark = Bookmark(
    id = id.toString(),
    folderId = folderId.toString(),
    url = url,
    title = title,
    description = description
)
