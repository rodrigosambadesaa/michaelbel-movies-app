package org.michaelbel.movies.ui.ktx

import androidx.compose.ui.Modifier

actual fun Modifier.onSecondaryClick(
    onClick: () -> Unit
): Modifier = this
