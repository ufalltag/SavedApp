import SwiftUI
import Shared

/// Шаг 2 регистрации: ввод username и финальный запрос регистрации.
struct RegisterUsernameView: View {

    let email: String
    let password: String

    /// Регистрация прошла успешно — возвращаемся на экран входа с сообщением.
    let onRegistered: (String) -> Void

    @State private var wrapper: RegisterUsernameViewModelWrapper
    @State private var errorMessage: String?

    init(email: String, password: String, onRegistered: @escaping (String) -> Void) {
        self.email = email
        self.password = password
        self.onRegistered = onRegistered
        _wrapper = State(initialValue: RegisterUsernameViewModelWrapper(email: email, password: password))
    }

    var body: some View {
        content
            .trackScreen("register_username")
            .background(Color(.systemGroupedBackground))
            .navigationTitle(String.navTitle)
            .navigationBarTitleDisplayMode(.inline)
            .onAppear {
                wrapper.collectSideEffects(
                    onNavigateToLogin: { message in onRegistered(message) },
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

private extension RegisterUsernameView {

    var content: some View {
        ScrollView {
            VStack(spacing: .sectionSpacing) {
                RegistrationProgressBar(currentStep: .step, totalSteps: .totalSteps)
                AuthBrandHeader(title: String.title, subtitle: String.subtitle)
                usernameField
                AuthPrimaryButton(
                    title: String.doneButton,
                    isLoading: wrapper.isLoading,
                    action: wrapper.submit
                )
            }
            .padding(.horizontal, .horizontalPadding)
            .padding(.top, .topPadding)
        }
        .scrollIndicators(.hidden)
        .scrollDismissesKeyboard(.interactively)
    }

    var usernameField: some View {
        AuthField(
            icon: .usernameIcon,
            placeholder: String.usernamePlaceholder,
            text: Binding(
                get: { wrapper.username },
                set: { wrapper.onUsernameChanged($0) }
            ),
            contentType: .username,
            submitLabel: .done,
            onSubmit: wrapper.submit
        )
    }
}

// MARK: - Constants

private extension Int {

    static let step = 2
    static let totalSteps = 2
}

private extension CGFloat {

    static let sectionSpacing: CGFloat = 24
    static let horizontalPadding: CGFloat = 24
    static let topPadding: CGFloat = 16
}

private extension String {

    static let navTitle = "Регистрация"
    static let title = "Как вас называть?"
    static let subtitle = "Придумайте имя пользователя для профиля"
    static let usernamePlaceholder = "Имя пользователя"
    static let doneButton = "Готово"
    static let errorTitle = "Ошибка"
    static let okButton = "OK"
    static let usernameIcon = "person"
}

// MARK: - Preview

#Preview {
    NavigationStack {
        RegisterUsernameView(email: "", password: "", onRegistered: { _ in })
    }
}
