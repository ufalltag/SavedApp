import SwiftUI
import Shared

@Observable
@MainActor
final class HomeViewModelWrapper {

    private(set) var folders: [Folder] = []
    private(set) var recentBookmarks: [Bookmark] = []
    private(set) var isFoldersLoading: Bool = true
    private(set) var isBookmarksLoading: Bool = true
    private(set) var isAnalyzing: Bool = false
    private(set) var username: String? = nil

    private(set) var bookmarkPendingDelete: Bookmark?
    private(set) var bookmarkPendingMove: Bookmark?
    private(set) var folderPendingDelete: Folder?
    private(set) var folderPendingRename: Folder?

    private(set) var searchResults: [Bookmark] = []
    private(set) var isSearching: Bool = false

    @ObservationIgnored private var viewModel: HomeViewModel?
    @ObservationIgnored private var collector: HomeViewModelCollector?
    @ObservationIgnored private var started = false
    @ObservationIgnored private var sideEffectsStarted = false

    init() {}

    func start() {
        guard !started else { return }
        started = true

        let vm = KoinHelper().getHomeViewModel()
        let col = HomeViewModelCollector(viewModel: vm)
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

    private func apply(_ s: HomeState) {
        if folders != s.folders { folders = s.folders }
        if recentBookmarks != s.recentBookmarks { recentBookmarks = s.recentBookmarks }
        if isFoldersLoading != s.isFoldersLoading { isFoldersLoading = s.isFoldersLoading }
        if isBookmarksLoading != s.isBookmarksLoading { isBookmarksLoading = s.isBookmarksLoading }
        if isAnalyzing != s.isAnalyzing { isAnalyzing = s.isAnalyzing }
        if username != s.username { username = s.username }
        if bookmarkPendingDelete != s.bookmarkPendingDelete { bookmarkPendingDelete = s.bookmarkPendingDelete }
        if bookmarkPendingMove != s.bookmarkPendingMove { bookmarkPendingMove = s.bookmarkPendingMove }
        if folderPendingDelete != s.folderPendingDelete { folderPendingDelete = s.folderPendingDelete }
        if folderPendingRename != s.folderPendingRename { folderPendingRename = s.folderPendingRename }
        if searchResults != s.searchResults { searchResults = s.searchResults }
        if isSearching != s.isSearching { isSearching = s.isSearching }
    }

    func collectSideEffects(
        onOpenUrl: @escaping (String) -> Void,
        onShowError: @escaping (String) -> Void,
        onRequireFolderSelection: @escaping (_ url: String, _ suggestedFolderName: String?, _ bookmarkTitle: String) -> Void
    ) {
        start()
        guard !sideEffectsStarted, let collector else { return }
        sideEffectsStarted = true

        collector.observeSideEffects { effect in
            switch onEnum(of: effect) {
            case .openUrl(let e):
                onOpenUrl(e.url)
            case .showError(let e):
                onShowError(e.message)
            case .requireFolderSelection(let e):
                onRequireFolderSelection(e.url, e.suggestedFolderName, e.bookmarkTitle)
            }
        }
    }

    func refresh()                                    { viewModel?.refresh() }
    func createFolder(_ name: String)                 { viewModel?.createFolder(name: name) }
    func openBookmark(_ url: String)                  { viewModel?.openBookmark(url: url) }
    func analyzeUrl(_ url: String)                    { viewModel?.analyzeUrl(url: url) }
    func saveToNewFolder(url: String, folderName: String, bookmarkTitle: String) {
        viewModel?.saveToNewFolder(url: url, folderName: folderName, bookmarkTitle: bookmarkTitle)
    }
    func saveToExistingFolder(url: String, folderId: String, bookmarkTitle: String) {
        viewModel?.saveToExistingFolder(url: url, folderId: folderId, bookmarkTitle: bookmarkTitle)
    }

    // Bookmark: delete
    func requestDeleteBookmark(_ bookmark: Bookmark)   { viewModel?.requestDeleteBookmark(bookmark: bookmark) }
    func confirmDeleteBookmark()                       { viewModel?.confirmDeleteBookmark() }
    func dismissDeleteBookmark()                       { viewModel?.dismissDeleteBookmark() }

    // Bookmark: move
    func requestMoveBookmark(_ bookmark: Bookmark)     { viewModel?.requestMoveBookmark(bookmark: bookmark) }
    func confirmMoveBookmark(targetFolderId: String)   { viewModel?.confirmMoveBookmark(targetFolderId: targetFolderId) }
    func dismissMoveBookmark()                         { viewModel?.dismissMoveBookmark() }

    // Folder: delete
    func requestDeleteFolder(_ folder: Folder)         { viewModel?.requestDeleteFolder(folder: folder) }
    func confirmDeleteFolder()                         { viewModel?.confirmDeleteFolder() }
    func dismissDeleteFolder()                         { viewModel?.dismissDeleteFolder() }

    // Folder: rename
    func requestRenameFolder(_ folder: Folder)         { viewModel?.requestRenameFolder(folder: folder) }
    func confirmRenameFolder(_ newName: String)        { viewModel?.confirmRenameFolder(newName: newName) }
    func dismissRenameFolder()                         { viewModel?.dismissRenameFolder() }

    // Search
    func searchBookmarks(_ query: String)              { viewModel?.searchBookmarks(query: query) }
    func clearSearch()                                 { viewModel?.clearSearch() }
}
