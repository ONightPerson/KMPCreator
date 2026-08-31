package com.lbz.kmpcreator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform