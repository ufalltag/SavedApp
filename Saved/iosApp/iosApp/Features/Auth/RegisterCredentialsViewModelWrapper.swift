import SwiftUI
import Shared

@Observable
@MainActor
final class RegisterCredentialsViewModelWrapper {

    private(set) var email: String = ""
    private(set) var password: String = ""

    @ObservationIgnored private var viewModel: RegisterCredentialsViewModel?
    @ObservationIgnored private var collector: RegisterCredentialsViewModelCollector?
    @ObservationIgnored private var started = false
    @ObservationIgnored private var sideEffectsStarted = false

    init() {}

    func start() {
        guard !started else { return }
        started = true

        let vm = KoinHelper().getRegisterCredentialsViewModel()
        let col = RegisterCredentialsViewModelCollector(viewModel: vm)
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

    private func apply(_ s: RegisterCredentialsState) {
        if email != s.email { email = s.email }
        if password != s.password { password = s.password }
    }

    func collectSideEffects(
        onNavigateToUsername: @escaping (_ email: String, _ password: String) -> Void,
        onShowError: @escaping (String) -> Void
    ) {
        start()
        guard !sideEffectsStarted, let collector else { return }
        sideEffectsStarted = true

        collector.observeSideEffects { effect in
            switch onEnum(of: effect) {
            case .navigateToUsername(let e):
                onNavigateToUsername(e.email, e.password)
            case .showError(let e):
                onShowError(e.message)
            }
        }
    }

    func onEmailChanged(_ email: String)  { viewModel?.onEmailChanged(email: email) }
    func onPasswordChanged(_ pwd: String) { viewModel?.onPasswordChanged(password: pwd) }
    func next()                           { viewModel?.next() }
}
