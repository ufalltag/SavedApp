package org.example.saved.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.model.Folder


@Serializable
data class FolderDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String
)

@Serializable
data class CreateFolderRequestDto(
    @SerialName("name") val name: String
)

fun FolderDto.toDomain(): Folder = Folder(id = id, name = name)

@Serializable
data class BookmarkDto(
    @SerialName("id") val id: String,
    @SerialName("folder_id") val folderId: String,
    @SerialName("url") val url: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null
)

@Serializable
data class CreateBookmarkRequestDto(
    @SerialName("url") val url: String,
    @SerialName("folder_id") val folderId: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null
)

fun BookmarkDto.toDomain(): Bookmark = Bookmark(
    id = id,
    folderId = folderId,
    url = url,
    title = title,
    description = description
)
