import SwiftUI
import Shared

struct AllFoldersView: View {

    @State private var vm = AllFoldersViewModelWrapper()
    @State private var errorMessage: String?
    @State private var folderLinksFolderId: String = ""
    @State private var folderLinksFolderName: String = ""
    @State private var showFolderLinks = false

    var body: some View {
        content
            .trackScreen("all_folders")
            .task { vm.collectSideEffects(onShowError: { errorMessage = $0 }) }
            .alert(String.errorAlertTitle, isPresented: Binding(
                get: { errorMessage != nil },
                set: { if !$0 { errorMessage = nil } }
            )) {
                Button(String.alertCancel, role: .cancel) { errorMessage = nil }
            } message: {
                Text(errorMessage ?? "")
            }
    }
}

// MARK: - Content

private extension AllFoldersView {

    var content: some View {
        Group {
            if vm.isLoading {
                loadingView
            } else {
                folderList
            }
        }
        .navigationTitle(String.navigationTitle)
        .navigationBarTitleDisplayMode(.large)
        .background(Color(.systemGroupedBackground))
        .navigationDestination(isPresented: $showFolderLinks) {
            FolderLinksView(folderId: folderLinksFolderId, folderName: folderLinksFolderName)
        }
    }

    var loadingView: some View {
        ProgressView()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
    }

    var folderList: some View {
        List {
            ForEach(Array(vm.folders.enumerated()), id: \.element.id) { index, folder in
                Button {
                    folderLinksFolderId = folder.id
                    folderLinksFolderName = folder.name
                    showFolderLinks = true
                } label: {
                    FolderListRowView(folder: folder)
                }
                .buttonStyle(.plain)
                .listRowInsets(EdgeInsets(
                    top: .rowVerticalInset,
                    leading: .rowHorizontalInset,
                    bottom: .rowVerticalInset,
                    trailing: .rowHorizontalInset
                ))
                .onAppear {
                    triggerLoadMoreIfNeeded(currentIndex: index)
                }
            }

            if vm.isLoadingMore {
                loadingMoreRow
            }
        }
        .listStyle(.insetGrouped)
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
        let threshold = vm.folders.count - .paginationThreshold
        guard currentIndex >= threshold, vm.hasMore, !vm.isLoadingMore else { return }
        vm.loadMore()
    }
}

// MARK: - Constants

private extension CGFloat {

    static let rowVerticalInset: CGFloat = 6
    static let rowHorizontalInset: CGFloat = 16
    static let loadingMorePadding: CGFloat = 8
}

private extension Int {

    static let paginationThreshold = 5
}

private extension String {

    static let navigationTitle = "My Folders"
    static let errorAlertTitle = "Error"
    static let alertCancel = "Cancel"
}

// MARK: - Preview

#Preview {
    NavigationStack {
        AllFoldersView()
    }
}
