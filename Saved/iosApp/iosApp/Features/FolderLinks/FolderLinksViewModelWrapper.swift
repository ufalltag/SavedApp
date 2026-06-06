import SwiftUI
import Shared

@Observable
@MainActor
final class FolderLinksViewModelWrapper {

    private(set) var bookmarks: [Bookmark] = []
    private(set) var folders: [Folder] = []
    private(set) var isLoading: Bool = true
    private(set) var isLoadingMore: Bool = false
    private(set) var hasMore: Bool = true
    private(set) var bookmarkPendingDelete: Bookmark?
    private(set) var bookmarkPendingMove: Bookmark?

    @ObservationIgnored private var viewModel: FolderLinksViewModel?
    @ObservationIgnored private var collector: FolderLinksViewModelCollector?
    @ObservationIgnored private var started = false
    @ObservationIgnored private var sideEffectsStarted = false

    private let folderId: String
    private let folderName: String

    init(folderId: String, folderName: String) {
        self.folderId = folderId
        self.folderName = folderName
    }

    func start() {
        guard !started else { return }
        started = true

        let vm = KoinHelper().getFolderLinksViewModel(folderId: folderId, folderName: folderName)
        let col = FolderLinksViewModelCollector(viewModel: vm)
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

    private func apply(_ s: FolderLinksState) {
        if bookmarks != s.bookmarks { bookmarks = s.bookmarks }
        if folders != s.folders { folders = s.folders }
        if isLoading != s.isLoading { isLoading = s.isLoading }
        if isLoadingMore != s.isLoadingMore { isLoadingMore = s.isLoadingMore }
        if hasMore != s.hasMore { hasMore = s.hasMore }
        if bookmarkPendingDelete != s.bookmarkPendingDelete { bookmarkPendingDelete = s.bookmarkPendingDelete }
        if bookmarkPendingMove != s.bookmarkPendingMove { bookmarkPendingMove = s.bookmarkPendingMove }
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

    func loadMore()                                         { collector?.loadMore() }
    func requestDeleteBookmark(_ b: Bookmark)              { collector?.requestDeleteBookmark(bookmark: b) }
    func confirmDeleteBookmark()                           { collector?.confirmDeleteBookmark() }
    func dismissDeleteBookmark()                           { collector?.dismissDeleteBookmark() }
    func requestMoveBookmark(_ b: Bookmark)                { collector?.requestMoveBookmark(bookmark: b) }
    func confirmMoveBookmark(targetFolderId: String)       { collector?.confirmMoveBookmark(targetFolderId: targetFolderId) }
    func dismissMoveBookmark()                             { collector?.dismissMoveBookmark() }
}
