package org.michaelbel.movies.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import org.michaelbel.movies.account.AccountScreen
import org.michaelbel.movies.feed.FeedScreen
import org.michaelbel.movies.settings.SettingsScreen
import org.michaelbel.movies.ui.navigation.AccountDestination
import org.michaelbel.movies.ui.navigation.AppRoute
import org.michaelbel.movies.ui.navigation.MainDestination
import org.michaelbel.movies.ui.navigation.SettingsDestination
import org.michaelbel.movies.main.fadePredictiveTransitionSpec
import org.michaelbel.movies.main.fadeTransitionSpec

@Composable
fun NavContent(
    modifier: Modifier = Modifier
) {
    val backStack: MutableList<AppRoute> = rememberSerializable(serializer = SnapshotStateListSerializer()) {
        mutableStateListOf(MainDestination())
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        transitionSpec = fadeTransitionSpec(),
        popTransitionSpec = fadeTransitionSpec(),
        predictivePopTransitionSpec = fadePredictiveTransitionSpec(),
        entryProvider = entryProvider {
            entry<MainDestination> { FeedScreen() }
            entry<AccountDestination> { AccountScreen() }
            entry<SettingsDestination> { SettingsScreen() }
        }
    )
}
