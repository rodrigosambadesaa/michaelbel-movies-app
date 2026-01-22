package org.michaelbel.movies.main.ui

import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import org.michaelbel.movies.account.AccountScreen
import org.michaelbel.movies.feed.ui.FeedScreen
import org.michaelbel.movies.search.ui.SearchScreen
import org.michaelbel.movies.settings.ui.SettingsScreen
import org.michaelbel.movies.ui.navigation.AccountDestination
import org.michaelbel.movies.ui.navigation.AppRoute
import org.michaelbel.movies.ui.navigation.MainDestination
import org.michaelbel.movies.ui.navigation.SearchDestination
import org.michaelbel.movies.ui.navigation.SettingsDestination

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
        popTransitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(clip = false) },
        predictivePopTransitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(clip = false) },
        entryProvider = entryProvider {
            entry<MainDestination> { FeedScreen() }
            entry<SearchDestination> { SearchScreen() }
            entry<AccountDestination> { AccountScreen() }
            entry<SettingsDestination> { SettingsScreen() }
        }
    )
}
