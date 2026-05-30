import SwiftUI

struct AddFolderCellView: View {

    var onTap: () -> Void = {}

    var body: some View {
        Button(action: onTap) {
            VStack(spacing: .contentSpacing) {
                cardView
                titleView
            }
            .frame(maxWidth: .infinity)
        }
        .buttonStyle(.plain)
    }
}

// MARK: - Content

private extension AddFolderCellView {

    var cardView: some View {
        RoundedRectangle(cornerRadius: .cornerRadius)
            .fill(Color(.secondarySystemGroupedBackground))
            .frame(width: .cardSize, height: .cardSize)
            .shadow(
                color: .black.opacity(0.10),
                radius: 8,
                x: 0,
                y: 4
            )
            .padding(.top, .cardTopPadding)
            .overlay {
                Image(systemName: .plusSymbol)
                    .font(.title)
                    .fontWeight(.medium)
                    .foregroundStyle(Color.accentColor)
                    .offset(y: .plusOffset)
            }
    }
    var titleView: some View {
        Text(String.title)
            .font(.subheadline)
            .fontWeight(.semibold)
            .foregroundStyle(.primary)
    }
}

// MARK: - Constants

private extension CGFloat {

    static let cardSize: CGFloat = 76
    static let cardTopPadding: CGFloat = 12
    static let cornerRadius: CGFloat = 16
    static let contentSpacing: CGFloat = 20
    static let plusOffset: CGFloat = 6
}

private extension String {

    static let title = "Add folder"
    static let plusSymbol = "plus"
}

// MARK: - Preview

#Preview {
    AddFolderCellView()
        .frame(width: 110)
        .padding()
}
