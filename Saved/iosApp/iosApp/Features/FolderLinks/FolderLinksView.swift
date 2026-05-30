import SwiftUI
import Shared

struct FolderLinksView: View {

    private let folderId: String
    private let folderName: String

    @State private var vm: FolderLinksViewModelWrapper
    @State private var errorMessage: String?

    init(folderId: String, folderName: String) {
        self.folderId = folderId
        self.folderName = folderName
        _vm = State(initialValue: FolderLinksViewModelWrapper(folderId: folderId, folderName: folderName))
    }

    var body: some View {
        content
            .task { vm.collectSideEffects(onShowError: { errorMessage = $0 }) }
            .alert(String.errorAlertTitle, isPresented: Binding(
                get: { errorMessage != nil },
                set: { if !$0 { errorMessage = nil } }
            )) {
                Button(String.alertCancel, role: .cancel) { errorMessage = nil }
            } message: {
                Text(errorMessage ?? "")
            }
            .alert(String.deleteTitle, isPresented: Binding(
                get: { vm.bookmarkPendingDelete != nil },
                set: { if !$0 { vm.dismissDeleteBookmark() } }
            )) {
                Button(String.deleteConfirm, role: .destructive) { vm.confirmDeleteBookmark() }
                Button(String.alertCancel, role: .cancel) { vm.dismissDeleteBookmark() }
            } message: {
                Text(String.deleteMessage)
            }
            .sheet(isPresented: Binding(
                get: { vm.bookmarkPendingMove != nil },
                set: { if !$0 { vm.dismissMoveBookmark() } }
            )) {
                if let bookmark = vm.bookmarkPendingMove {
                    MoveBookmarkSheet(
                        bookmark: bookmark,
                        folders: vm.folders,
                        onSelectFolder: { vm.confirmMoveBookmark(targetFolderId: $0) },
                        onDismiss: { vm.dismissMoveBookmark() }
                    )
                    .presentationDetents([.medium])
                    .presentationDragIndicator(.hidden)
                }
            }
    }
}

// MARK: - Content

private extension FolderLinksView {

    var content: some View {
        Group {
            if vm.isLoading {
                loadingView
            } else {
                linksList
            }
        }
        .navigationTitle(folderName)
        .navigationBarTitleDisplayMode(.large)
        .background(Color(.systemGroupedBackground))
    }

    var loadingView: some View {
        ProgressView()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    var linksList: some View {
        List {
            ForEach(Array(vm.bookmarks.enumerated()), id: \.element.id) { index, bookmark in
                let link = LinkItem(bookmarkId: bookmark.id, title: bookmark.title, url: bookmark.url)
                RecentLinkCellView(
                    link: link,
                    onMove: { vm.requestMoveBookmark(bookmark) },
                    onDelete: { vm.requestDeleteBookmark(bookmark) }
                )
                .listRowInsets(EdgeInsets(
                    top: .rowVerticalInset,
                    leading: .rowHorizontalInset,
                    bottom: .rowVerticalInset,
                    trailing: .rowHorizontalInset
                ))
                .listRowBackground(Color.clear)
                .listRowSeparator(.hidden)
                .onAppear {
                    triggerLoadMoreIfNeeded(currentIndex: index)
                }
            }

            if vm.isLoadingMore {
                loadingMoreRow
            }
        }
        .listStyle(.plain)
        .overlay {
            if vm.bookmarks.isEmpty {
                EmptyLinksView()
            }
        }
    }

    var loadingMoreRow: some View {
        HStack {
            Spacer()
            ProgressView()
                .padding(.vertical, .loadingMorePadding)
            Spacer()
        }
        .listRowBackground(Color.clear)
        .listRowSeparator(.hidden)
    }

    func triggerLoadMoreIfNeeded(currentIndex: Int) {
        let threshold = vm.bookmarks.count - .paginationThreshold
        guard currentIndex >= threshold, vm.hasMore, !vm.isLoadingMore else { return }
        vm.loadMore()
    }
}

// MARK: - Constants

private extension CGFloat {

    static let rowVerticalInset: CGFloat = 4
    static let rowHorizontalInset: CGFloat = 16
    static let loadingMorePadding: CGFloat = 8
}

private extension Int {

    static let paginationThreshold = 5
}

private extension String {

    static let errorAlertTitle = "Error"
    static let alertCancel = "Cancel"
    static let deleteTitle = "Delete link?"
    static let deleteMessage = "This link will be permanently deleted."
    static let deleteConfirm = "Delete"
}

// MARK: - Preview

#Preview {
    NavigationStack {
        FolderLinksView(folderId: "1", folderName: "Design Resources")
    }
}
