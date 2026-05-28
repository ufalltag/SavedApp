package org.example.saved.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object AuthRoute

@Serializable
object BookmarksRoute
@Serializable
data class FolderRoute(val id: String, val name: String)
