import SwiftUI

struct AccountView: View {

    @State private var vm = AccountViewModelWrapper()
    @AppStorage("isDarkMode") private var isDarkMode = false
    @Environment(\.dismiss) private var dismiss
    @Environment(AppViewModelWrapper.self) private var appWrapper

    @State private var showChangePassword = false
    @State private var oldPassword = ""
    @State private var newPassword = ""
    @State private var errorMessage: String?

    var body: some View {
        NavigationStack {
            content
                .trackScreen("account")
                .background(Color(.systemGroupedBackground))
                .navigationTitle(String.navigationTitle)
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .topBarTrailing) {
                        Button(String.doneButton) { dismiss() }
                            .fontWeight(.semibold)
                    }
                }
                .alert(String.changePasswordTitle, isPresented: $showChangePassword) {
                    changePasswordAlert
                }
                .alert(String.errorTitle, isPresented: Binding(
                    get: { errorMessage != nil },
                    set: { if !$0 { errorMessage = nil } }
                )) {
                    Button(String.alertOk, role: .cancel) { errorMessage = nil }
                } message: {
                    Text(errorMessage ?? "")
                }
                .task {
                    vm.collectSideEffects(
                        onShowError: { errorMessage = $0 },
                        onPasswordChanged: { showChangePassword = false },
                        onLoggedOut: { appWrapper.onLoggedOut() }
                    )
                }
        }
    }
}

// MARK: - Content

private extension AccountView {

    var content: some View {
        ScrollView {
            VStack(spacing: .sectionSpacing) {
                profileHeader
                securityCard
                appearanceCard
                logoutCard
            }
            .padding(.horizontal, .horizontalPadding)
            .padding(.top, .topPadding)
        }
    }

    var profileHeader: some View {
        VStack(spacing: .headerSpacing) {
            avatarView
            VStack(spacing: .nameSpacing) {
                Text(vm.email.emailUsername)
                    .font(.title2)
                    .fontWeight(.bold)
                if !vm.email.isEmpty {
                    Text(vm.email)
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
            }
        }
        .padding(.bottom, .headerBottomPadding)
    }

    var avatarView: some View {
        Circle()
            .fill(Color.accentColor.opacity(.avatarBackgroundOpacity))
            .frame(width: .avatarSize, height: .avatarSize)
            .overlay {
                if vm.isLoading {
                    ProgressView()
                        .tint(Color.accentColor)
                } else {
                    Text(vm.email.avatarLetter)
                        .font(.system(size: .avatarFontSize, weight: .semibold))
                        .foregroundStyle(Color.accentColor)
                }
            }
    }

    var securityCard: some View {
        Button(action: { showChangePassword = true }) {
            HStack(spacing: .rowSpacing) {
                iconView(symbol: String.lockSymbol)
                Text(String.changePassword)
                    .font(.subheadline)
                    .fontWeight(.medium)
                    .foregroundStyle(.primary)
                Spacer()
                if vm.isChangingPassword {
                    ProgressView()
                } else {
                    Image(systemName: String.chevronSymbol)
                        .font(.caption)
                        .fontWeight(.semibold)
                        .foregroundStyle(.tertiary)
                }
            }
            .padding(.rowPadding)
            .background(.background, in: RoundedRectangle(cornerRadius: .cornerRadius))
        }
        .disabled(vm.isChangingPassword)
    }

    var appearanceCard: some View {
        HStack(spacing: .rowSpacing) {
            iconView(symbol: isDarkMode ? String.darkModeSymbol : String.lightModeSymbol)
            Text(String.darkMode)
                .font(.subheadline)
                .fontWeight(.medium)
            Spacer()
            Toggle("", isOn: Binding(
                get: { isDarkMode },
                set: { newValue in
                    isDarkMode = newValue
                    applyTheme(newValue)
                }
            ))
            .labelsHidden()
        }
        .padding(.rowPadding)
        .background(.background, in: RoundedRectangle(cornerRadius: .cornerRadius))
    }

    var logoutCard: some View {
        Button(action: { vm.logout() }) {
            HStack(spacing: .rowSpacing) {
                Image(systemName: String.logoutSymbol)
                    .font(.system(size: .iconFontSize, weight: .medium))
                    .foregroundStyle(Color.red)
                    .frame(width: .iconSize, height: .iconSize)
                    .background(Color.red.opacity(.iconBackgroundOpacity), in: RoundedRectangle(cornerRadius: .iconCornerRadius))
                Text(String.logout)
                    .font(.subheadline)
                    .fontWeight(.medium)
                    .foregroundStyle(Color.red)
                Spacer()
            }
            .padding(.rowPadding)
            .background(.background, in: RoundedRectangle(cornerRadius: .cornerRadius))
        }
    }

    func iconView(symbol: String) -> some View {
        Image(systemName: symbol)
            .font(.system(size: .iconFontSize, weight: .medium))
            .foregroundStyle(Color.accentColor)
            .frame(width: .iconSize, height: .iconSize)
            .background(Color.accentColor.opacity(.iconBackgroundOpacity), in: RoundedRectangle(cornerRadius: .iconCornerRadius))
    }

    @ViewBuilder
    var changePasswordAlert: some View {
        SecureField(String.oldPasswordPlaceholder, text: $oldPassword)
        SecureField(String.newPasswordPlaceholder, text: $newPassword)
        Button(String.changeConfirm) {
            vm.changePassword(oldPassword: oldPassword, newPassword: newPassword)
            oldPassword = ""
            newPassword = ""
        }
        Button(String.alertCancel, role: .cancel) {
            oldPassword = ""
            newPassword = ""
        }
    }
}

// MARK: - String helpers

private extension String {

    var emailUsername: String {
        components(separatedBy: "@").first ?? self
    }

    var avatarLetter: String {
        String(emailUsername.prefix(1).uppercased())
    }
}

// MARK: - Constants

private extension CGFloat {

    static let sectionSpacing: CGFloat = 12
    static let horizontalPadding: CGFloat = 16
    static let topPadding: CGFloat = 24
    static let headerSpacing: CGFloat = 16
    static let nameSpacing: CGFloat = 4
    static let headerBottomPadding: CGFloat = 12
    static let avatarSize: CGFloat = 80
    static let avatarFontSize: CGFloat = 32
    static let rowPadding: CGFloat = 16
    static let rowSpacing: CGFloat = 12
    static let cornerRadius: CGFloat = 16
    static let iconSize: CGFloat = 36
    static let iconFontSize: CGFloat = 15
    static let iconCornerRadius: CGFloat = 8
}

private extension Double {

    static let avatarBackgroundOpacity: Double = 0.2
    static let iconBackgroundOpacity: Double = 0.12
}

private extension String {

    static let navigationTitle = "Account"
    static let doneButton = "Done"
    static let changePassword = "Change Password"
    static let darkMode = "Dark Mode"
    static let logout = "Log Out"
    static let logoutSymbol = "rectangle.portrait.and.arrow.right"
    static let lockSymbol = "lock.fill"
    static let chevronSymbol = "chevron.right"
    static let darkModeSymbol = "moon.fill"
    static let lightModeSymbol = "sun.max.fill"
    static let changePasswordTitle = "Change Password"
    static let oldPasswordPlaceholder = "Current password"
    static let newPasswordPlaceholder = "New password"
    static let changeConfirm = "Change"
    static let alertCancel = "Cancel"
    static let alertOk = "OK"
    static let errorTitle = "Error"
}

// MARK: - Preview

#Preview {
    AccountView()
        .environment(AppViewModelWrapper())
}
