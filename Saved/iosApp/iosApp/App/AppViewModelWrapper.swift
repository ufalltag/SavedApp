import SwiftUI
import Shared

/// Корневое состояние авторизации. Один экземпляр на всё приложение:
/// решает, показывать LoginView или HomeView, и переключается при входе/выходе.
@Observable
@MainActor
final class AppViewModelWrapper {

    private(set) var isCheckingSession: Bool = true
    private(set) var isLoggedIn: Bool = false

    @ObservationIgnored private var viewModel: AppViewModel?
    @ObservationIgnored private var collector: AppViewModelCollector?
    @ObservationIgnored private var started = false

    init() {}

    func start() {
        guard !started else { return }
        started = true

        let vm = KoinHelper().getAppViewModel()
        let col = AppViewModelCollector(viewModel: vm)
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

    private func apply(_ s: AppState) {
        if isCheckingSession != s.isCheckingSession { isCheckingSession = s.isCheckingSession }
        if isLoggedIn != s.isLoggedIn { isLoggedIn = s.isLoggedIn }
    }

    func onAuthenticated() { collector?.onAuthenticated() }
    func onLoggedOut()     { collector?.onLoggedOut() }
}
