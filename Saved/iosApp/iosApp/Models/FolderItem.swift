import Foundation

struct FolderItem: Identifiable {

    let id: UUID
    let folderId: String
    let title: String
    let linksCount: Int

    init(id: UUID = UUID(), folderId: String = "", title: String, linksCount: Int) {
        self.id = id
        self.folderId = folderId
        self.title = title
        self.linksCount = linksCount
    }
}
