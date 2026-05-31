import UIKit

func applyTheme(_ isDark: Bool, animated: Bool = true) {
    let style: UIUserInterfaceStyle = isDark ? .dark : .light
    let scenes = UIApplication.shared.connectedScenes.compactMap { $0 as? UIWindowScene }
    let windows = scenes.flatMap { $0.windows }

    if animated {
        windows.forEach { window in
            UIView.transition(
                with: window,
                duration: 0.3,
                options: .transitionCrossDissolve,
                animations: { window.overrideUserInterfaceStyle = style }
            )
        }
    } else {
        windows.forEach { $0.overrideUserInterfaceStyle = style }
    }
}
