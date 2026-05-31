package org.example.saved.di

import org.example.saved.data.local.SettingsStorageImpl
import org.example.saved.data.local.TokenStorageImpl
import org.example.saved.data.network.AuthApiService
import org.example.saved.data.network.BookmarkApiService
import org.example.saved.data.network.HttpClientProvider
import org.example.saved.data.repository.AuthRepositoryImpl
import org.example.saved.data.repository.BookmarkRepositoryImpl
import org.example.saved.domain.repository.AuthRepository
import org.example.saved.domain.repository.BookmarkRepository
import org.example.saved.domain.repository.SettingsStorage
import org.example.saved.domain.repository.TokenStorage
import org.example.saved.domain.usecase.ChangePasswordUseCase
import org.example.saved.domain.usecase.GetProfileUseCase
import org.example.saved.domain.usecase.CreateFolderUseCase
import org.example.saved.domain.usecase.DeleteBookmarkUseCase
import org.example.saved.domain.usecase.DeleteFolderUseCase
import org.example.saved.domain.usecase.GetBookmarksUseCase
import org.example.saved.domain.usecase.GetFoldersUseCase
import org.example.saved.domain.usecase.IsLoggedInUseCase
import org.example.saved.domain.usecase.LoginUseCase
import org.example.saved.domain.usecase.RegisterUseCase
import org.example.saved.domain.usecase.RenameFolderUseCase
import org.example.saved.domain.usecase.GetRecentBookmarksUseCase
import org.example.saved.domain.usecase.AnalyzeUrlUseCase
import org.example.saved.domain.usecase.SaveAnalyzedBookmarkUseCase
import org.example.saved.domain.usecase.UpdateBookmarkUseCase
import org.example.saved.presentation.account.AccountViewModel
import org.example.saved.presentation.app.AppViewModel
import org.example.saved.presentation.auth.LoginViewModel
import org.example.saved.presentation.auth.RegisterCredentialsViewModel
import org.example.saved.presentation.auth.RegisterUsernameViewModel
import org.example.saved.presentation.bookmarks.BookmarksViewModel
import org.example.saved.presentation.folder.FolderDetailViewModel
import org.example.saved.presentation.folderlinks.FolderLinksViewModel
import org.example.saved.presentation.folders.AllFoldersViewModel
import org.example.saved.presentation.home.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect fun platformModule(): Module

val commonModule = module {
    single<TokenStorage> { TokenStorageImpl(get()) }

    // Network
    single { HttpClientProvider(get()) }
    single { AuthApiService(get()) }
    single { BookmarkApiService(get()) }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }
    single<BookmarkRepository> { BookmarkRepositoryImpl(get()) }
    single<SettingsStorage> { SettingsStorageImpl(get()) }

    // Use cases
    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::ChangePasswordUseCase)
    factoryOf(::GetProfileUseCase)
    factoryOf(::IsLoggedInUseCase)
    factoryOf(::GetFoldersUseCase)
    factoryOf(::GetBookmarksUseCase)
    factoryOf(::CreateFolderUseCase)
    factoryOf(::RenameFolderUseCase)
    factoryOf(::DeleteFolderUseCase)
    factoryOf(::UpdateBookmarkUseCase)
    factoryOf(::DeleteBookmarkUseCase)
    factoryOf(::GetRecentBookmarksUseCase)
    factoryOf(::AnalyzeUrlUseCase)
    factoryOf(::SaveAnalyzedBookmarkUseCase)

    // ViewModels
    factoryOf(::LoginViewModel)
    factoryOf(::RegisterCredentialsViewModel)
    factory { (email: String, password: String) ->
        RegisterUsernameViewModel(get(), email, password)
    }
    factoryOf(::BookmarksViewModel)
    factoryOf(::HomeViewModel)
    factoryOf(::AllFoldersViewModel)

    factory { (folderId: String, folderName: String) ->
        FolderDetailViewModel(get())
    }

    factory { (folderId: String, folderName: String) ->
        FolderLinksViewModel(folderId, folderName, get(), get(), get(), get())
    }
    factoryOf(::AccountViewModel)
    factoryOf(::AppViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(commonModule, platformModule())
    }
}
