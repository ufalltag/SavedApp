import SwiftUI
import Shared

struct BookmarksView: View {

    @State private var wrapper = BookmarksViewModelWrapper()

    // URL analyze input
    @State private var urlInput = ""

    // Toast side-effect
    @State private var toastMessage: String?

    // Create folder alert
    @State private var showCreateFolderAlert = false
    @State private var newFolderName = ""

    // Rename folder alert
    @State private var folderToRenameId: String?
    @State private var renameFolderText = ""

    // Rename bookmark alert
    @State private var bookmarkToRenameId: String?
    @State private var renameBookmarkText = ""

    var body: some View {
        ZStack {
            NavigationView {
                VStack(spacing: 0) {
                    analyzeSection
                    Divider()
                    foldersSection
                    Divider()
                    bookmarksSection
                }
                .navigationTitle("Saved")
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button {
                            newFolderName = ""
                            showCreateFolderAlert = true
                        } label: {
                            Image(systemName: "folder.badge.plus")
                        }
                    }
                }
            }
            .navigationViewStyle(.stack)

            // Full-screen loader while AI is analyzing
            if wrapper.isAnalyzing {
                Color.black.opacity(0.45)
                    .ignoresSafeArea()

                VStack(spacing: 16) {
                    ProgressView()
                        .scaleEffect(1.5)
                        .tint(.white)
                    Text("AI is analyzing…")
                        .foregroundColor(.white)
                        .font(.subheadline.weight(.medium))
                }
                .padding(32)
                .background(.ultraThinMaterial)
                .clipShape(RoundedRectangle(cornerRadius: 20))
            }
        }
        .trackScreen("bookmarks")
        .onAppear {
            wrapper.collectSideEffects(
                onShowToast: { toastMessage = $0 },
                onOpenUrl: { urlString in
                    guard let url = URL(string: urlString) else { return }
                    UIApplication.shared.open(url)
                }
            )
        }
        // Create folder
        .alert("New Folder", isPresented: $showCreateFolderAlert) {
            TextField("Folder name", text: $newFolderName)
            Button("Create") { wrapper.createFolder(newFolderName) }
            Button("Cancel", role: .cancel) {}
        }
        // Rename folder
        .alert("Rename Folder", isPresented: Binding(
            get: { folderToRenameId != nil },
            set: { if !$0 { folderToRenameId = nil } }
        )) {
            TextField("Folder name", text: $renameFolderText)
            Button("Save") {
                if let id = folderToRenameId { wrapper.renameFolder(id, renameFolderText) }
                folderToRenameId = nil
            }
            Button("Cancel", role: .cancel) { folderToRenameId = nil }
        }
        // Rename bookmark
        .alert("Rename Bookmark", isPresented: Binding(
            get: { bookmarkToRenameId != nil },
            set: { if !$0 { bookmarkToRenameId = nil } }
        )) {
            TextField("Title", text: $renameBookmarkText)
            Button("Save") {
                if let id = bookmarkToRenameId { wrapper.renameBookmark(id, renameBookmarkText) }
                bookmarkToRenameId = nil
            }
            Button("Cancel", role: .cancel) { bookmarkToRenameId = nil }
        }
        // Toast notification
        .alert("Notice", isPresented: Binding(
            get: { toastMessage != nil },
            set: { if !$0 { toastMessage = nil } }
        )) {
            Button("OK") { toastMessage = nil }
        } message: {
            Text(toastMessage ?? "")
        }
    }

    // MARK: - Analyze section

    private var analyzeSection: some View {
        HStack(spacing: 10) {
            TextField("Paste a link…", text: $urlInput)
                .textFieldStyle(.roundedBorder)
                .keyboardType(.URL)
                .autocapitalization(.none)
                .autocorrectionDisabled()

            Button {
                let trimmed = urlInput.trimmingCharacters(in: .whitespaces)
                wrapper.analyzeAndSaveUrl(trimmed)
                urlInput = ""
            } label: {
                Text("Analyze")
                    .frame(width: 64)
            }
            .buttonStyle(.borderedProminent)
            .disabled(wrapper.isAnalyzing || urlInput.trimmingCharacters(in: .whitespaces).isEmpty)
        }
        .padding(.horizontal)
        .padding(.vertical, 12)
    }

    // MARK: - Folders section

    private var foldersSection: some View {
        Group {
            if wrapper.isFoldersLoading {
                ProgressView()
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 20)
            } else if wrapper.folders.isEmpty {
                Text("No folders yet — create one")
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 20)
            } else {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(wrapper.folders, id: \.id) { folder in
                            folderChip(folder: folder)
                        }
                    }
                    .padding(.horizontal)
                    .padding(.vertical, 10)
                }
            }
        }
        .background(Color(.secondarySystemBackground))
    }

    @ViewBuilder
    private func folderChip(folder: Folder) -> some View {
        let isSelected = wrapper.selectedFolderId == folder.id

        Button { wrapper.selectFolder(folder.id) } label: {
            Text(folder.name)
                .font(.subheadline.weight(isSelected ? .semibold : .regular))
                .padding(.horizontal, 14)
                .padding(.vertical, 7)
                .background(isSelected ? Color.accentColor : Color(.tertiarySystemBackground))
                .foregroundColor(isSelected ? .white : .primary)
                .clipShape(Capsule())
        }
        .buttonStyle(.plain)
        .contextMenu {
            Button {
                renameFolderText = folder.name
                folderToRenameId = folder.id
            } label: {
                Label("Rename", systemImage: "pencil")
            }

            Button(role: .destructive) {
                wrapper.deleteFolder(folder.id)
            } label: {
                Label("Delete", systemImage: "trash")
            }
        }
    }

    // MARK: - Bookmarks section

    @ViewBuilder
    private var bookmarksSection: some View {
        if wrapper.isBookmarksLoading {
            ProgressView()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if wrapper.bookmarks.isEmpty {
            VStack(spacing: 12) {
                Image(systemName: "bookmark.slash")
                    .font(.system(size: 44))
                    .foregroundColor(.secondary)
                Text("No bookmarks in this folder")
                    .foregroundColor(.secondary)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else {
            List {
                ForEach(wrapper.bookmarks, id: \.id) { bookmark in
                    bookmarkRow(bookmark: bookmark)
                }
            }
            .listStyle(.plain)
        }
    }

    @ViewBuilder
    private func bookmarkRow(bookmark: Bookmark) -> some View {
        Button {
            wrapper.openBookmark(bookmark.url)
        } label: {
            VStack(alignment: .leading, spacing: 4) {
                Text(bookmark.title)
                    .font(.body)
                    .foregroundColor(.primary)
                Text(bookmark.url)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(1)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .contextMenu {
            Button {
                renameBookmarkText = bookmark.title
                bookmarkToRenameId = bookmark.id
            } label: {
                Label("Rename", systemImage: "pencil")
            }

            Button(role: .destructive) {
                wrapper.deleteBookmark(bookmark.id)
            } label: {
                Label("Delete", systemImage: "trash")
            }
        }
    }
}
