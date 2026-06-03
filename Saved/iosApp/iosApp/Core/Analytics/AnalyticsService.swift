import FirebaseAnalytics
import FirebaseCrashlytics

enum AnalyticsService {

    static func logScreen(_ name: String) {
        Analytics.logEvent(AnalyticsEventScreenView, parameters: [
            AnalyticsParameterScreenName: name,
        ])
    }

    static func setUser(id: String?) {
        Analytics.setUserID(id)
        Crashlytics.crashlytics().setUserID(id ?? "")
    }

    static func logError(_ error: Error) {
        Crashlytics.crashlytics().record(error: error)
    }
}
