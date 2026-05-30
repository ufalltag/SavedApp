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
        ScrollView {
            VStack(spacing: 24) {

                Text("Имя пользователя")
                    .font(.largeTitle.bold())
                    .frame(maxWidth: .infinity, alignment: .leading)

                Text("Шаг 2 из 2")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .frame(maxWidth: .infinity, alignment: .leading)

                TextField("Username", text: Binding(
                    get: { wrapper.username },
                    set: { wrapper.onUsernameChanged($0) }
                ))
                .textFieldStyle(.roundedBorder)
                .autocapitalization(.none)
                .autocorrectionDisabled()

                Button(action: wrapper.submit) {
                    Group {
                        if wrapper.isLoading {
                            ProgressView().tint(.white)
                        } else {
                            Text("Готово")
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 4)
                }
                .buttonStyle(.borderedProminent)
                .disabled(wrapper.isLoading)
            }
            .padding(24)
        }
        .navigationTitle("Регистрация")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            wrapper.collectSideEffects(
                onNavigateToLogin: { message in onRegistered(message) },
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
