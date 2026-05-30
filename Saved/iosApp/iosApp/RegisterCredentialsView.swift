import SwiftUI
import Shared

/// Шаг 1 регистрации: ввод email и пароля.
struct RegisterCredentialsView: View {

    /// Переход на шаг 2 с собранными данными.
    let onNext: (_ email: String, _ password: String) -> Void

    @State private var wrapper = RegisterCredentialsViewModelWrapper()
    @State private var errorMessage: String?

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {

                Text("Регистрация")
                    .font(.largeTitle.bold())
                    .frame(maxWidth: .infinity, alignment: .leading)

                Text("Шаг 1 из 2")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
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

                Button(action: wrapper.next) {
                    Text("Далее")
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 4)
                }
                .buttonStyle(.borderedProminent)
            }
            .padding(24)
        }
        .navigationTitle("Регистрация")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            wrapper.collectSideEffects(
                onNavigateToUsername: { email, password in onNext(email, password) },
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
