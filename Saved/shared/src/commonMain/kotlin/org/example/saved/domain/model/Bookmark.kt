package org.example.saved.domain.model

import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

@OptIn(ExperimentalObjCName::class)
data class Bookmark(
    val id: String,
    val folderId: String,
    val url: String,
    val title: String,
    @property:ObjCName("bookmarkDescription") val description: String? = null
)
