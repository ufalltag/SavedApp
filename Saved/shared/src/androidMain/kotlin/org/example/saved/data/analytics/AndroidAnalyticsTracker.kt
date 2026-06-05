package org.example.saved.data.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.example.saved.domain.analytics.AnalyticsTracker

class AndroidAnalyticsTracker(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val crashlytics: FirebaseCrashlytics,
) : AnalyticsTracker {
    override fun logEvent(
        eventName: String,
        params: Map<String, String>,
    ) {
        firebaseAnalytics.logEvent(eventName) {
            params.forEach { (key, value) -> param(key, value) }
        }
    }

    override fun logScreen(screenName: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        }
        firebaseAnalytics.logEvent(screenName, null)
    }

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}
