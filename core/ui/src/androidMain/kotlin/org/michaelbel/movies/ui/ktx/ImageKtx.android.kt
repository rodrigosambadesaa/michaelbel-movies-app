package org.michaelbel.movies.ui.ktx

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
actual fun navigateToImageUri(): (uri: String) -> Unit {
    val context = LocalContext.current
    return { uri ->
        context.navigateToImageUri(uri.toUri())
    }
}
