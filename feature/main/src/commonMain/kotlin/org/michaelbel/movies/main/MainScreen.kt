package org.michaelbel.movies.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.account.AccountScreen
import org.michaelbel.movies.auth.AuthScreen
import org.michaelbel.movies.debug.DebugScreen
import org.michaelbel.movies.details.DetailsScreen
import org.michaelbel.movies.gallery.GalleryScreen
import org.michaelbel.movies.main.event.MainEvent
import org.michaelbel.movies.main.intent.MainIntent
import org.michaelbel.movies.main.tabs.MainTabsScreen
import org.michaelbel.movies.notify.NotifyScreen
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.settings.SettingsScreen
import org.michaelbel.movies.ui.ObserveAsEvents
import org.michaelbel.movies.ui.bottomSheetUsePlatformDefaultWidth
import org.michaelbel.movies.ui.collectAsStateCommon
import org.michaelbel.movies.ui.dialogUsePlatformDefaultWidth
import org.michaelbel.movies.ui.fadePredictiveTransitionSpec
import org.michaelbel.movies.ui.fadeTransitionSpec
import org.michaelbel.movies.ui.navigation.AccountDestination
import org.michaelbel.movies.ui.navigation.AppRoute
import org.michaelbel.movies.ui.navigation.AuthDestination
import org.michaelbel.movies.ui.navigation.DebugDestination
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.navigation.FeedDestination
import org.michaelbel.movies.ui.navigation.GalleryDestination
import org.michaelbel.movies.ui.navigation.MainDestination
import org.michaelbel.movies.ui.navigation.MainNavigator
import org.michaelbel.movies.ui.navigation.NotifyDestination
import org.michaelbel.movies.ui.navigation.SettingsDestination

@Composable
fun MainScreen(
    onFinish: () -> Unit = {},
    onAuthenticate: () -> Unit = {},
    onScreenshotBlockEnabledChanged: (Boolean) -> Unit = {},
    onRequestReview: () -> Unit = {},
    onRequestUpdate: () -> Unit = {},
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val backStack: MutableList<AppRoute> = rememberSerializable(serializer = SnapshotStateListSerializer()) {
        mutableStateListOf(MainDestination())
    }

    LaunchedEffect(state.isScreenshotBlockEnabled) {
        onScreenshotBlockEnabledChanged(state.isScreenshotBlockEnabled)
    }

    LaunchedEffect(state.openDebugSheet) {
        if (state.openDebugSheet) {
            if (backStack.lastOrNull() != DebugDestination) {
                backStack.add(DebugDestination)
            }
            viewModel.dispatch(MainIntent.ConsumeDebugNavigation)
        }
    }

    MainScreenContent(
        backStack = backStack
    )

    ObserveAsEvents(
        flow = viewModel.eventFlow
    ) { event ->
        when (event) {
            is MainEvent.BiometricCancel -> onFinish()
            is MainEvent.BiometricAuthenticate -> onAuthenticate()
            else -> Unit
        }
    }

    ObserveAsEvents(
        flow = MainNavigator.destFlow
    ) { dest ->
        when (dest) {
            is MainNavigator.NavigationEvent.Back -> if (backStack.size > 1) backStack.removeLastOrNull()
            is MainNavigator.NavigationEvent.Forward -> {
                if (backStack.lastOrNull() != dest.destination) {
                    backStack.add(dest.destination)
                }
            }
            is MainNavigator.NavigationEvent.RequestReview -> onRequestReview()
            is MainNavigator.NavigationEvent.RequestUpdate -> onRequestUpdate()
        }
    }
}

@Composable
private fun MainScreenContent(
    backStack: MutableList<AppRoute>
) {
    val dialogSceneStrategy = remember { DialogSceneStrategy<AppRoute>() }

    NavDisplay(
        backStack = backStack,
        modifier = Modifier.fillMaxSize(),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        sceneStrategies = listOf(dialogSceneStrategy),
        transitionSpec = fadeTransitionSpec(),
        popTransitionSpec = fadeTransitionSpec(),
        predictivePopTransitionSpec = fadePredictiveTransitionSpec(),
        entryProvider = entryProvider {
            entry<AuthDestination>(
                metadata = DialogSceneStrategy.dialog(dialogProperties = DialogProperties(usePlatformDefaultWidth = dialogUsePlatformDefaultWidth))
            ) {
                AuthScreen()
            }
            entry<AccountDestination>(
                metadata = DialogSceneStrategy.dialog(dialogProperties = DialogProperties(usePlatformDefaultWidth = dialogUsePlatformDefaultWidth))
            ) {
                AccountScreen()
            }
            entry<NotifyDestination>(
                metadata = DialogSceneStrategy.dialog(dialogProperties = DialogProperties(usePlatformDefaultWidth = bottomSheetUsePlatformDefaultWidth))
            ) {
                NotifyScreen()
            }
            entry<DebugDestination>(
                metadata = DialogSceneStrategy.dialog(dialogProperties = DialogProperties(usePlatformDefaultWidth = bottomSheetUsePlatformDefaultWidth))
            ) {
                DebugScreen()
            }
            entry<MainDestination> {
                MainTabsScreen(
                    feedDestination = FeedDestination(
                        mainDestination = it.copy(approved = it.approved.orEmpty())
                    )
                )
            }
            entry<DetailsDestination> { DetailsScreen(destination = it) }
            entry<GalleryDestination> { GalleryScreen(destination = it) }
            entry<SettingsDestination> { SettingsScreen() }
        }
    )
}
