import SwiftUI
import Shared

@Observable
@MainActor
final class AllFoldersViewModelWrapper {

    private(set) var folders: [Folder] = []
    private(set) var isLoading: Bool = true
    private(set) var isLoadingMore: Bool = false
    private(set) var hasMore: Bool = true

    @ObservationIgnored private var viewModel: AllFoldersViewModel?
    @ObservationIgnored private var collector: AllFoldersViewModelCollector?
    @ObservationIgnored private var started = false
    @ObservationIgnored private var sideEffectsStarted = false

    init() {}

    func start() {
        guard !started else { return }
        started = true

        let vm = KoinHelper().getAllFoldersViewModel()
        let col = AllFoldersViewModelCollector(viewModel: vm)
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

    private func apply(_ s: AllFoldersState) {
        if folders != s.folders { folders = s.folders }
        if isLoading != s.isLoading { isLoading = s.isLoading }
        if isLoadingMore != s.isLoadingMore { isLoadingMore = s.isLoadingMore }
        if hasMore != s.hasMore { hasMore = s.hasMore }
    }

    func collectSideEffects(onShowError: @escaping (String) -> Void) {
        start()
        guard !sideEffectsStarted, let collector else { return }
        sideEffectsStarted = true

        collector.observeSideEffects { effect in
            switch onEnum(of: effect) {
            case .showError(let e):
                onShowError(e.message)
            }
        }
    }

    func loadMore() {
        collector?.loadMore()
    }
}
