import SwiftUI

struct EmptyLinksView: View {

    var body: some View {
        content
    }
}

// MARK: - Content

private extension EmptyLinksView {

    var content: some View {
        VStack(spacing: .contentSpacing) {
            iconView
            titleView
            subtitleView
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, .verticalPadding)
        .background(Color(.secondarySystemGroupedBackground), in: RoundedRectangle(cornerRadius: .cornerRadius))
    }

    var iconView: some View {
        ZStack {
            RoundedRectangle(cornerRadius: .iconCornerRadius)
                .fill(Color.accentColor.opacity(.iconBackgroundOpacity))
                .frame(width: .iconContainerSize, height: .iconContainerSize)
            Image(systemName: String.linkSymbol)
                .font(.system(size: .symbolSize, weight: .medium))
                .foregroundStyle(Color.accentColor)
        }
    }

    var titleView: some View {
        Text(String.emptyTitle)
            .font(.subheadline.bold())
            .foregroundStyle(.primary)
    }

    var subtitleView: some View {
        Text(String.emptySubtitle)
            .font(.caption)
            .foregroundStyle(.secondary)
            .multilineTextAlignment(.center)
    }
}

// MARK: - Constants

private extension CGFloat {

    static let contentSpacing: CGFloat = 8
    static let verticalPadding: CGFloat = 28
    static let cornerRadius: CGFloat = 16
    static let iconContainerSize: CGFloat = 52
    static let iconCornerRadius: CGFloat = 12
    static let symbolSize: CGFloat = 22
}

private extension Double {

    static let iconBackgroundOpacity: Double = 0.12
}

private extension String {

    static let linkSymbol = "link"
    static let emptyTitle = "No recent links"
    static let emptySubtitle = "Your saved links will appear here"
}

// MARK: - Preview

#Preview {
    EmptyLinksView()
        .padding(.horizontal, 16)
        .background(Color(.systemGroupedBackground))
}
