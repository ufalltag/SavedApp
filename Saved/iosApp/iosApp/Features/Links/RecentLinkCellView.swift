import SwiftUI

struct RecentLinkCellView: View {

    let link: LinkItem
    var onMove: () -> Void = {}
    var onDelete: () -> Void = {}

    var body: some View {
        content
    }
}

// MARK: - Content

private extension RecentLinkCellView {

    var content: some View {
        HStack(spacing: .contentSpacing) {
            linkIcon
            infoView
            Spacer()
            menuButton
        }
        .padding(.cellPadding)
        .background(.background, in: RoundedRectangle(cornerRadius: .cornerRadius))
    }

    var linkIcon: some View {
        ZStack {
            RoundedRectangle(cornerRadius: .iconCornerRadius)
                .fill(Color.accentColor.opacity(.iconBackgroundOpacity))
                .frame(width: .iconSize, height: .iconSize)
            Image(systemName: .linkSymbol)
                .font(.system(size: .symbolSize, weight: .medium))
                .foregroundStyle(Color.accentColor)
        }
    }

    var infoView: some View {
        VStack(alignment: .leading, spacing: .infoSpacing) {
            Text(link.title)
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundStyle(.primary)
                .lineLimit(.titleLineLimit)
            Text(link.url + .separator + link.date.formatted(.recentLink))
                .font(.caption)
                .foregroundStyle(.secondary)
                .lineLimit(.subtitleLineLimit)
        }
    }

    var menuButton: some View {
        Menu {
            Button {
                onMove()
            } label: {
                Label(String.menuMove, systemImage: String.menuMoveSymbol)
            }
            Button(role: .destructive) {
                onDelete()
            } label: {
                Label(String.menuDelete, systemImage: String.menuDeleteSymbol)
            }
        } label: {
            Image(systemName: .ellipsisSymbol)
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .padding(.menuButtonPadding)
        }
    }
}

// MARK: - Constants

private extension CGFloat {

    static let contentSpacing: CGFloat = 12
    static let cellPadding: CGFloat = 12
    static let cornerRadius: CGFloat = 16
    static let iconSize: CGFloat = 52
    static let iconCornerRadius: CGFloat = 12
    static let symbolSize: CGFloat = 20
    static let menuButtonPadding: CGFloat = 4
    static let infoSpacing: CGFloat = 2
}

private extension Double {

    static let iconBackgroundOpacity: Double = 0.12
}

private extension Int {

    static let titleLineLimit = 1
    static let subtitleLineLimit = 1
}

private extension String {

    static let linkSymbol = "link"
    static let ellipsisSymbol = "ellipsis"
    static let separator = " | "
    static let menuMove = "Move"
    static let menuDelete = "Delete"
    static let menuMoveSymbol = "folder"
    static let menuDeleteSymbol = "trash"
}

private extension FormatStyle where Self == Date.FormatStyle {

    static var recentLink: Date.FormatStyle {
        .dateTime.day().month(.twoDigits).year()
    }
}

// MARK: - Preview

#Preview {
    RecentLinkCellView(link: LinkItem(
        title: "SwiftUI Documentation",
        url: "developer.apple.com",
        date: .now
    ))
    .padding()
    .background(Color(.systemGroupedBackground))
}
