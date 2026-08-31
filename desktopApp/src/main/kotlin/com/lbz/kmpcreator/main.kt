package com.lbz.kmpcreator

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMPCreator",
    ) {
        App()
    }
}