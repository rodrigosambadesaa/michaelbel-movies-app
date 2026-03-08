package org.michaelbel.movies.ui.ktx

import androidx.compose.runtime.Composable

@Composable
expect fun rememberSpeechRecognitionLauncher(onInputText: (String) -> Unit): () -> Unit

@Composable
expect fun shareText(text: String, title: String): () -> Unit

@Composable
expect fun navigateToImageUri(): (uri: String) -> Unit

@Composable
expect fun requestTileService(onSuccess: (String) -> Unit): () -> Unit
