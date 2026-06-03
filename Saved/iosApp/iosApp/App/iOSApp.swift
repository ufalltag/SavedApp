import SwiftUI
import Shared
import FirebaseCore

@main
struct iOSApp: App {

    @AppStorage("isDarkMode") private var isDarkMode = false

    init() {
        FirebaseApp.configure()
        // Запускаем Koin DI до того как любой View попытается получить зависимость.
        // Без этого вызова KoinHelper().getLoginViewModel() бросает исключение.
        // Kotlin default-параметры не передаются в Swift — нужно явно передать пустое замыкание
        KoinKt.doInitKoin(appDeclaration: { _ in })
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .onAppear { applyTheme(isDarkMode, animated: false) }
        }
    }
}
