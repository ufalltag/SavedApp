package org.example.saved.di

import org.example.saved.presentation.auth.AuthViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class KoinHelper : KoinComponent {
    fun getAuthViewModel(): AuthViewModel = get()
}
