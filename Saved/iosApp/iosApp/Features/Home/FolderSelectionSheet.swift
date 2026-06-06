import SwiftUI
import Shared

struct FolderSelectionContext: Identifiable {
    let id = UUID()
    let url: String
    let suggestedFolderName: String?
    let bookmarkTitle: String
}

struct FolderSelectionSheet: View {

    let context: FolderSelectionContext
    let folders: [Folder]
    let onCreateNew: (String) -> Void
    let onSelectExisting: (String) -> Void
    let onDismiss: () -> Void

    @State private var showFolderPicker = false

    var body: some View {
        VStack(spacing: 0) {
            handle

            VStack(spacing: .contentSpacing) {
                icon
                texts
                buttons
            }
            .padding(.horizontal, .horizontalPadding)
            .padding(.top, .topPadding)
            .padding(.bottom, .bottomPadding)
        }
        .frame(maxWidth: .infinity)
        .confirmationDialog(
            String.folderPickerTitle,
            isPresented: $showFolderPicker,
            titleVisibility: .visible
        ) {
            ForEach(folders, id: \.id) { folder in
                Button(folder.name) {
                    onSelectExisting(folder.id)
                }
            }
        }
    }
}

// MARK: - Subviews

private extension FolderSelectionSheet {

    var handle: some View {
        Capsule()
            .fill(Color.secondary.opacity(.handleOpacity))
            .frame(width: .handleWidth, height: .handleHeight)
            .padding(.top, .handleTopPadding)
    }

    var icon: some View {
        Image(systemName: String.sparklesSymbol)
            .font(.system(size: .iconSize))
            .foregroundStyle(.blue)
            .padding(.top, .iconTopPadding)
    }

    var texts: some View {
        VStack(spacing: .textSpacing) {
            Text(String.sheetTitle)
                .font(.headline)
                .multilineTextAlignment(.center)

            if let name = context.suggestedFolderName {
                Text(name)
                    .font(.title3.bold())
                    .foregroundStyle(.primary)
            }

            Text(String.sheetSubtitle)
                .font(.subheadline)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
        }
    }

    var buttons: some View {
        VStack(spacing: .buttonSpacing) {
            if let name = context.suggestedFolderName {
                Button {
                    onCreateNew(name)
                } label: {
                    Text("\(String.createButtonPrefix)\"\(name)\"")
                        .font(.body.weight(.semibold))
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, .buttonVerticalPadding)
                        .background(.blue.gradient, in: RoundedRectangle(cornerRadius: .buttonCornerRadius))
                        .foregroundStyle(.white)
                }
            }

            Button {
                showFolderPicker = true
            } label: {
                Text(String.chooseExistingButton)
                    .font(.body.weight(.medium))
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, .buttonVerticalPadding)
                    .background(Color.secondary.opacity(.secondaryButtonOpacity), in: RoundedRectangle(cornerRadius: .buttonCornerRadius))
                    .foregroundStyle(.primary)
            }

            Button(String.cancelButton, role: .cancel) {
                onDismiss()
            }
            .font(.subheadline)
            .foregroundStyle(.secondary)
            .padding(.top, .cancelTopPadding)
        }
    }
}

// MARK: - Constants

private extension CGFloat {
    static let contentSpacing: CGFloat = 20
    static let horizontalPadding: CGFloat = 24
    static let topPadding: CGFloat = 8
    static let bottomPadding: CGFloat = 32
    static let handleWidth: CGFloat = 36
    static let handleHeight: CGFloat = 4
    static let handleTopPadding: CGFloat = 12
    static let iconSize: CGFloat = 36
    static let iconTopPadding: CGFloat = 8
    static let textSpacing: CGFloat = 6
    static let buttonSpacing: CGFloat = 12
    static let buttonVerticalPadding: CGFloat = 14
    static let buttonCornerRadius: CGFloat = 14
    static let cancelTopPadding: CGFloat = 4
}

private extension Double {
    static let handleOpacity: Double = 0.3
    static let secondaryButtonOpacity: Double = 0.12
}

private extension String {
    static let sparklesSymbol = "sparkles"
    static let folderPickerTitle = "Choose an existing folder"
    static let sheetTitle = "AI suggests creating a folder"
    static let sheetSubtitle = "None of your current folders fit this link. Create a new one or pick an existing one."
    static let createButtonPrefix = "Create folder "
    static let chooseExistingButton = "Choose existing"
    static let cancelButton = "Cancel"
}
