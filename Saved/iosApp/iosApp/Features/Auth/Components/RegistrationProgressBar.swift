import SwiftUI

/// Индикатор прогресса регистрации: сегментированные капсулы + подпись «Шаг X из Y».
struct RegistrationProgressBar: View {

    /// Текущий шаг (1-based).
    let currentStep: Int
    let totalSteps: Int

    var body: some View {
        content
    }
}

// MARK: - Content

private extension RegistrationProgressBar {

    var content: some View {
        VStack(alignment: .leading, spacing: .spacing) {
            segments
            Text("Step \(currentStep) of \(totalSteps)")
                .font(.footnote.weight(.medium))
                .foregroundStyle(.secondary)
        }
    }

    var segments: some View {
        HStack(spacing: .segmentSpacing) {
            ForEach(0..<totalSteps, id: \.self) { index in
                segment(isFilled: index < currentStep)
            }
        }
    }

    func segment(isFilled: Bool) -> some View {
        Capsule()
            .fill(isFilled ? Color.accentColor : Color.gray.opacity(.inactiveOpacity))
            .frame(height: .segmentHeight)
            .animation(.easeInOut(duration: .animation), value: isFilled)
    }
}

// MARK: - Constants

private extension CGFloat {

    static let spacing: CGFloat = 8
    static let segmentSpacing: CGFloat = 6
    static let segmentHeight: CGFloat = 6
}

private extension Double {

    static let inactiveOpacity: Double = 0.2
    static let animation: Double = 0.25
}

// MARK: - Preview

#Preview {
    VStack(spacing: 24) {
        RegistrationProgressBar(currentStep: 1, totalSteps: 2)
        RegistrationProgressBar(currentStep: 2, totalSteps: 2)
    }
    .padding()
}
