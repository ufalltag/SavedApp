import Foundation

struct LinkItem: Identifiable {

    let id: UUID
    let bookmarkId: String
    let title: String
    let url: String
    let date: Date

    init(id: UUID = UUID(), bookmarkId: String = "", title: String, url: String, date: Date = .now) {
        self.id = id
        self.bookmarkId = bookmarkId
        self.title = title
        self.url = url
        self.date = date
    }
}
