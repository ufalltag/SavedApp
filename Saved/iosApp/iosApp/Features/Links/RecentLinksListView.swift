import SwiftUI

struct RecentLinksListView: View {

    let links: [LinkItem]
    var onTap: (LinkItem) -> Void = { _ in }
    var onMove: (LinkItem) -> Void = { _ in }
    var onDelete: (LinkItem) -> Void = { _ in }

    var body: some View {
        content
    }
}

// MARK: - Content

private extension RecentLinksListView {

    var content: some View {
        VStack(spacing: .sectionSpacing) {
            SectionHeaderView(title: String.sectionTitle)
            linksList
        }
        .padding(.horizontal, .horizontalPadding)
    }

    @ViewBuilder
    var linksList: some View {
        if links.isEmpty {
            EmptyLinksView()
        } else {
            VStack(spacing: .rowSpacing) {
                ForEach(links.prefix(.maxItems)) { link in
                    RecentLinkCellView(
                        link: link,
                        onTap: { onTap(link) },
                        onMove: { onMove(link) },
                        onDelete: { onDelete(link) }
                    )
                }
            }
        }
    }
}

// MARK: - Constants

private extension CGFloat {

    static let horizontalPadding: CGFloat = 16
    static let sectionSpacing: CGFloat = 12
    static let rowSpacing: CGFloat = 8
}

private extension Int {

    static let maxItems = 5
}

private extension String {

    static let sectionTitle = "Last links"
}

// MARK: - Preview

#Preview {
    NavigationStack {
        ScrollView {
            RecentLinksListView(links: [
                LinkItem(title: "SwiftUI Documentation", url: "developer.apple.com", date: .now),
                LinkItem(title: "GitHub — apple/swift", url: "github.com", date: .now),
                LinkItem(title: "Hacking with Swift", url: "hackingwithswift.com", date: .now)
            ])
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle("My Folders")
    }
}
