package org.example.saved.di

import org.example.saved.data.local.TokenStorageImpl
import org.example.saved.data.network.createHttpClient
import org.example.saved.data.repository.AuthRepositoryImpl
import org.example.saved.domain.repository.AuthRepository
import org.example.saved.domain.repository.TokenStorage
import org.example.saved.presentation.auth.AuthViewModel
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
    factoryOf(::AuthViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(commonModule, platformModule())
    }
}
