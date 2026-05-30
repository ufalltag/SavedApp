package org.example.saved.domain.model

data class AnalyzeResult(
    val url: String,
    val title: String,
    val suggestedFolder: String?,
    val isNewFolder: Boolean,
    val confidence: Float
)
