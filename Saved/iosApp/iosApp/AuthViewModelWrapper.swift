import SwiftUI
import Shared

@MainActor
class AuthViewModelWrapper: ObservableObject {

    // Один экземпляр ViewModel на весь wrapper
    private let viewModel: AuthViewModel
    private let collector: AuthViewModelCollector

    @Published var state: AuthState

    init() {
        // Получаем один экземпляр из Koin (factory — каждый раз новый, но здесь вызываем один раз)
        let vm = KoinHelper().getAuthViewModel()
        // collector использует тот же vm — не создаём второй!
        let col = AuthViewModelCollector(viewModel: vm)

        viewModel = vm
        collector = col

        // Синхронно читаем начальное состояние
        state = col.currentState

        // Подписываемся на изменения: Kotlin вызывает замыкание при каждом новом State
        col.observeState { [weak self] newState in
            self?.state = newState
        }
    }

    deinit {
        collector.dispose()
    }

    func collectSideEffects(
        onNavigateToHome: @escaping () -> Void,
        onShowError: @escaping (String) -> Void
    ) {
        collector.observeSideEffects { effect in
            switch onEnum(of: effect) {
            case .navigateToHome:
                onNavigateToHome()
            case .showError(let e):
                onShowError(e.message)
            case .showMessage(let m):
                onShowError(m.message)
            }
        }
    }

    // Все события отправляем в тот же viewModel
    func onEmailChanged(_ email: String)    { viewModel.onEmailChanged(email: email) }
    func onPasswordChanged(_ pwd: String)   { viewModel.onPasswordChanged(password: pwd) }
    func toggleMode()                       { viewModel.toggleMode() }
    func submit()                           { viewModel.submit() }
}
