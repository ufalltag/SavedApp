import SwiftUI

struct FolderGridCellView: View {

    let folder: FolderItem
    var onTap: () -> Void = {}
    var onRename: () -> Void = {}
    var onDelete: () -> Void = {}

    var body: some View {
        content
    }
}

// MARK: - Content

private extension FolderGridCellView {

    var content: some View {
        VStack(spacing: .contentSpacing) {
            folderImage
            titleView
            linksCountView
        }
        .onTapGesture { onTap() }
        .contextMenu {
            Button {
                onRename()
            } label: {
                Label(String.menuRename, systemImage: String.menuRenameSymbol)
            }
            Button(role: .destructive) {
                onDelete()
            } label: {
                Label(String.menuDelete, systemImage: String.menuDeleteSymbol)
            }
        }
    }

    var folderImage: some View {
        Image(.folder)
            .resizable()
            .scaledToFit()
            .frame(height: .imageHeight)
    }

    var titleView: some View {
        Text(folder.title)
            .font(.subheadline)
            .fontWeight(.semibold)
            .foregroundStyle(.primary)
            .multilineTextAlignment(.center)
            .lineLimit(.titleLineLimit)
    }

    var linksCountView: some View {
        Text("\(folder.linksCount) \(String.linksLabel)")
            .font(.caption)
            .foregroundStyle(.secondary)
    }
}

// MARK: - Constants

private extension CGFloat {

    static let imageHeight: CGFloat = 120
    static let contentSpacing: CGFloat = 2
}

private extension Int {

    static let titleLineLimit = 2
}

private extension String {

    static let linksLabel = "links"
    static let menuRename = "Rename"
    static let menuDelete = "Delete"
    static let menuRenameSymbol = "pencil"
    static let menuDeleteSymbol = "trash"
}

// MARK: - Preview

#Preview {
    FolderGridCellView(folder: FolderItem(title: "Design", linksCount: 12))
        .padding()
}
