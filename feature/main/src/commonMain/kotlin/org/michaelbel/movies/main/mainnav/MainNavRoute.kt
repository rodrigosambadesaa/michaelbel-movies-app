@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.main.mainnav

import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.TonalToggleButton
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
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.feed.ui.FeedScreen
import org.michaelbel.movies.main.intent.MainIntent
import org.michaelbel.movies.settings.ui.SettingsScreen
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.ObserveAsEvents
import org.michaelbel.movies.ui.navigation.AppRoute
import org.michaelbel.movies.ui.navigation.FeedDestination
import org.michaelbel.movies.ui.navigation.SettingsDestination

@Composable
fun MainNavRoute(
    requestToken: String?,
    approved: Boolean?,
    viewModel: MainNavViewModel = koinViewModel()
) {
    val layoutDirection = LocalLayoutDirection.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val feedDestination = remember(requestToken, approved) {
        FeedDestination(requestToken = requestToken, approved = approved ?: false)
    }

    val backStack: MutableList<AppRoute> = rememberSerializable(serializer = SnapshotStateListSerializer()) {
        mutableStateListOf(feedDestination)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.inversePrimary
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TonalToggleButton(
                        checked = backStack[backStack.lastIndex] == feedDestination,
                        onCheckedChange = { backStack[backStack.lastIndex] = feedDestination },
                        shapes = ToggleButtonShapes(shape = RoundedCornerShape(50.dp), pressedShape = RoundedCornerShape(50.dp), checkedShape = RoundedCornerShape(50.dp))
                    ) {
                        Icon(
                            imageVector = MoviesIcons.GridView,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(ButtonDefaults.IconSpacing)
                        )

                        Text(
                            text = "Feed"
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    TonalToggleButton(
                        checked = backStack[backStack.lastIndex] == SettingsDestination,
                        onCheckedChange = { backStack[backStack.lastIndex] = SettingsDestination },
                        shapes = ToggleButtonShapes(shape = RoundedCornerShape(50.dp), pressedShape = RoundedCornerShape(50.dp), checkedShape = RoundedCornerShape(50.dp))
                    ) {
                        Icon(
                            imageVector = MoviesIcons.Settings,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(ButtonDefaults.IconSpacing)
                        )

                        Text(
                            text = "Settings"
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(16.dp)
                    )

                    FloatingActionButton(
                        onClick = { viewModel.dispatch(MainIntent.SearchClick) },
                        modifier = Modifier.size(48.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        elevation = FloatingActionButtonDefaults.elevation()
                    ) {
                        Icon(
                            imageVector = MoviesIcons.Search,
                            contentDescription = null
                        )
                    }
                }
            }
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(layoutDirection),
                top = 0.dp,
                end = innerPadding.calculateEndPadding(layoutDirection),
                bottom = innerPadding.calculateBottomPadding()
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
        flow = viewModel.snackbarMessage,
        key1 = snackbarHostState
    ) { message ->
        scope.launch {
            snackbarHostState.run {
                currentSnackbarData?.dismiss()
                showSnackbar(message)
            }
        }
    }
}