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
                .navigationTitle("Сохранённое")
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
                    Text("Нейросеть анализирует…")
                        .foregroundColor(.white)
                        .font(.subheadline.weight(.medium))
                }
                .padding(32)
                .background(.ultraThinMaterial)
                .clipShape(RoundedRectangle(cornerRadius: 20))
            }
        }
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
        .alert("Новая папка", isPresented: $showCreateFolderAlert) {
            TextField("Название папки", text: $newFolderName)
            Button("Создать") { wrapper.createFolder(newFolderName) }
            Button("Отмена", role: .cancel) {}
        }
        // Rename folder
        .alert("Переименовать папку", isPresented: Binding(
            get: { folderToRenameId != nil },
            set: { if !$0 { folderToRenameId = nil } }
        )) {
            TextField("Название папки", text: $renameFolderText)
            Button("Сохранить") {
                if let id = folderToRenameId { wrapper.renameFolder(id, renameFolderText) }
                folderToRenameId = nil
            }
            Button("Отмена", role: .cancel) { folderToRenameId = nil }
        }
        // Rename bookmark
        .alert("Переименовать закладку", isPresented: Binding(
            get: { bookmarkToRenameId != nil },
            set: { if !$0 { bookmarkToRenameId = nil } }
        )) {
            TextField("Название", text: $renameBookmarkText)
            Button("Сохранить") {
                if let id = bookmarkToRenameId { wrapper.renameBookmark(id, renameBookmarkText) }
                bookmarkToRenameId = nil
            }
            Button("Отмена", role: .cancel) { bookmarkToRenameId = nil }
        }
        // Toast notification
        .alert("Уведомление", isPresented: Binding(
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
            TextField("Вставьте ссылку…", text: $urlInput)
                .textFieldStyle(.roundedBorder)
                .keyboardType(.URL)
                .autocapitalization(.none)
                .autocorrectionDisabled()

            Button {
                let trimmed = urlInput.trimmingCharacters(in: .whitespaces)
                wrapper.analyzeAndSaveUrl(trimmed)
                urlInput = ""
            } label: {
                Text("Анализ")
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
                Text("Нет папок — создайте первую")
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
                Label("Переименовать", systemImage: "pencil")
            }

            Button(role: .destructive) {
                wrapper.deleteFolder(folder.id)
            } label: {
                Label("Удалить", systemImage: "trash")
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
                Text("Нет закладок в этой папке")
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
                Label("Переименовать", systemImage: "pencil")
            }

            Button(role: .destructive) {
                wrapper.deleteBookmark(bookmark.id)
            } label: {
                Label("Удалить", systemImage: "trash")
            }
        }
    }
}
