package org.example.saved.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnalyzeRequestDto(
    @SerialName("url") val url: String
)

@Serializable
data class AnalyzeResponseDto(
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("suggested_folder") val suggestedFolder: String?,
    @SerialName("is_new_folder") val isNewFolder: Boolean,
    @SerialName("confidence") val confidence: Float,
    @SerialName("message") val message: String? = null
)
