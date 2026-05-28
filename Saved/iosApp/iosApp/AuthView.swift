import SwiftUI
import Shared

struct AuthView: View {

    @StateObject private var wrapper = AuthViewModelWrapper()
    @State private var errorMessage: String?
    @State private var isAuthenticated = false

    var body: some View {
        if isAuthenticated {
            Text("Вы вошли!")
                .font(.largeTitle)
        } else {
            authForm
                .onAppear {
                    wrapper.collectSideEffects(
                        onNavigateToHome: { isAuthenticated = true },
                        onShowError: { errorMessage = $0 }
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
        }
    }

    private var authForm: some View {
        ScrollView {
            VStack(spacing: 24) {

                Text(wrapper.state.isLoginMode ? "Вход" : "Регистрация")
                    .font(.largeTitle.bold())
                    .frame(maxWidth: .infinity, alignment: .leading)

                VStack(spacing: 16) {
                    // Email — передаём значение из state, при вводе отправляем в ViewModel
                    TextField("Email", text: Binding(
                        get: { wrapper.state.email },
                        set: { wrapper.onEmailChanged($0) }
                    ))
                    .textFieldStyle(.roundedBorder)
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)
                    .autocorrectionDisabled()

                    // Password
                    SecureField("Пароль", text: Binding(
                        get: { wrapper.state.password },
                        set: { wrapper.onPasswordChanged($0) }
                    ))
                    .textFieldStyle(.roundedBorder)
                }

                // Кнопка входа / регистрации
                Button(action: wrapper.submit) {
                    Group {
                        if wrapper.state.isLoading {
                            ProgressView().tint(.white)
                        } else {
                            Text(wrapper.state.isLoginMode ? "Войти" : "Зарегистрироваться")
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 4)
                }
                .buttonStyle(.borderedProminent)
                .disabled(wrapper.state.isLoading)

                // Переключатель между Входом и Регистрацией
                Button(action: wrapper.toggleMode) {
                    Text(wrapper.state.isLoginMode
                         ? "Нет аккаунта? Зарегистрироваться"
                         : "Уже есть аккаунт? Войти")
                    .font(.footnote)
                }
            }
            .padding(24)
        }
    }
}
