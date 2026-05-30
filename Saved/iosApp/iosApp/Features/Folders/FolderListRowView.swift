import SwiftUI
import Shared

struct FolderListRowView: View {

    let folder: Folder

    var body: some View {
        content
    }
}

// MARK: - Content

private extension FolderListRowView {

    var content: some View {
        HStack(spacing: .contentSpacing) {
            folderIcon
            infoView
            Spacer()
            chevron
        }
        .padding(.vertical, .verticalPadding)
    }

    var folderIcon: some View {
        ZStack {
            RoundedRectangle(cornerRadius: .iconCornerRadius)
                .fill(Color.accentColor.opacity(.iconBackgroundOpacity))
                .frame(width: .iconSize, height: .iconSize)
            Image(.folder)
                .resizable()
                .scaledToFit()
                .frame(width: .folderImageSize, height: .folderImageSize)
        }
    }

    var infoView: some View {
        VStack(alignment: .leading, spacing: .infoSpacing) {
            Text(folder.name)
                .font(.body)
                .fontWeight(.semibold)
                .foregroundStyle(.primary)
                .lineLimit(.nameLineLimit)
            Text("\(folder.bookmarksCount) \(String.linksLabel)")
                .font(.subheadline)
                .foregroundStyle(.secondary)
        }
    }

    var chevron: some View {
        Image(systemName: .chevronSymbol)
            .font(.caption)
            .fontWeight(.semibold)
            .foregroundStyle(.tertiary)
    }
}

// MARK: - Constants

private extension CGFloat {

    static let contentSpacing: CGFloat = 14
    static let verticalPadding: CGFloat = 4
    static let iconSize: CGFloat = 48
    static let iconCornerRadius: CGFloat = 10
    static let folderImageSize: CGFloat = 30
    static let infoSpacing: CGFloat = 2
}

private extension Double {

    static let iconBackgroundOpacity: Double = 0.12
}

private extension Int {

    static let nameLineLimit = 1
}

private extension String {

    static let linksLabel = "links"
    static let chevronSymbol = "chevron.right"
}

// MARK: - Preview

#Preview {
    List {
        FolderListRowView(folder: Folder(id: "1", name: "Design Resources", bookmarksCount: 42))
        FolderListRowView(folder: Folder(id: "2", name: "Swift Articles", bookmarksCount: 7))
    }
    .listStyle(.insetGrouped)
}
