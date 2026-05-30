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
        NavigationStack(path: $path) {
            form
                .navigationTitle("Вход")
                .navigationDestination(for: RegisterRoute.self) { route in
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
        }
        .onAppear {
            wrapper.collectSideEffects(
                onNavigateToHome: { onAuthenticated() },
                onShowError: { errorMessage = $0 },
                onShowMessage: { successMessage = $0 }
            )
        }
        .alert("Ошибка", isPresented: Binding(
            get: { errorMessage != nil },
            set: { if !$0 { errorMessage = nil } }
        )) {
            Button("OK") { errorMessage = nil }
        } message: {
            Text(errorMessage ?? "")
        }
        .alert("Готово", isPresented: Binding(
            get: { successMessage != nil },
            set: { if !$0 { successMessage = nil } }
        )) {
            Button("OK") { successMessage = nil }
        } message: {
            Text(successMessage ?? "")
        }
    }

    private var form: some View {
        ScrollView {
            VStack(spacing: 24) {

                Text("Вход")
                    .font(.largeTitle.bold())
                    .frame(maxWidth: .infinity, alignment: .leading)

                VStack(spacing: 16) {
                    TextField("Email", text: Binding(
                        get: { wrapper.email },
                        set: { wrapper.onEmailChanged($0) }
                    ))
                    .textFieldStyle(.roundedBorder)
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)
                    .autocorrectionDisabled()

                    SecureField("Пароль", text: Binding(
                        get: { wrapper.password },
                        set: { wrapper.onPasswordChanged($0) }
                    ))
                    .textFieldStyle(.roundedBorder)
                }

                Button(action: wrapper.submit) {
                    Group {
                        if wrapper.isLoading {
                            ProgressView().tint(.white)
                        } else {
                            Text("Войти")
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 4)
                }
                .buttonStyle(.borderedProminent)
                .disabled(wrapper.isLoading)

                Button {
                    path.append(.credentials)
                } label: {
                    Text("Нет аккаунта? Зарегистрироваться")
                        .font(.footnote)
                }
            }
            .padding(24)
        }
    }
}
