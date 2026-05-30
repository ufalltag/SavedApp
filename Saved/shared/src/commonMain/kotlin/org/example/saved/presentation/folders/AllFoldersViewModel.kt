package org.example.saved.presentation.folders

import androidx.lifecycle.ViewModel
import org.example.saved.domain.usecase.GetFoldersUseCase
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

private const val PAGE_SIZE = 20

class AllFoldersViewModel(
    private val getFoldersUseCase: GetFoldersUseCase
) : ViewModel(), ContainerHost<AllFoldersState, AllFoldersSideEffect> {

    override val container: Container<AllFoldersState, AllFoldersSideEffect> =
        container(AllFoldersState()) {
            loadFirstPage()
        }

    private fun loadFirstPage() = intent {
        reduce { state.copy(isLoading = true) }

        getFoldersUseCase(page = 1, limit = PAGE_SIZE).onSuccess { folders ->
            reduce {
                state.copy(
                    folders = folders,
                    isLoading = false,
                    currentPage = 1,
                    hasMore = folders.size >= PAGE_SIZE
                )
            }
        }.onFailure { error ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(AllFoldersSideEffect.ShowError(error.message ?: "Ошибка загрузки папок"))
        }
    }

    fun loadMore() = intent {
        if (state.isLoading || state.isLoadingMore || !state.hasMore) return@intent
        val nextPage = state.currentPage + 1
        reduce { state.copy(isLoadingMore = true) }

        getFoldersUseCase(page = nextPage, limit = PAGE_SIZE).onSuccess { newFolders ->
            reduce {
                state.copy(
                    folders = state.folders + newFolders,
                    isLoadingMore = false,
                    currentPage = nextPage,
                    hasMore = newFolders.size >= PAGE_SIZE
                )
            }
        }.onFailure { error ->
            reduce { state.copy(isLoadingMore = false) }
            postSideEffect(AllFoldersSideEffect.ShowError(error.message ?: "Ошибка загрузки папок"))
        }
    }
}
