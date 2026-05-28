package org.example.saved.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.example.saved.domain.repository.TokenStorage

class TokenStorageImpl(
    private val dataStore: DataStore<Preferences>,
) : TokenStorage {
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    override suspend fun getAccessToken(): String? =
        dataStore.data.map { preferences -> preferences[accessTokenKey] }.first()

    override suspend fun getRefreshToken(): String? =
        dataStore.data.map { preferences -> preferences[refreshTokenKey] }.first()

    override suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
    ) {
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[refreshTokenKey] = refreshToken
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
        }
    }
}
