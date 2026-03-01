package org.michaelbel.movies.notify

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.michaelbel.movies.ui.navigation.MainNavigator

@Composable
actual fun NotifyScreen(
    viewModel: NotifyViewModel
) {
    LaunchedEffect(Unit) {
        MainNavigator.back()
    }
}
