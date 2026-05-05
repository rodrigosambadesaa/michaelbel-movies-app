package org.michaelbel.movies.common.browser

import androidx.compose.runtime.Composable

actual fun tmdbAuthRedirectUrl(): String = defaultTmdbAuthRedirectUrl

@Composable
actual fun navigateToUrl(url: String): () -> Unit {
    return {}
}
