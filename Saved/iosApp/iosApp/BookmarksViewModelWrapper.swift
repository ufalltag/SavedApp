import SwiftUI
import Shared

@Observable
@MainActor
final class BookmarksViewModelWrapper {

    private(set) var folders: [Folder] = []
    private(set) var selectedFolderId: String? = nil
    private(set) var bookmarks: [Bookmark] = []
    private(set) var isFoldersLoading: Bool = true
    private(set) var isBookmarksLoading: Bool = false
    private(set) var isAnalyzing: Bool = false

    @ObservationIgnored private var viewModel: BookmarksViewModel?
    @ObservationIgnored private var collector: BookmarksViewModelCollector?
    @ObservationIgnored private var started = false
    @ObservationIgnored private var sideEffectsStarted = false

    // ВАЖНО: init обязан быть без побочных эффектов.
    // @State переоценивает выражение `BookmarksViewModelWrapper()` при КАЖДОМ
    // пересоздании родительского View (SwiftUI оставляет только первый экземпляр,
    // но инициализатор выполняется каждый раз). Если создавать здесь ViewModel,
    // в чьём init() Orbit запускает loadFolders() → сеть, получаем шквал запросов.
    init() {}

    // Создаёт ViewModel и подписку РОВНО один раз. Вызывать из .onAppear/.task.
    func start() {
        guard !started else { return }
        started = true

        let vm = KoinHelper().getBookmarksViewModel()
        let col = BookmarksViewModelCollector(viewModel: vm)
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

    // Присваиваем только изменившиеся поля (Folder/Bookmark — data class,
    // равенство структурное), чтобы перерисовывать лишь затронутые секции.
    private func apply(_ s: BookmarksState) {
        if folders != s.folders { folders = s.folders }
        if selectedFolderId != s.selectedFolderId { selectedFolderId = s.selectedFolderId }
        if bookmarks != s.bookmarks { bookmarks = s.bookmarks }
        if isFoldersLoading != s.isFoldersLoading { isFoldersLoading = s.isFoldersLoading }
        if isBookmarksLoading != s.isBookmarksLoading { isBookmarksLoading = s.isBookmarksLoading }
        if isAnalyzing != s.isAnalyzing { isAnalyzing = s.isAnalyzing }
    }

    func collectSideEffects(
        onShowToast: @escaping (String) -> Void,
        onOpenUrl: @escaping (String) -> Void
    ) {
        start() // гарантируем, что ViewModel создан
        guard !sideEffectsStarted, let collector else { return }
        sideEffectsStarted = true

        collector.observeSideEffects { effect in
            switch onEnum(of: effect) {
            case .showToast(let e):
                onShowToast(e.message)
            case .openUrl(let e):
                onOpenUrl(e.url)
            }
        }
    }

    func selectFolder(_ folderId: String)                    { viewModel?.selectFolder(folderId: folderId) }
    func analyzeAndSaveUrl(_ url: String)                    { viewModel?.analyzeAndSaveUrl(url: url) }
    func createFolder(_ name: String)                        { viewModel?.createFolder(name: name) }
    func renameFolder(_ id: String, _ name: String)          { viewModel?.renameFolder(folderId: id, newName: name) }
    func deleteFolder(_ id: String)                          { viewModel?.deleteFolder(folderId: id) }
    func openBookmark(_ url: String)                         { viewModel?.openBookmark(url: url) }
    func deleteBookmark(_ id: String)                        { viewModel?.deleteBookmark(bookmarkId: id) }
    func renameBookmark(_ id: String, _ title: String)       { viewModel?.renameBookmark(bookmarkId: id, newTitle: title) }
}
