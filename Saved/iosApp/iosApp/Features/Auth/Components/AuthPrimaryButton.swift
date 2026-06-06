import SwiftUI

struct AuthPrimaryButton: View {

    let title: String
    var isLoading: Bool = false
    var isEnabled: Bool = true
    let action: () -> Void

    var body: some View {
        content
    }
}

// MARK: - Content

private extension AuthPrimaryButton {

    var content: some View {
        Button(action: action) {
            label
        }
        .buttonStyle(.plain)
        .disabled(isLoading || !isEnabled)
    }

    var label: some View {
        Group {
            if isLoading {
                ProgressView()
                    .tint(.white)
            } else {
                Text(title)
                    .font(.body.weight(.semibold))
                    .foregroundStyle(.white)
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: .height)
        .background(shape.fill(background))
        .opacity(isEnabled ? 1 : .disabledOpacity)
    }

    var background: AnyGradient {
        isEnabled ? Color.accentColor.gradient : Color.gray.gradient
    }

    var shape: RoundedRectangle {
        RoundedRectangle(cornerRadius: .cornerRadius, style: .continuous)
    }
}

// MARK: - Constants

private extension CGFloat {

    static let height: CGFloat = 54
    static let cornerRadius: CGFloat = 16
}

private extension Double {

    static let disabledOpacity: Double = 0.6
}

// MARK: - Preview

#Preview {
    VStack(spacing: 16) {
        AuthPrimaryButton(title: "Sign In", action: {})
        AuthPrimaryButton(title: "Loading", isLoading: true, action: {})
        AuthPrimaryButton(title: "Disabled", isEnabled: false, action: {})
    }
    .padding()
}
