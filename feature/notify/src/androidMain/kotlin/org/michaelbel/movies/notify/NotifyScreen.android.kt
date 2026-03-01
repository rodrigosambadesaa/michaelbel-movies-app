package org.michaelbel.movies.notify

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.michaelbel.movies.notify.intent.NotifyIntent
import org.michaelbel.movies.notify.ui.NotifyBottomSheet

@Composable
actual fun NotifyScreen(
    viewModel: NotifyViewModel
) {
    NotifyBottomSheet(
        onDismissRequest = { viewModel.dispatch(NotifyIntent.DismissRequest) },
        modifier = Modifier.fillMaxWidth()
    )
}
