import SwiftUI
import Shared

/// Маршруты потока регистрации внутри NavigationStack экрана входа.
enum RegisterRoute: Hashable {
    case credentials                                    // шаг 1: email + пароль
    case username(email: String, password: String)      // шаг 2: username
}

/// Экран Входа. Также является корнем навигации для потока регистрации.
struct LoginView: View {

    /// Вызывается после успешного входа — переключает приложение на главный экран.
    let onAuthenticated: () -> Void

    @State private var wrapper = LoginViewModelWrapper()
    @State private var path: [RegisterRoute] = []
    @State private var errorMessage: String?
    @State private var successMessage: String?

    var body: some View {
        navigation
            .trackScreen("login")
            .onAppear {
                wrapper.collectSideEffects(
                    onNavigateToHome: { onAuthenticated() },
                    onShowError: { errorMessage = $0 },
                    onShowMessage: { successMessage = $0 }
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
            .alert(String.doneTitle, isPresented: Binding(
                get: { successMessage != nil },
                set: { if !$0 { successMessage = nil } }
            )) {
                Button(String.okButton) { successMessage = nil }
            } message: {
                Text(successMessage ?? "")
            }
    }
}

// MARK: - Content

private extension LoginView {

    var navigation: some View {
        NavigationStack(path: $path) {
            content
                .background(Color(.systemGroupedBackground))
                .navigationDestination(for: RegisterRoute.self, destination: destination)
        }
    }

    @ViewBuilder
    func destination(for route: RegisterRoute) -> some View {
        switch route {
        case .credentials:
            RegisterCredentialsView(onNext: { email, password in
                path.append(.username(email: email, password: password))
            })
        case .username(let email, let password):
            RegisterUsernameView(
                email: email,
                password: password,
                onRegistered: { message in
                    path.removeAll()           // вернуться на экран входа
                    successMessage = message
                }
            )
        }
    }

    var content: some View {
        ScrollView {
            VStack(spacing: .sectionSpacing) {
                AuthBrandHeader(title: String.title, subtitle: String.subtitle)
                fields
                AuthPrimaryButton(
                    title: String.loginButton,
                    isLoading: wrapper.isLoading,
                    action: wrapper.submit
                )
                registerLink
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
                contentType: .password,
                submitLabel: .go,
                onSubmit: wrapper.submit
            )
        }
    }

    var registerLink: some View {
        HStack(spacing: .linkSpacing) {
            Text(String.noAccount)
                .foregroundStyle(.secondary)
            Button(String.registerButton) {
                path.append(.credentials)
            }
            .fontWeight(.semibold)
        }
        .font(.subheadline)
        .padding(.top, .linkTopPadding)
    }
}

// MARK: - Constants

private extension CGFloat {

    static let sectionSpacing: CGFloat = 24
    static let fieldSpacing: CGFloat = 14
    static let horizontalPadding: CGFloat = 24
    static let topPadding: CGFloat = 32
    static let linkSpacing: CGFloat = 4
    static let linkTopPadding: CGFloat = 4
}

private extension String {

    static let title = "Welcome back"
    static let subtitle = "Sign in to get back to your saved"
    static let emailPlaceholder = "Email"
    static let passwordPlaceholder = "Password"
    static let loginButton = "Sign In"
    static let noAccount = "No account?"
    static let registerButton = "Sign Up"
    static let errorTitle = "Error"
    static let doneTitle = "Done"
    static let okButton = "OK"
    static let emailIcon = "envelope"
    static let passwordIcon = "lock"
}

// MARK: - Preview

#Preview {
    LoginView(onAuthenticated: {})
}
