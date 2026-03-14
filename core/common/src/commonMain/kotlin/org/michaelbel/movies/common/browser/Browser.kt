package org.michaelbel.movies.common.browser

import androidx.compose.runtime.Composable

const val defaultTmdbAuthRedirectUrl = "movies://redirect_url"

expect fun tmdbAuthRedirectUrl(): String

@Composable
expect fun navigateToUrl(url: String): () -> Unit
