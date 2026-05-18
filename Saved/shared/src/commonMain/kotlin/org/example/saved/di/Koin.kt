package org.example.saved.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.example.saved.data.local.TokenStorageImpl
import org.example.saved.data.network.createHttpClient
import org.example.saved.domain.repository.TokenStorage

expect fun platformModule(): Module

val commonModule = module {
    single<TokenStorage> { TokenStorageImpl(get()) }

    single { createHttpClient(get()) }
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(commonModule, platformModule())
    }
}