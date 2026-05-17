package org.example.saved

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform