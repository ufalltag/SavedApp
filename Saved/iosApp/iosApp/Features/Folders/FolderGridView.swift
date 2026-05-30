import SwiftUI

struct FolderGridView: View {

    let folders: [FolderItem]
    var onCreateFolder: () -> Void = {}
    var onSeeAll: () -> Void = {}
    var onFolderTap: (FolderItem) -> Void = { _ in }
    var onRenameFolder: (FolderItem) -> Void = { _ in }
    var onDeleteFolder: (FolderItem) -> Void = { _ in }

    var body: some View {
        content
    }
}

// MARK: - Content

private extension FolderGridView {

    var columns: [GridItem] {
        Array(repeating: GridItem(.flexible(), spacing: .columnSpacing), count: .columnCount)
    }

    var content: some View {
        VStack(spacing: .sectionSpacing) {
            SectionHeaderView(title: String.sectionTitle, onSeeAll: onSeeAll)
            LazyVGrid(columns: columns, spacing: .rowSpacing) {
                AddFolderCellView(onTap: onCreateFolder)
                ForEach(folders.prefix(.maxItems)) { folder in
                    FolderGridCellView(
                        folder: folder,
                        onTap: { onFolderTap(folder) },
                        onRename: { onRenameFolder(folder) },
                        onDelete: { onDeleteFolder(folder) }
                    )
                }
            }
        }
        .padding(.horizontal, .horizontalPadding)
    }
}

// MARK: - Constants

private extension CGFloat {

    static let horizontalPadding: CGFloat = 16
    static let columnSpacing: CGFloat = 8
    static let rowSpacing: CGFloat = 16
    static let sectionSpacing: CGFloat = 2
}

private extension String {

    static let sectionTitle = "My folders"
}

private extension Int {

    static let columnCount = 3
    static let maxItems = 6
}

// MARK: - Preview

#Preview {
    NavigationStack {
        FolderGridView(folders: [
            FolderItem(title: "All photo", linksCount: 345),
            FolderItem(title: "Max Wo", linksCount: 18),
            FolderItem(title: "Music 1", linksCount: 222),
            FolderItem(title: "Lernen", linksCount: 12),
            FolderItem(title: "Games", linksCount: 34),
            FolderItem(title: "Other", linksCount: 7)
        ])
        .navigationTitle("My Folders")
    }
}
