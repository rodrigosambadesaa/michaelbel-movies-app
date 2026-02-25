package org.michaelbel.movies.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import org.michaelbel.movies.account.AccountScreen
import org.michaelbel.movies.auth.AuthScreen
import org.michaelbel.movies.details.DetailsScreen
import org.michaelbel.movies.gallery.GalleryScreen
import org.michaelbel.movies.main.mainnav.MainNavRoute
import org.michaelbel.movies.settings.SettingsScreen
import org.michaelbel.movies.ui.ktx.ObserveAsEvents
import org.michaelbel.movies.ui.ktx.USE_PLATFORM_DEFAULT_WIDTH
import org.michaelbel.movies.ui.navigation.AccountDestination
import org.michaelbel.movies.ui.navigation.AppRoute
import org.michaelbel.movies.ui.navigation.AuthDestination
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.navigation.GalleryDestination
import org.michaelbel.movies.ui.navigation.MainDestination
import org.michaelbel.movies.ui.navigation.MainNavigator
import org.michaelbel.movies.ui.navigation.SettingsDestination

@Composable
fun MainContent(
    onRequestReview: () -> Unit = {},
    onRequestUpdate: () -> Unit = {}
) {
    val backStack: MutableList<AppRoute> = rememberSerializable(serializer = SnapshotStateListSerializer()) {
        mutableStateListOf(MainDestination())
    }

    NavDisplay(
        backStack = backStack,
        modifier = Modifier.fillMaxSize(),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        sceneStrategy = remember { DialogSceneStrategy() },
        transitionSpec = fadeTransitionSpec(),
        popTransitionSpec = fadeTransitionSpec(),
        predictivePopTransitionSpec = fadePredictiveTransitionSpec(),
        entryProvider = entryProvider {
            entry<AuthDestination>(
                metadata = DialogSceneStrategy.dialog(
                    dialogProperties = DialogProperties(
                        usePlatformDefaultWidth = USE_PLATFORM_DEFAULT_WIDTH
                    )
                )
            ) {
                AuthScreen()
            }
            entry<AccountDestination>(
                metadata = DialogSceneStrategy.dialog(
                    dialogProperties = DialogProperties(
                        usePlatformDefaultWidth = USE_PLATFORM_DEFAULT_WIDTH
                    )
                )
            ) {
                AccountScreen()
            }
            entry<MainDestination> {
                MainNavRoute(
                    requestToken = it.requestToken,
                    approved = it.approved
                )
            }
            entry<DetailsDestination> { DetailsScreen(destination = it) }
            entry<GalleryDestination> { GalleryScreen(destination = it) }
            entry<SettingsDestination> { SettingsScreen() }
        }
    )

    ObserveAsEvents(MainNavigator.destFlow) { dest ->
        when (dest) {
            is MainNavigator.NavigationEvent.Back -> if (backStack.size > 1) backStack.removeLastOrNull()
            is MainNavigator.NavigationEvent.Forward -> backStack.add(dest.destination)
            is MainNavigator.NavigationEvent.RequestReview -> onRequestReview()
            is MainNavigator.NavigationEvent.RequestUpdate -> onRequestUpdate()
        }
    }
}
