package org.example.saved.domain.repository

interface SettingsStorage {
    suspend fun getDarkMode(): Boolean?
    suspend fun setDarkMode(isDark: Boolean?)
}
