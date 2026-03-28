@file:OptIn(ExperimentalComposeUiApi::class)

package org.michaelbel.movies

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport

fun main() {
    ComposeViewport(
        viewportContainerId = "ComposeTarget"
    ) {
        WebApp()
    }
}
