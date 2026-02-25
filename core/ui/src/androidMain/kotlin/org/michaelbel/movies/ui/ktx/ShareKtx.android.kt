package org.michaelbel.movies.ui.ktx

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun shareText(text: String, title: String): () -> Unit {
    val context = LocalContext.current
    return {
        context.navigateToShareText(text, title)
    }
}
