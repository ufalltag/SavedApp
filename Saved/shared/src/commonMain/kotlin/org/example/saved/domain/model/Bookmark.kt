package org.example.saved.domain.model

data class Bookmark(
    val id: String,
    val folderId: String,
    val url: String,
    val title: String,
    val description: String? = null
)
