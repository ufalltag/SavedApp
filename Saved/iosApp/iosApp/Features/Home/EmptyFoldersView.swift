import SwiftUI

struct EmptyFoldersView: View {

    let onCreateFolder: () -> Void

    var body: some View {
        content
    }
}

// MARK: - Content

private extension EmptyFoldersView {

    var content: some View {
        VStack(spacing: .zero) {
            Spacer()
            iconView
            titleView
                .padding(.top, .titleTopPadding)
            subtitleView
                .padding(.top, .subtitleTopPadding)
            Spacer()
            createButton
        }
        .padding(.horizontal, .horizontalPadding)
        .padding(.bottom, .bottomPadding)
    }

    var iconView: some View {
        ZStack {
            Circle()
                .fill(Color.accentColor.opacity(.iconBackgroundOpacity))
                .frame(width: .iconContainerSize, height: .iconContainerSize)
            Image(systemName: String.folderFillSymbol)
                .font(.system(size: .iconSize, weight: .medium))
                .foregroundStyle(Color.accentColor)
        }
    }

    var titleView: some View {
        Text(String.emptyTitle)
            .font(.title2.bold())
            .foregroundStyle(.primary)
            .multilineTextAlignment(.center)
    }

    var subtitleView: some View {
        Text(String.emptySubtitle)
            .font(.subheadline)
            .foregroundStyle(.secondary)
            .multilineTextAlignment(.center)
            .lineSpacing(.subtitleLineSpacing)
    }

    var createButton: some View {
        Button(action: onCreateFolder) {
            createButtonContent
        }
        .buttonStyle(.plain)
    }

    var createButtonContent: some View {
        HStack(spacing: .buttonSpacing) {
            Image(systemName: String.plusSymbol)
                .fontWeight(.semibold)
            Text(String.createButtonTitle)
                .fontWeight(.semibold)
        }
        .font(.body)
        .foregroundStyle(.white)
        .frame(maxWidth: .infinity)
        .padding(.vertical, .buttonVerticalPadding)
        .background(Color.accentColor, in: RoundedRectangle(cornerRadius: .buttonCornerRadius))
    }
}

// MARK: - Constants

private extension CGFloat {

    static let horizontalPadding: CGFloat = 24
    static let bottomPadding: CGFloat = 120
    static let iconContainerSize: CGFloat = 96
    static let iconSize: CGFloat = 40
    static let titleTopPadding: CGFloat = 24
    static let subtitleTopPadding: CGFloat = 10
    static let subtitleLineSpacing: CGFloat = 4
    static let buttonSpacing: CGFloat = 8
    static let buttonVerticalPadding: CGFloat = 16
    static let buttonCornerRadius: CGFloat = 16
}

private extension Double {

    static let iconBackgroundOpacity: Double = 0.12
}

private extension String {

    static let folderFillSymbol = "folder.fill"
    static let plusSymbol = "plus"
    static let emptyTitle = "No folders yet"
    static let emptySubtitle = "Add a folder to keep your\nimportant links organized"
    static let createButtonTitle = "Create folder"
}

// MARK: - Preview

#Preview {
    EmptyFoldersView(onCreateFolder: {})
        .background(Color(.systemGroupedBackground))
}
