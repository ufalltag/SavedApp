import SwiftUI
import Shared

struct SearchView: View {

    let searchResults: [Bookmark]
    let isSearching: Bool
    var onQueryChanged: (String) -> Void
    var onDismiss: () -> Void

    @State private var query: String = ""
    @FocusState private var isFocused: Bool
    @State private var safariURL: SafariURL?

    var body: some View {
        VStack(spacing: 0) {
            searchHeader
            resultsContent
        }
        .trackScreen("search")
        .background(Color(.systemGroupedBackground).ignoresSafeArea())
        .onAppear { isFocused = true }
        .sheet(item: $safariURL) { item in
            SafariView(url: item.url)
                .ignoresSafeArea()
        }
    }
}

// MARK: - Content

private extension SearchView {

    var searchHeader: some View {
        HStack(spacing: .headerSpacing) {
            HStack(spacing: .fieldSpacing) {
                Image(systemName: String.searchSymbol)
                    .foregroundStyle(.secondary)
                TextField(String.searchPlaceholder, text: $query)
                    .focused($isFocused)
                    .submitLabel(.search)
                    .onChange(of: query) { _, newValue in
                        onQueryChanged(newValue)
                    }
                if !query.isEmpty {
                    Button { clearQuery() } label: {
                        Image(systemName: String.clearSymbol)
                            .foregroundStyle(.secondary)
                    }
                }
            }
            .padding(.horizontal, .fieldHPadding)
            .padding(.vertical, .fieldVPadding)
            .background(.bar, in: RoundedRectangle(cornerRadius: .fieldCornerRadius))

            Button(String.cancelButton) {
                onDismiss()
            }
            .foregroundStyle(.primary)
        }
        .padding(.horizontal, .headerHPadding)
        .padding(.top, .headerTopPadding)
        .padding(.bottom, .headerBottomPadding)
        .background(.bar)
    }

    @ViewBuilder
    var resultsContent: some View {
        if isSearching {
            loadingView
        } else if !query.trimmingCharacters(in: .whitespaces).isEmpty && searchResults.isEmpty {
            emptyView
        } else {
            resultsList
        }
    }

    var loadingView: some View {
        VStack {
            Spacer()
            ProgressView()
                .scaleEffect(.loaderScale)
            Spacer()
        }
    }

    var emptyView: some View {
        VStack(spacing: .emptySpacing) {
            Spacer()
            Image(systemName: String.emptySymbol)
                .font(.system(size: .emptyIconSize))
                .foregroundStyle(.tertiary)
            Text(String.emptyTitle)
                .font(.headline)
                .foregroundStyle(.secondary)
            Text("\"\(query)\"")
                .font(.subheadline)
                .foregroundStyle(.tertiary)
            Spacer()
        }
    }

    var resultsList: some View {
        ScrollView {
            LazyVStack(spacing: .rowSpacing) {
                ForEach(searchResults, id: \.id) { bookmark in
                    Button {
                        if let url = URL(string: bookmark.url) {
                            safariURL = SafariURL(url: url)
                        }
                    } label: {
                        resultRow(bookmark)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.top, .listTopPadding)
            .padding(.horizontal, .listHPadding)
            .padding(.bottom, .listBottomPadding)
        }
        .scrollDismissesKeyboard(.interactively)
    }

    func resultRow(_ bookmark: Bookmark) -> some View {
        HStack(spacing: .rowContentSpacing) {
            ZStack {
                RoundedRectangle(cornerRadius: .iconCornerRadius)
                    .fill(Color.accentColor.opacity(.iconOpacity))
                    .frame(width: .iconSize, height: .iconSize)
                Image(systemName: String.linkSymbol)
                    .font(.system(size: .iconSymbolSize, weight: .medium))
                    .foregroundStyle(Color.accentColor)
            }
            VStack(alignment: .leading, spacing: .rowTextSpacing) {
                Text(bookmark.title)
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .foregroundStyle(.primary)
                    .lineLimit(1)
                Text(bookmark.url)
                    .font(.caption)
                    .foregroundStyle(.secondary)
                    .lineLimit(1)
            }
            Spacer()
            Image(systemName: String.chevronSymbol)
                .font(.caption)
                .foregroundStyle(.tertiary)
        }
        .padding(.rowPadding)
        .background(.background, in: RoundedRectangle(cornerRadius: .rowCornerRadius))
    }

    func clearQuery() {
        query = ""
        onQueryChanged("")
        isFocused = true
    }
}

// MARK: - Constants

private extension CGFloat {

    static let headerSpacing: CGFloat = 8
    static let headerHPadding: CGFloat = 16
    static let headerTopPadding: CGFloat = 12
    static let headerBottomPadding: CGFloat = 8
    static let fieldSpacing: CGFloat = 8
    static let fieldHPadding: CGFloat = 10
    static let fieldVPadding: CGFloat = 9
    static let fieldCornerRadius: CGFloat = 12
    static let loaderScale: CGFloat = 1.3
    static let emptySpacing: CGFloat = 12
    static let emptyIconSize: CGFloat = 48
    static let iconSize: CGFloat = 44
    static let iconCornerRadius: CGFloat = 10
    static let iconSymbolSize: CGFloat = 18
    static let rowContentSpacing: CGFloat = 12
    static let rowTextSpacing: CGFloat = 2
    static let rowPadding: CGFloat = 12
    static let rowCornerRadius: CGFloat = 14
    static let rowSpacing: CGFloat = 8
    static let listTopPadding: CGFloat = 12
    static let listHPadding: CGFloat = 16
    static let listBottomPadding: CGFloat = 24
}

private extension Double {

    static let iconOpacity: Double = 0.12
}

private extension String {

    static let cancelButton = "Cancel"
    static let searchPlaceholder = "Search bookmarks..."
    static let searchSymbol = "magnifyingglass"
    static let clearSymbol = "xmark.circle.fill"
    static let linkSymbol = "link"
    static let chevronSymbol = "chevron.right"
    static let emptySymbol = "magnifyingglass"
    static let emptyTitle = "No results found"
}

// MARK: - Preview

#Preview {
    SearchView(
        searchResults: [],
        isSearching: false,
        onQueryChanged: { _ in },
        onDismiss: {}
    )
}
