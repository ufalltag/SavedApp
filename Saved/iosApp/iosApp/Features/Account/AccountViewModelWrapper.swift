import SwiftUI
import Shared

@Observable
@MainActor
final class AccountViewModelWrapper {

    private(set) var email: String = ""
    private(set) var isLoading: Bool = true
    private(set) var isChangingPassword: Bool = false

    @ObservationIgnored private var viewModel: AccountViewModel?
    @ObservationIgnored private var collector: AccountViewModelCollector?
    @ObservationIgnored private var started = false
    @ObservationIgnored private var sideEffectsStarted = false

    init() {}

    func start() {
        guard !started else { return }
        started = true

        let vm = KoinHelper().getAccountViewModel()
        let col = AccountViewModelCollector(viewModel: vm)
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

    private func apply(_ s: AccountState) {
        if email != s.email { email = s.email }
        if isLoading != s.isLoading { isLoading = s.isLoading }
        if isChangingPassword != s.isChangingPassword { isChangingPassword = s.isChangingPassword }
    }

    func collectSideEffects(
        onShowError: @escaping (String) -> Void,
        onPasswordChanged: @escaping () -> Void,
        onLoggedOut: @escaping () -> Void
    ) {
        start()
        guard !sideEffectsStarted, let collector else { return }
        sideEffectsStarted = true

        collector.observeSideEffects { effect in
            switch onEnum(of: effect) {
            case .showError(let e):
                onShowError(e.message)
            case .passwordChanged:
                onPasswordChanged()
            case .loggedOut:
                onLoggedOut()
            }
        }
    }

    func changePassword(oldPassword: String, newPassword: String) {
        collector?.changePassword(oldPassword: oldPassword, newPassword: newPassword)
    }

    func logout() {
        collector?.logout()
    }
}
