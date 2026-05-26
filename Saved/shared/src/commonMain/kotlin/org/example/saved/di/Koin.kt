package org.example.saved.di

import org.example.saved.data.local.TokenStorageImpl
import org.example.saved.data.network.createHttpClient
import org.example.saved.data.repository.AuthRepositoryImpl
import org.example.saved.data.repository.BookmarkRepositoryImpl
import org.example.saved.domain.repository.AuthRepository
import org.example.saved.domain.repository.BookmarkRepository
import org.example.saved.domain.repository.TokenStorage
import org.example.saved.domain.usecase.SaveAnalyzedBookmarkUseCase
import org.example.saved.presentation.app.AppViewModel
import org.example.saved.presentation.auth.AuthViewModel
import org.example.saved.presentation.bookmarks.BookmarksViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

expect fun platformModule(): Module

val commonModule = module {
    single<TokenStorage> { TokenStorageImpl(get()) }

    single { createHttpClient(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<BookmarkRepository> { BookmarkRepositoryImpl(get()) }
    factoryOf(::AuthViewModel)
    factoryOf(::SaveAnalyzedBookmarkUseCase)
    factoryOf(::BookmarksViewModel)
    factoryOf(::AppViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(commonModule, platformModule())
    }
}
