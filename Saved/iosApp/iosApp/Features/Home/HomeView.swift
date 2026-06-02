import SwiftUI
import UIKit
import Shared

struct HomeView: View {

    @State private var vm = HomeViewModelWrapper()
    @State private var input: String = ""
    @State private var showCreateFolderAlert = false
    @State private var newFolderName = ""
    @FocusState private var isFocused: Bool
    @State private var showInlineTitle = false
    @State private var headerHeight: CGFloat = 0
    @State private var showAllFolders = false
    @State private var folderLinksFolderId: String = ""
    @State private var folderLinksFolderName: String = ""
    @State private var showFolderLinks = false
    @State private var folderSelectionContext: FolderSelectionContext?
    @State private var errorMessage: String?
    @State private var renameFolderText: String = ""
    @State private var showAccount = false
    @StateObject private var speechRecognizer = SpeechRecognizer()
    @State private var showSearch = false
    @State private var safariURL: SafariURL?

    var body: some View {
        content
            .task {
                vm.collectSideEffects(
                    onOpenUrl: { _ in },
                    onShowError: { errorMessage = $0 },
                    onRequireFolderSelection: { url, suggested, title in
                        folderSelectionContext = FolderSelectionContext(url: url, suggestedFolderName: suggested, bookmarkTitle: title)
                    }
                )
            }
            .alert(String.alertTitle, isPresented: $showCreateFolderAlert) {
                alertContent
            }
            .alert(String.errorAlertTitle, isPresented: Binding(
                get: { errorMessage != nil },
                set: { if !$0 { errorMessage = nil } }
            )) {
                Button(String.alertCancel, role: .cancel) { errorMessage = nil }
            } message: {
                Text(errorMessage ?? "")
            }
            .sheet(isPresented: $showAccount) {
                AccountView()
                    .presentationDragIndicator(.hidden)
            }
            .sheet(item: $safariURL) { item in
                SafariView(url: item.url)
                    .ignoresSafeArea()
            }
            .fullScreenCover(isPresented: $showSearch) {
                SearchView(
                    searchResults: vm.searchResults,
                    isSearching: vm.isSearching,
                    onSearch: { vm.searchBookmarks($0) },
                    onClear: { vm.clearSearch() },
                    onDismiss: { showSearch = false }
                )
            }
            .sheet(item: $folderSelectionContext) { context in
                FolderSelectionSheet(
                    context: context,
                    folders: vm.folders,
                    onCreateNew: { name in
                        folderSelectionContext = nil
                        vm.saveToNewFolder(url: context.url, folderName: name, bookmarkTitle: context.bookmarkTitle)
                    },
                    onSelectExisting: { folderId in
                        folderSelectionContext = nil
                        vm.saveToExistingFolder(url: context.url, folderId: folderId, bookmarkTitle: context.bookmarkTitle)
                    },
                    onDismiss: { folderSelectionContext = nil }
                )
                .presentationDetents([.medium])
                .presentationDragIndicator(.hidden)
            }
            // Move bookmark — half sheet
            .sheet(isPresented: Binding(
                get: { vm.bookmarkPendingMove != nil },
                set: { if !$0 { vm.dismissMoveBookmark() } }
            )) {
                if let bookmark = vm.bookmarkPendingMove {
                    MoveBookmarkSheet(
                        bookmark: bookmark,
                        folders: vm.folders,
                        onSelectFolder: { folderId in
                            vm.confirmMoveBookmark(targetFolderId: folderId)
                        },
                        onDismiss: { vm.dismissMoveBookmark() }
                    )
                    .presentationDetents([.medium])
                    .presentationDragIndicator(.hidden)
                }
            }
            // Delete bookmark — confirmation alert
            .alert(String.deleteBookmarkTitle, isPresented: Binding(
                get: { vm.bookmarkPendingDelete != nil },
                set: { if !$0 { vm.dismissDeleteBookmark() } }
            )) {
                Button(String.deleteConfirm, role: .destructive) { vm.confirmDeleteBookmark() }
                Button(String.alertCancel, role: .cancel) { vm.dismissDeleteBookmark() }
            } message: {
                Text(String.deleteBookmarkMessage)
            }
            // Delete folder — confirmation alert
            .alert(String.deleteFolderTitle, isPresented: Binding(
                get: { vm.folderPendingDelete != nil },
                set: { if !$0 { vm.dismissDeleteFolder() } }
            )) {
                Button(String.deleteConfirm, role: .destructive) { vm.confirmDeleteFolder() }
                Button(String.alertCancel, role: .cancel) { vm.dismissDeleteFolder() }
            } message: {
                Text(String.deleteFolderMessage)
            }
            // Rename folder — alert with text field + warning
            .alert(String.renameFolderTitle, isPresented: Binding(
                get: { vm.folderPendingRename != nil },
                set: { if !$0 { vm.dismissRenameFolder() } }
            )) {
                TextField(String.alertPlaceholder, text: $renameFolderText)
                Button(String.renameConfirm) {
                    let name = renameFolderText.trimmingCharacters(in: .whitespaces)
                    vm.confirmRenameFolder(name)
                    renameFolderText = ""
                }
                Button(String.alertCancel, role: .cancel) {
                    vm.dismissRenameFolder()
                    renameFolderText = ""
                }
            } message: {
                Text(String.renameFolderMessage)
            }
    }
}

// MARK: - Content

private extension HomeView {

    var content: some View {
        NavigationStack {
            Group {
                if !vm.isFoldersLoading && vm.folders.isEmpty {
                    emptyFoldersContent
                } else {
                    scrollContent
                }
            }
            .background(Color(.systemGroupedBackground))
            .navigationTitle("")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text(vm.username ?? "")
                        .font(.headline)
                        .opacity(showInlineTitle ? 1 : 0)
                        .animation(.easeInOut(duration: .titleAnimationDuration), value: showInlineTitle)
                }
            }
            .navigationDestination(isPresented: $showAllFolders) {
                AllFoldersView()
            }
            .navigationDestination(isPresented: $showFolderLinks) {
                FolderLinksView(folderId: folderLinksFolderId, folderName: folderLinksFolderName)
            }
        }
        .safeAreaInset(edge: .bottom) {
            bottomBar
        }
        .overlay {
            if vm.isAnalyzing {
                analyzeLoader
            }
        }
    }

    var analyzeLoader: some View {
        ZStack {
            Color.black.opacity(.loaderBackgroundOpacity)
                .ignoresSafeArea()
            VStack(spacing: .loaderSpacing) {
                ProgressView()
                    .scaleEffect(.loaderScale)
                    .tint(.primary)
                Text(String.loaderText)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
            .padding(.loaderPadding)
            .background(.regularMaterial, in: RoundedRectangle(cornerRadius: .loaderCornerRadius))
        }
        .transition(.opacity.animation(.easeInOut(duration: .loaderAnimationDuration)))
    }

    var emptyFoldersContent: some View {
        EmptyFoldersView(onCreateFolder: { showCreateFolderAlert = true })
    }

    var scrollContent: some View {
        ScrollView {
            scrollOffsetTracker
            titleHeader
                .onGeometryChange(for: CGFloat.self) { $0.size.height } action: {
                    headerHeight = $0
                }
            VStack(spacing: .sectionSpacing) {
                FolderGridView(
                    folders: folderItems,
                    onCreateFolder: { showCreateFolderAlert = true },
                    onSeeAll: { showAllFolders = true },
                    onFolderTap: { item in
                        folderLinksFolderId = item.folderId
                        folderLinksFolderName = item.title
                        showFolderLinks = true
                    },
                    onRenameFolder: { item in
                        guard let folder = vm.folders.first(where: { $0.id == item.folderId }) else { return }
                        renameFolderText = folder.name
                        vm.requestRenameFolder(folder)
                    },
                    onDeleteFolder: { item in
                        guard let folder = vm.folders.first(where: { $0.id == item.folderId }) else { return }
                        vm.requestDeleteFolder(folder)
                    }
                )
                RecentLinksListView(
                    links: linkItems,
                    onTap: { link in
                        if let url = URL(string: link.url) {
                            safariURL = SafariURL(url: url)
                        }
                    },
                    onMove: { link in
                        guard let bookmark = vm.recentBookmarks.first(where: { $0.id == link.bookmarkId }) else { return }
                        vm.requestMoveBookmark(bookmark)
                    },
                    onDelete: { link in
                        guard let bookmark = vm.recentBookmarks.first(where: { $0.id == link.bookmarkId }) else { return }
                        vm.requestDeleteBookmark(bookmark)
                    }
                )
            }
            .padding(.bottom, .scrollBottomPadding)
        }
        .scrollIndicators(.hidden)
        .coordinateSpace(name: String.scrollCoordinateSpace)
        .onPreferenceChange(ScrollOffsetKey.self) { offset in
            let shouldShow = offset > headerHeight * .titleShowThreshold
            guard shouldShow != showInlineTitle else { return }
            showInlineTitle = shouldShow
        }
    }

    var scrollOffsetTracker: some View {
        Color.clear
            .frame(height: 0)
            .background {
                GeometryReader { geo in
                    Color.clear.preference(
                        key: ScrollOffsetKey.self,
                        value: -geo.frame(in: .named(String.scrollCoordinateSpace)).minY
                    )
                }
            }
    }

    var titleHeader: some View {
        HStack(alignment: .center, spacing: .headerSpacing) {
            avatarView
            VStack(alignment: .leading, spacing: .headerTextSpacing) {
                Text(greetingText)
                    .font(.system(size: .greetingFontSize, weight: .bold))
                Text(formattedDate)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
            Spacer()
        }
        .padding(.horizontal, .horizontalPadding)
        .padding(.bottom, .headerBottomPadding)
    }

    var avatarView: some View {
        let letter = vm.username.flatMap { $0.first.map(String.init) } ?? "?"
        return Button(action: { showAccount = true }) {
            Circle()
                .fill(Color.accentColor.opacity(.avatarBackgroundOpacity))
                .frame(width: .avatarSize, height: .avatarSize)
                .overlay {
                    Text(letter)
                        .font(.body.weight(.semibold))
                        .foregroundStyle(Color.accentColor)
                }
        }
        .buttonStyle(.plain)
    }

    @ViewBuilder
    var alertContent: some View {
        TextField(String.alertPlaceholder, text: $newFolderName)
        Button(String.alertCreate) {
            let name = newFolderName.trimmingCharacters(in: .whitespaces)
            if !name.isEmpty { vm.createFolder(name) }
            newFolderName = ""
        }
        Button(String.alertCancel, role: .cancel) {
            newFolderName = ""
        }
    }

    var bottomBar: some View {
        let fillColor = Color.gray.opacity(.bottomBarFillOpacity)
        return AnimatedBottomBar(
            hint: String.bottomBarHint,
            text: $input,
            isFocused: $isFocused
        ) {
            bottomBarLeadingButtons(fillColor: fillColor)
        } trailingAction: {
            trailingActionButton(fillColor: fillColor)
        } mainAction: {
            mainActionButton
        }
        .padding(.horizontal, .horizontalPadding)
        .padding(.bottom, .bottomBarBottomPadding)
    }

    @ViewBuilder
    func bottomBarLeadingButtons(fillColor: Color) -> some View {
        Button(action: {
            guard let pasted = UIPasteboard.general.string?
                .trimmingCharacters(in: .whitespaces),
                !pasted.isEmpty else { return }
            input += pasted
            isFocused = true
        }) {
            Image(systemName: String.librarySymbol)
                .fontWeight(.medium)
                .foregroundStyle(Color.primary)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(fillColor, in: .circle)
        }
        Button(action: {}) {
            Image(systemName: String.searchSymbol)
                .fontWeight(.medium)
                .foregroundStyle(Color.primary)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(fillColor, in: .circle)
        }
        microphoneButton(fillColor: fillColor)
    }

    func microphoneButton(fillColor: Color) -> some View {
        Button(action: { speechRecognizer.toggle() }) {
            Image(systemName: speechRecognizer.isRecording ? String.microphoneActiveSymbol : String.microphoneSymbol)
                .foregroundStyle(speechRecognizer.isRecording ? Color.white : Color.primary)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(speechRecognizer.isRecording ? Color.red : fillColor, in: .circle)
        }
        .onChange(of: speechRecognizer.transcript) { _, newValue in
            input = newValue
            isFocused = true
        }
    }

    func trailingActionButton(fillColor: Color) -> some View {
        Button(action: {
            guard isFocused else {
                guard let pasted = UIPasteboard.general.string?
                    .trimmingCharacters(in: .whitespaces),
                    !pasted.isEmpty else { return }
                input += pasted
                isFocused = true
                return
            }
            let url = input.trimmingCharacters(in: .whitespaces)
            guard !url.isEmpty else { return }
            vm.analyzeUrl(url)
            input = ""
            isFocused = false
        }) {
            ZStack {
                Group {
                    if vm.isAnalyzing {
                        ProgressView()
                            .tint(.white)
                    } else {
                        Image(systemName: String.sendSymbol)
                            .fontWeight(.medium)
                            .foregroundStyle(Color.primary)
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(.blue.gradient, in: .circle)
                .blur(radius: isFocused ? 0 : .buttonBlurRadius)
                .opacity(isFocused ? 1 : 0)
                Image(systemName: String.librarySymbol)
                    .foregroundStyle(Color.primary)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                    .background(fillColor, in: .circle)
                    .blur(radius: !isFocused ? 0 : .buttonBlurRadius)
                    .opacity(!isFocused ? 1 : 0)
            }
        }
        .disabled(vm.isAnalyzing)
    }

    var mainActionButton: some View {
        Button(action: { showSearch = true }) {
            Image(systemName: String.searchSymbol)
                .font(.body)
                .foregroundStyle(Color.primary)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }
}

// MARK: - Computed

private extension HomeView {

    var folderItems: [FolderItem] {
        vm.folders.map { FolderItem(folderId: $0.id, title: $0.name, linksCount: Int($0.bookmarksCount)) }
    }

    var linkItems: [LinkItem] {
        vm.recentBookmarks.map { LinkItem(bookmarkId: $0.id, title: $0.title, url: $0.url) }
    }

    var greetingText: String {
        let name = vm.username ?? String.userNameFallback
        let hour = Calendar.current.component(.hour, from: Date())
        switch hour {
        case 5..<12:  return "\(String.greetingMorning), \(name)"
        case 12..<17: return "\(String.greetingAfternoon), \(name)"
        case 17..<21: return "\(String.greetingEvening), \(name)"
        default:      return "\(String.greetingNight), \(name)"
        }
    }

    var formattedDate: String {
        let formatter = DateFormatter()
        formatter.dateFormat = String.dateFormat
        return formatter.string(from: Date())
    }
}

// MARK: - Scroll Preference Key

private struct ScrollOffsetKey: PreferenceKey {
    static var defaultValue: CGFloat = 0
    static func reduce(value: inout CGFloat, nextValue: () -> CGFloat) { value = nextValue() }
}

// MARK: - Constants

private extension CGFloat {

    static let sectionSpacing: CGFloat = 36
    static let scrollBottomPadding: CGFloat = 80
    static let horizontalPadding: CGFloat = 16
    static let headerSpacing: CGFloat = 16
    static let headerTextSpacing: CGFloat = 2
    static let headerBottomPadding: CGFloat = 28
    static let avatarSize: CGFloat = 48
    static let greetingFontSize: CGFloat = 26
    static let bottomBarBottomPadding: CGFloat = 10
    static let buttonBlurRadius: CGFloat = 5
    static let titleShowThreshold: CGFloat = 0.5
    static let loaderSpacing: CGFloat = 12
    static let loaderPadding: CGFloat = 24
    static let loaderCornerRadius: CGFloat = 16
    static let loaderScale: CGFloat = 1.3
}

private extension Double {

    static let avatarBackgroundOpacity: Double = 0.2
    static let bottomBarFillOpacity: Double = 0.15
    static let titleAnimationDuration: Double = 0.2
    static let loaderBackgroundOpacity: Double = 0.3
    static let loaderAnimationDuration: Double = 0.2
}

private extension String {

    static let scrollCoordinateSpace = "scrollView"
    static let userNameFallback = "there"
    static let greetingMorning = "Good morning"
    static let greetingAfternoon = "Good afternoon"
    static let greetingEvening = "Good evening"
    static let greetingNight = "Good night"
    static let dateFormat = "EEEE, d MMMM"
    static let alertTitle = "New Folder"
    static let alertPlaceholder = "Folder name"
    static let alertCreate = "Create"
    static let alertCancel = "Cancel"
    static let errorAlertTitle = "Ошибка"
    static let deleteBookmarkTitle = "Delete link?"
    static let deleteBookmarkMessage = "This link will be permanently deleted."
    static let deleteFolderTitle = "Delete folder?"
    static let deleteFolderMessage = "The folder and all its links will be permanently deleted."
    static let renameFolderTitle = "Rename folder"
    static let renameFolderMessage = "Links in this folder may no longer match the new name."
    static let deleteConfirm = "Delete"
    static let renameConfirm = "Rename"
    static let loaderText = "Анализирую ссылку..."
    static let bottomBarHint = "Type Here"
    static let librarySymbol = "document.on.document"
    static let searchSymbol = "magnifyingglass"
    static let microphoneSymbol = "microphone.fill"
    static let microphoneActiveSymbol = "waveform"
    static let sendSymbol = "paperplane.fill"
}

// MARK: - Preview

#Preview {
    HomeView()
}
