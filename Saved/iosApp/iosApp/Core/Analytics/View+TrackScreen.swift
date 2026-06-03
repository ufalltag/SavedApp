import SwiftUI

extension View {
    func trackScreen(_ name: String) -> some View {
        onAppear { AnalyticsService.logScreen(name) }
    }
}
