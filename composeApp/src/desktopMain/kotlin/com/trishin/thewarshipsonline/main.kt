package com.trishin.thewarshipsonline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
  Window(onCloseRequest = ::exitApplication, title = "The Warships Online") {
    TheWarshipsGameApp()
  }
}

@Preview
@Composable
fun AppDesktopPreview() {
  TheWarshipsGameApp()
}
