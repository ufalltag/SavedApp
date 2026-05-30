import SwiftUI
import Shared

@Observable
@MainActor
final class RegisterUsernameViewModelWrapper {

    private(set) var username: String = ""
    private(set) var isLoading: Bool = false

    // email/password с 1-го шага нужны для создания ViewModel в start().
    @ObservationIgnored private let email: String
    @ObservationIgnored private let password: String

    @ObservationIgnored private var viewModel: RegisterUsernameViewModel?
    @ObservationIgnored private var collector: RegisterUsernameViewModelCollector?
    @ObservationIgnored private var started = false
    @ObservationIgnored private var sideEffectsStarted = false

    // Сохранять строки в init безопасно (нет побочных эффектов).
    // Сама ViewModel создаётся в start().
    init(email: String, password: String) {
        self.email = email
        self.password = password
    }

    func start() {
        guard !started else { return }
        started = true

        let vm = KoinHelper().getRegisterUsernameViewModel(email: email, password: password)
        let col = RegisterUsernameViewModelCollector(viewModel: vm)
        viewModel = vm
        collector = col

        apply(col.currentState)
        col.observeState { [weak self] newState in
            self?.apply(newState)
        }
    }

    deinit {
        collector?.dispose()
    }

    private func apply(_ s: RegisterUsernameState) {
        if username != s.username { username = s.username }
        if isLoading != s.isLoading { isLoading = s.isLoading }
    }

    func collectSideEffects(
        onNavigateToLogin: @escaping (String) -> Void,
        onShowError: @escaping (String) -> Void
    ) {
        start()
        guard !sideEffectsStarted, let collector else { return }
        sideEffectsStarted = true

        collector.observeSideEffects { effect in
            switch onEnum(of: effect) {
            case .navigateToLogin(let e):
                onNavigateToLogin(e.message)
            case .showError(let e):
                onShowError(e.message)
            }
        }
    }

    func onUsernameChanged(_ name: String) { viewModel?.onUsernameChanged(username: name) }
    func submit()                          { viewModel?.submit() }
}
