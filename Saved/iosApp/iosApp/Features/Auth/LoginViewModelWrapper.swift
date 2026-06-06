import SwiftUI
import Shared

@Observable
@MainActor
final class LoginViewModelWrapper {

    private(set) var email: String = ""
    private(set) var password: String = ""
    private(set) var isLoading: Bool = false

    @ObservationIgnored private var viewModel: LoginViewModel?
    @ObservationIgnored private var collector: LoginViewModelCollector?
    @ObservationIgnored private var started = false
    @ObservationIgnored private var sideEffectsStarted = false

    // init без побочных эффектов: @State переоценивает выражение при каждом
    // пересоздании родителя. Создание ViewModel вынесено в start().
    init() {}

    func start() {
        guard !started else { return }
        started = true

        let vm = KoinHelper().getLoginViewModel()
        let col = LoginViewModelCollector(viewModel: vm)
        viewModel = vm
        collector = col

        apply(col.currentState)
        col.observeState { [weak self] newState in
            Task { @MainActor [weak self] in
                self?.apply(newState)
            }
        }
    }

    deinit {
        collector?.dispose()
    }

    private func apply(_ s: LoginState) {
        if email != s.email { email = s.email }
        if password != s.password { password = s.password }
        if isLoading != s.isLoading { isLoading = s.isLoading }
    }

    func collectSideEffects(
        onNavigateToHome: @escaping () -> Void,
        onShowError: @escaping (String) -> Void,
        onShowMessage: @escaping (String) -> Void
    ) {
        start()
        guard !sideEffectsStarted, let collector else { return }
        sideEffectsStarted = true

        collector.observeSideEffects { effect in
            switch onEnum(of: effect) {
            case .navigateToHome:
                onNavigateToHome()
            case .showError(let e):
                onShowError(e.message)
            case .showMessage(let m):
                onShowMessage(m.message)
            }
        }
    }

    func onEmailChanged(_ email: String)  { viewModel?.onEmailChanged(email: email) }
    func onPasswordChanged(_ pwd: String) { viewModel?.onPasswordChanged(password: pwd) }
    func submit()                         { viewModel?.submit() }
}
