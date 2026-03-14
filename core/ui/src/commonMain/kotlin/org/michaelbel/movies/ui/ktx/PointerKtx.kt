package org.michaelbel.movies.ui.ktx

import androidx.compose.ui.Modifier

expect fun Modifier.onSecondaryClick(onClick: () -> Unit): Modifier
