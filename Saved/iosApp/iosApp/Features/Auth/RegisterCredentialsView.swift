import SwiftUI
import Shared

/// Шаг 1 регистрации: ввод email и пароля.
struct RegisterCredentialsView: View {

    /// Переход на шаг 2 с собранными данными.
    let onNext: (_ email: String, _ password: String) -> Void

    @State private var wrapper = RegisterCredentialsViewModelWrapper()
    @State private var errorMessage: String?

    var body: some View {
        content
            .trackScreen("register_credentials")
            .background(Color(.systemGroupedBackground))
            .navigationTitle(String.navTitle)
            .navigationBarTitleDisplayMode(.inline)
            .onAppear {
                wrapper.collectSideEffects(
                    onNavigateToUsername: { email, password in onNext(email, password) },
                    onShowError: { errorMessage = $0 }
                )
            }
            .alert(String.errorTitle, isPresented: Binding(
                get: { errorMessage != nil },
                set: { if !$0 { errorMessage = nil } }
            )) {
                Button(String.okButton) { errorMessage = nil }
            } message: {
                Text(errorMessage ?? "")
            }
    }
}

// MARK: - Content

private extension RegisterCredentialsView {

    var content: some View {
        ScrollView {
            VStack(spacing: .sectionSpacing) {
                RegistrationProgressBar(currentStep: .step, totalSteps: .totalSteps)
                AuthBrandHeader(title: String.title, subtitle: String.subtitle)
                fields
                AuthPrimaryButton(title: String.nextButton, action: wrapper.next)
            }
            .padding(.horizontal, .horizontalPadding)
            .padding(.top, .topPadding)
        }
        .scrollIndicators(.hidden)
        .scrollDismissesKeyboard(.interactively)
    }

    var fields: some View {
        VStack(spacing: .fieldSpacing) {
            AuthField(
                icon: .emailIcon,
                placeholder: String.emailPlaceholder,
                text: Binding(
                    get: { wrapper.email },
                    set: { wrapper.onEmailChanged($0) }
                ),
                keyboard: .emailAddress,
                contentType: .username
            )
            AuthField(
                icon: .passwordIcon,
                placeholder: String.passwordPlaceholder,
                text: Binding(
                    get: { wrapper.password },
                    set: { wrapper.onPasswordChanged($0) }
                ),
                isSecure: true,
                contentType: .newPassword,
                submitLabel: .next,
                onSubmit: wrapper.next
            )
        }
    }
}

// MARK: - Constants

private extension Int {

    static let step = 1
    static let totalSteps = 2
}

private extension CGFloat {

    static let sectionSpacing: CGFloat = 24
    static let fieldSpacing: CGFloat = 14
    static let horizontalPadding: CGFloat = 24
    static let topPadding: CGFloat = 16
}

private extension String {

    static let navTitle = "Sign Up"
    static let title = "Create an account"
    static let subtitle = "Enter your email and choose a password"
    static let emailPlaceholder = "Email"
    static let passwordPlaceholder = "Password"
    static let nextButton = "Next"
    static let errorTitle = "Error"
    static let okButton = "OK"
    static let emailIcon = "envelope"
    static let passwordIcon = "lock"
}

// MARK: - Preview

#Preview {
    NavigationStack {
        RegisterCredentialsView(onNext: { _, _ in })
    }
}
