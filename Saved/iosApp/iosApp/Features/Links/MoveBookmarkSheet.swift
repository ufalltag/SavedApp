import SwiftUI
import Shared

struct MoveBookmarkSheet: View {

    let bookmark: Bookmark
    let folders: [Folder]
    let onSelectFolder: (String) -> Void
    let onDismiss: () -> Void

    private var sourceFolderName: String {
        folders.first { $0.id == bookmark.folderId }?.name ?? ""
    }

    var body: some View {
        VStack(spacing: 0) {
            handle
            VStack(spacing: .contentSpacing) {
                header
                fromRow
                folderList
            }
            .padding(.horizontal, .horizontalPadding)
            .padding(.bottom, .bottomPadding)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
    }
}

// MARK: - Subviews

private extension MoveBookmarkSheet {

    var handle: some View {
        Capsule()
            .fill(Color.secondary.opacity(.handleOpacity))
            .frame(width: .handleWidth, height: .handleHeight)
            .padding(.top, .handleTopPadding)
    }

    var header: some View {
        VStack(spacing: .headerTextSpacing) {
            Text(String.sheetTitle)
                .font(.headline)
            Text(bookmark.title)
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .lineLimit(.titleLineLimit)
                .multilineTextAlignment(.center)
        }
        .padding(.top, .headerTopPadding)
    }

    var fromRow: some View {
        HStack {
            Image(systemName: String.folderSymbol)
                .foregroundStyle(.secondary)
            Text(String.fromLabel)
                .font(.subheadline)
                .foregroundStyle(.secondary)
            Text(sourceFolderName)
                .font(.subheadline.weight(.semibold))
            Spacer()
        }
        .padding(.vertical, .fromRowVerticalPadding)
        .padding(.horizontal, .fromRowHorizontalPadding)
        .background(Color.secondary.opacity(.fromRowBackgroundOpacity), in: RoundedRectangle(cornerRadius: .fromRowCornerRadius))
    }

    var folderList: some View {
        VStack(spacing: .folderRowSpacing) {
            ForEach(folders, id: \.id) { folder in
                let isCurrent = folder.id == bookmark.folderId
                Button {
                    guard !isCurrent else { return }
                    onSelectFolder(folder.id)
                } label: {
                    HStack {
                        Image(systemName: String.folderSymbol)
                            .foregroundStyle(isCurrent ? .secondary : Color.accentColor)
                        Text(folder.name)
                            .font(.body)
                            .foregroundStyle(isCurrent ? .secondary : .primary)
                        Spacer()
                        if isCurrent {
                            Image(systemName: String.checkmarkSymbol)
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                        }
                    }
                    .padding(.vertical, .folderRowVerticalPadding)
                    .padding(.horizontal, .folderRowHorizontalPadding)
                    .background(Color.secondary.opacity(isCurrent ? .currentFolderOpacity : .folderRowOpacity), in: RoundedRectangle(cornerRadius: .folderRowCornerRadius))
                }
                .disabled(isCurrent)
            }
        }
    }
}

// MARK: - Constants

private extension CGFloat {
    static let contentSpacing: CGFloat = 16
    static let horizontalPadding: CGFloat = 20
    static let bottomPadding: CGFloat = 32
    static let handleWidth: CGFloat = 36
    static let handleHeight: CGFloat = 4
    static let handleTopPadding: CGFloat = 12
    static let headerTopPadding: CGFloat = 4
    static let headerTextSpacing: CGFloat = 4
    static let fromRowVerticalPadding: CGFloat = 12
    static let fromRowHorizontalPadding: CGFloat = 14
    static let fromRowCornerRadius: CGFloat = 12
    static let folderRowSpacing: CGFloat = 8
    static let folderRowVerticalPadding: CGFloat = 12
    static let folderRowHorizontalPadding: CGFloat = 14
    static let folderRowCornerRadius: CGFloat = 12
}

private extension Double {
    static let handleOpacity: Double = 0.3
    static let fromRowBackgroundOpacity: Double = 0.08
    static let folderRowOpacity: Double = 0.06
    static let currentFolderOpacity: Double = 0.04
}

private extension Int {
    static let titleLineLimit = 2
}

private extension String {
    static let sheetTitle = "Move to folder"
    static let fromLabel = "From:"
    static let folderSymbol = "folder"
    static let checkmarkSymbol = "checkmark"
}
