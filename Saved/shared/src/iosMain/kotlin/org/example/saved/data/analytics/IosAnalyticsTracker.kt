package org.example.saved.data.analytics

import org.example.saved.domain.analytics.AnalyticsTracker

class IosAnalyticsTracker : AnalyticsTracker {
    override fun logEvent(
        eventName: String,
        params: Map<String, String>,
    ) = Unit

    override fun logScreen(screenName: String) = Unit

    override fun recordException(throwable: Throwable) = Unit
}
