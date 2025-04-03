package org.michaelbel.movies.account

import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import org.michaelbel.movies.account.ui.AccountRoute
import org.michaelbel.movies.ui.ktx.USE_PLATFORM_DEFAULT_WIDTH

fun NavGraphBuilder.accountGraph() {
    dialog<AccountDestination>(
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = USE_PLATFORM_DEFAULT_WIDTH
        )
    ) {
        AccountRoute()
    }
}