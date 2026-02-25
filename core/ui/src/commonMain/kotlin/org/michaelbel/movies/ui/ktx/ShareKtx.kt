package org.michaelbel.movies.ui.ktx

import androidx.compose.runtime.Composable

@Composable
expect fun shareText(text: String, title: String): () -> Unit
