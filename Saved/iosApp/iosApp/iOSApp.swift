import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Запускаем Koin DI до того как любой View попытается получить зависимость.
        // Без этого вызова KoinHelper().getLoginViewModel() бросает исключение.
        // Kotlin default-параметры не передаются в Swift — нужно явно передать пустое замыкание
        KoinKt.doInitKoin(appDeclaration: { _ in })
    }

    var body: some Scene {
        WindowGroup {
            HomeView()
        }
    }
}
