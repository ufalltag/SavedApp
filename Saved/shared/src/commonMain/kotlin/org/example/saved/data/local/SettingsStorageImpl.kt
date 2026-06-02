package org.example.saved.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.example.saved.domain.repository.SettingsStorage

class SettingsStorageImpl(
    private val dataStore: DataStore<Preferences>,
) : SettingsStorage {
    private val darkModeKey = booleanPreferencesKey("dark_mode")

    override suspend fun getDarkMode(): Boolean? =
        dataStore.data
            .map { preferences ->
                if (preferences.contains(darkModeKey)) preferences[darkModeKey] else null
            }.first()

    override suspend fun setDarkMode(isDark: Boolean?) {
        dataStore.edit { preferences ->
            if (isDark == null) {
                preferences.remove(darkModeKey)
            } else {
                preferences[darkModeKey] = isDark
            }
        }
    }
}
