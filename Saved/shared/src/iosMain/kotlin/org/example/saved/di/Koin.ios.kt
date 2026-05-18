package org.example.saved.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.example.saved.data.local.provideDataStore

actual fun platformModule(): Module = module {
    single { provideDataStore() }
}
