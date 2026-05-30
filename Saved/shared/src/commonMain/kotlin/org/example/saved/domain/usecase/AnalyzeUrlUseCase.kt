package org.example.saved.domain.usecase

import org.example.saved.domain.model.AnalyzeResult
import org.example.saved.domain.repository.BookmarkRepository

class AnalyzeUrlUseCase(private val bookmarkRepository: BookmarkRepository) {
    suspend operator fun invoke(url: String): Result<AnalyzeResult> =
        bookmarkRepository.analyzeUrl(url)
}
