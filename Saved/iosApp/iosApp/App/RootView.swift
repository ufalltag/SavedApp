import SwiftUI

/// Корень приложения: переключает Login / Home по состоянию авторизации.
/// Без сплэша — пока сессия проверяется, показываем экран по текущему флагу
/// (для незалогиненного пользователя это Login).
struct RootView: View {

    @State private var appWrapper = AppViewModelWrapper()

    var body: some View {
        Group {
            if appWrapper.isCheckingSession {
                Color(.systemBackground)
                    .ignoresSafeArea()
            } else if appWrapper.isLoggedIn {
                HomeView()
            } else {
                LoginView(onAuthenticated: { appWrapper.onAuthenticated() })
            }
        }
        .environment(appWrapper)
        .task { appWrapper.start() }
    }
}
