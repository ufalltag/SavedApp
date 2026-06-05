package org.example.saved.domain.analytics

interface AnalyticsTracker {
    fun logEvent(
        eventName: String,
        params: Map<String, String> = emptyMap(),
    )

    fun logScreen(screenName: String)

    fun recordException(throwable: Throwable)
}
