import SwiftUI

/// Шапка экранов авторизации: фирменная иконка + заголовок + подзаголовок.
struct AuthBrandHeader: View {

    let title: String
    let subtitle: String

    var body: some View {
        content
    }
}

// MARK: - Content

private extension AuthBrandHeader {

    var content: some View {
        VStack(spacing: .textSpacing) {
            logo
            Text(title)
                .font(.system(size: .titleFontSize, weight: .bold))
                .multilineTextAlignment(.center)
            Text(subtitle)
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
        .frame(maxWidth: .infinity)
    }

    var logo: some View {
        Image(.brandIcon)
            .resizable()
            .scaledToFit()
            .frame(width: .logoIconSize, height: .logoIconSize)
            .frame(width: .logoSize, height: .logoSize)
            .background(
                Color.accentColor.opacity(.logoBackgroundOpacity),
                in: RoundedRectangle(cornerRadius: .logoCornerRadius, style: .continuous)
            )
            .padding(.bottom, .logoBottomPadding)
    }
}

// MARK: - Constants

private extension CGFloat {

    static let textSpacing: CGFloat = 6
    static let titleFontSize: CGFloat = 28
    static let logoSize: CGFloat = 88
    static let logoIconSize: CGFloat = 64
    static let logoCornerRadius: CGFloat = 22
    static let logoBottomPadding: CGFloat = 10
}

private extension Double {

    static let logoBackgroundOpacity: Double = 0.12
}

private extension ImageResource {

    static let brandIcon = ImageResource(name: "searchIcon", bundle: .main)
}

// MARK: - Preview

#Preview {
    AuthBrandHeader(
        title: "Welcome back",
        subtitle: "Sign in to continue"
    )
    .padding()
}
