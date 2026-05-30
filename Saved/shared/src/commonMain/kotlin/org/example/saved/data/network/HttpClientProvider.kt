package org.example.saved.data.network

import io.ktor.client.HttpClient
import org.example.saved.domain.repository.TokenStorage

class HttpClientProvider(private val tokenStorage: TokenStorage) {

    private var _client: HttpClient = createHttpClient(tokenStorage)

    val client: HttpClient get() = _client

    fun recreate() {
        _client.close()
        _client = createHttpClient(tokenStorage)
    }
}
