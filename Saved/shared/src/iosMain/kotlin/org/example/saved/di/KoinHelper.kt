package org.example.saved.di

import org.example.saved.presentation.auth.LoginViewModel
import org.example.saved.presentation.auth.RegisterCredentialsViewModel
import org.example.saved.presentation.auth.RegisterUsernameViewModel
import org.example.saved.presentation.bookmarks.BookmarksViewModel
import org.example.saved.presentation.folderlinks.FolderLinksViewModel
import org.example.saved.presentation.folders.AllFoldersViewModel
import org.example.saved.presentation.home.HomeViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class KoinHelper : KoinComponent {
    fun getLoginViewModel(): LoginViewModel = get()
    fun getRegisterCredentialsViewModel(): RegisterCredentialsViewModel = get()

    // email/password передаются с 1-го шага регистрации в фабрику Koin
    fun getRegisterUsernameViewModel(email: String, password: String): RegisterUsernameViewModel =
        get { parametersOf(email, password) }

    fun getBookmarksViewModel(): BookmarksViewModel = get()
    fun getHomeViewModel(): HomeViewModel = get()
    fun getAllFoldersViewModel(): AllFoldersViewModel = get()
    fun getFolderLinksViewModel(folderId: String, folderName: String): FolderLinksViewModel =
        get { parametersOf(folderId, folderName) }
}
