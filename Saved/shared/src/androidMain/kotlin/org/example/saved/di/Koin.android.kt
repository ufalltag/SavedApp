package org.example.saved.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.example.saved.data.analytics.AndroidAnalyticsTracker
import org.example.saved.data.local.provideDataStore
import org.example.saved.domain.analytics.AnalyticsTracker
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module =
    module {
        single { FirebaseAnalytics.getInstance(androidContext()) }
        single { FirebaseCrashlytics.getInstance() }
        single<AnalyticsTracker> { AndroidAnalyticsTracker(get(), get()) }
        single { provideDataStore(androidContext()) }
    }
