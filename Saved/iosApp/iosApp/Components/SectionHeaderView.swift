import SwiftUI

struct SectionHeaderView: View {

    let title: String
    var onSeeAll: (() -> Void)? = nil

    var body: some View {
        HStack {
            Text(title)
                .font(.title2.bold())
                .foregroundStyle(.primary)
            Spacer()
            if let onSeeAll {
                Button(action: onSeeAll) {
                    HStack(spacing: .seeAllSpacing) {
                        Text(String.seeAll)
                            .font(.subheadline)
                        Image(systemName: .chevronSymbol)
                            .font(.caption)
                            .fontWeight(.semibold)
                    }
                    .foregroundStyle(.primary)
                }
            }
        }
    }
}

// MARK: - Constants

private extension CGFloat {
    static let seeAllSpacing: CGFloat = 2
}

private extension String {
    static let seeAll = "See all"
    static let chevronSymbol = "chevron.right"
}

// MARK: - Preview

#Preview {
    SectionHeaderView(title: "My Folders")
        .padding(.horizontal, 16)
}
