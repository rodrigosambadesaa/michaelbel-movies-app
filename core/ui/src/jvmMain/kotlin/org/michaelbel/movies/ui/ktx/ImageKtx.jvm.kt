package org.michaelbel.movies.ui.ktx

import androidx.compose.runtime.Composable
import java.awt.Desktop
import java.io.File
import java.net.URI

@Composable
actual fun navigateToImageUri(): (uri: String) -> Unit {
    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
    return { uri ->
        val imageUri = runCatching { URI.create(uri) }.getOrNull()
        if (desktop != null && imageUri != null) {
            if (imageUri.scheme == "file" && desktop.isSupported(Desktop.Action.OPEN)) {
                desktop.open(File(imageUri))
            } else if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(imageUri)
            }
        }
    }
}
