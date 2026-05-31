package org.example.saved.domain.usecase

import org.example.saved.domain.model.Bookmark
import org.example.saved.domain.repository.BookmarkRepository

class SaveAnalyzedBookmarkUseCase(
    private val bookmarkRepository: BookmarkRepository
) {
    /**
     * @param url: Ссылка, которую ввел пользователь
     * @param targetFolderId: Опционально. Если пользователь выбрал папку вручную (минуя нейросеть)
     */
    suspend operator fun invoke(
        url: String,
        targetFolderId: String? = null,
        bookmarkTitle: String? = null
    ): Result<Bookmark> {

        if (targetFolderId != null) {
            return bookmarkRepository.saveBookmark(
                url = url,
                folderId = targetFolderId,
                title = bookmarkTitle ?: "New Bookmark"
            )
        }

        val analysisResult = bookmarkRepository.analyzeUrl(url).getOrElse { error ->
            return Result.failure(Exception("Нейросеть не смогла проанализировать ссылку: ${error.message}"))
        }

        // Нейросеть не уверена (confidence < 0.7) — suggestedFolder == null
        if (analysisResult.suggestedFolder == null) {
            return Result.failure(Exception("Нейросеть не смогла определить папку — выберите вручную"))
        }

        val finalFolderId: String

        if (analysisResult.isNewFolder) {
            val newFolder = bookmarkRepository.createFolder(analysisResult.suggestedFolder).getOrElse { error ->
                return Result.failure(Exception("Ошибка создания предложенной папки: ${error.message}"))
            }
            finalFolderId = newFolder.id
        } else {
            val folders = bookmarkRepository.getFolders().getOrElse { error ->
                return Result.failure(Exception("Ошибка загрузки списка папок: ${error.message}"))
            }

            val existingFolder = folders.find { it.name == analysisResult.suggestedFolder }

            if (existingFolder != null) {
                finalFolderId = existingFolder.id
            } else {
                val fallbackFolder =
                    bookmarkRepository.createFolder(analysisResult.suggestedFolder).getOrNull()
                        ?: return Result.failure(Exception("Критическая ошибка синхронизации папок"))
                finalFolderId = fallbackFolder.id
            }
        }

        return bookmarkRepository.saveBookmark(
            url = url,
            folderId = finalFolderId,
            title = analysisResult.title
        )
    }
}
