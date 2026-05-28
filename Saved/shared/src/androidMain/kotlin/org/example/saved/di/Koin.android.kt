package org.example.saved.di

import org.example.saved.data.local.provideDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module =
    module {
        single { provideDataStore(androidContext()) }
    }
