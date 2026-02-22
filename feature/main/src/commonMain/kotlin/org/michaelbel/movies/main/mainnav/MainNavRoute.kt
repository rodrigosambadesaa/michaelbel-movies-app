@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.main.mainnav

import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationItemIconPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.feed.FeedScreen
import org.michaelbel.movies.main.intent.MainIntent
import org.michaelbel.movies.main.mainnav.event.MainNavAppEvent
import org.michaelbel.movies.main.mainnav.event.MainNavEvent
import org.michaelbel.movies.settings.SettingsScreen
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.ObserveAsEvents
import org.michaelbel.movies.ui.navigation.AppRoute
import org.michaelbel.movies.ui.navigation.FeedDestination
import org.michaelbel.movies.ui.navigation.SettingsDestination
import org.michaelbel.movies.ui.strings.MoviesStrings

@Composable
fun MainNavRoute(
    requestToken: String?,
    approved: Boolean?,
    viewModel: MainNavViewModel = koinViewModel()
) {
    val layoutDirection = LocalLayoutDirection.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val authFailureMessage = stringResource(MoviesStrings.feed_auth_failure)
    val authSuccessMessage = stringResource(MoviesStrings.feed_auth_success)
    val feedDestination = remember(requestToken, approved) {
        FeedDestination(requestToken = requestToken, approved = approved ?: false)
    }

    val backStack: MutableList<AppRoute> = rememberSerializable(serializer = SnapshotStateListSerializer()) {
        mutableStateListOf(feedDestination)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 0.dp, top = 8.dp)
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                HorizontalFloatingToolbar(
                    expanded = true
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShortNavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = MoviesIcons.GridView,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(MoviesStrings.main_nav_feed),
                                    style = MaterialTheme.typography.titleSmallEmphasized.copy(letterSpacing = .4.sp)
                                )
                            },
                            selected = backStack[backStack.lastIndex] == feedDestination,
                            onClick = {
                                if (backStack[backStack.lastIndex] == feedDestination) {
                                    viewModel.dispatch(MainIntent.FeedReselected)
                                }
                                backStack[backStack.lastIndex] = feedDestination
                            },
                            iconPosition = NavigationItemIconPosition.Start,
                        )

                        ShortNavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = MoviesIcons.Settings,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(MoviesStrings.main_nav_settings),
                                    style = MaterialTheme.typography.titleSmallEmphasized.copy(letterSpacing = .4.sp)
                                )
                            },
                            selected = backStack[backStack.lastIndex] == SettingsDestination,
                            onClick = { backStack[backStack.lastIndex] = SettingsDestination },
                            iconPosition = NavigationItemIconPosition.Start
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(layoutDirection),
                top = 0.dp,
                end = innerPadding.calculateEndPadding(layoutDirection),
                bottom = 0.dp
            ),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            popTransitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(clip = false) },
            predictivePopTransitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(clip = false) },
            entryProvider = entryProvider {
                entry<FeedDestination> { FeedScreen() }
                entry<SettingsDestination> { SettingsScreen() }
            }
        )
    }

    LaunchedEffect(feedDestination.requestToken, feedDestination.approved) {
        viewModel.onRedirect(feedDestination.requestToken, feedDestination.approved)
    }

    ObserveAsEvents(
        flow = MainNavAppEvent.eventFlow
    ) { event ->
        when (event) {
            MainNavAppEvent.Event.OpenFeed -> backStack[backStack.lastIndex] = feedDestination
            MainNavAppEvent.Event.OpenSettings -> backStack[backStack.lastIndex] = SettingsDestination
        }
    }

    ObserveAsEvents(
        flow = viewModel.eventFlow,
        key1 = snackbarHostState,
        key2 = authFailureMessage to authSuccessMessage
    ) { event ->
        when (event) {
            is MainNavEvent.ShowSnackbar -> {
                scope.launch {
                    val message = when (event.message) {
                        MoviesStrings.feed_auth_failure -> authFailureMessage
                        MoviesStrings.feed_auth_success -> authSuccessMessage
                        else -> return@launch
                    }
                    snackbarHostState.run {
                        currentSnackbarData?.dismiss()
                        showSnackbar(message)
                    }
                }
            }
        }
    }
}
