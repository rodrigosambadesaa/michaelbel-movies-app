package org.michaelbel.movies.ui.ktx

import androidx.compose.runtime.Composable

@Composable
expect fun navigateToImageUri(): (uri: String) -> Unit
