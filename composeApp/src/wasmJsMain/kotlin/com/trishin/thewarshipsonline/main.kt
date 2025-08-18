package com.trishin.thewarshipsonline

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  ComposeViewport(
    viewportContainer = requireNotNull(document.body)
  ) {
    TheWarshipsGameApp()
  }
}
