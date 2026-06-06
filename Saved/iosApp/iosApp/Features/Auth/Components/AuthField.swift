import SwiftUI

/// Стилизованное поле ввода для экранов авторизации:
/// иконка слева, белая карточка, подсветка рамки в фокусе,
/// для паролей — кнопка показать/скрыть.
struct AuthField: View {

    let icon: String
    let placeholder: String
    @Binding var text: String

    var isSecure: Bool = false
    var keyboard: UIKeyboardType = .default
    var contentType: UITextContentType? = nil
    var submitLabel: SubmitLabel = .next
    var onSubmit: () -> Void = {}

    @FocusState private var isFocused: Bool
    @State private var isRevealed = false

    var body: some View {
        content
    }
}

// MARK: - Content

private extension AuthField {

    var content: some View {
        HStack(spacing: .rowSpacing) {
            iconView
            field
            if isSecure {
                revealButton
            }
        }
        .padding(.horizontal, .horizontalPadding)
        .padding(.vertical, .verticalPadding)
        .background(.background, in: shape)
        .overlay(border)
        .animation(.easeInOut(duration: .borderAnimation), value: isFocused)
    }

    var iconView: some View {
        Image(systemName: icon)
            .font(.system(size: .iconFontSize, weight: .medium))
            .foregroundStyle(isFocused ? Color.accentColor : .secondary)
            .frame(width: .iconSize)
    }

    @ViewBuilder
    var field: some View {
        Group {
            if isSecure && !isRevealed {
                SecureField(placeholder, text: $text)
            } else {
                TextField(placeholder, text: $text)
            }
        }
        .focused($isFocused)
        .font(.body)
        .keyboardType(keyboard)
        .textContentType(contentType)
        .textInputAutocapitalization(.never)
        .autocorrectionDisabled()
        .submitLabel(submitLabel)
        .onSubmit(onSubmit)
    }

    var revealButton: some View {
        Button {
            isRevealed.toggle()
        } label: {
            Image(systemName: isRevealed ? .eyeSlashSymbol : .eyeSymbol)
                .font(.system(size: .iconFontSize))
                .foregroundStyle(.secondary)
        }
        .buttonStyle(.plain)
    }

    var shape: RoundedRectangle {
        RoundedRectangle(cornerRadius: .cornerRadius, style: .continuous)
    }

    var border: some View {
        shape.strokeBorder(
            isFocused ? Color.accentColor : Color.clear,
            lineWidth: .borderWidth
        )
    }
}

// MARK: - Constants

private extension CGFloat {

    static let rowSpacing: CGFloat = 12
    static let horizontalPadding: CGFloat = 16
    static let verticalPadding: CGFloat = 16
    static let cornerRadius: CGFloat = 16
    static let iconSize: CGFloat = 22
    static let iconFontSize: CGFloat = 16
    static let borderWidth: CGFloat = 1.5
}

private extension Double {

    static let borderAnimation: Double = 0.2
}

private extension String {

    static let eyeSymbol = "eye"
    static let eyeSlashSymbol = "eye.slash"
}

// MARK: - Preview

#Preview {
    VStack(spacing: 16) {
        AuthField(icon: "envelope", placeholder: "Email", text: .constant("hi@mail.com"))
        AuthField(icon: "lock", placeholder: "Password", text: .constant("secret"), isSecure: true)
    }
    .padding()
    .background(Color(.systemGroupedBackground))
}
