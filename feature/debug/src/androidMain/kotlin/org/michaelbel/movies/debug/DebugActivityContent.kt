@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.debug

import android.app.Activity
import android.content.ClipData
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import movies.feature.debug.generated.resources.Res
import movies.feature.debug.generated.resources.debug_app_settings
import movies.feature.debug.generated.resources.debug_developer_settings
import movies.feature.debug.generated.resources.debug_firebase_token
import movies.feature.debug.generated.resources.debug_firebase_token_copy
import movies.feature.debug.generated.resources.debug_notification_dialog_expire_reset
import movies.feature.debug.generated.resources.debug_title
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel
import org.michaelbel.movies.ui.accessibility.MoviesContentDescriptionCommon
import org.michaelbel.movies.ui.icons.MoviesAndroidIcons
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.displayCutoutWindowInsets
import org.michaelbel.movies.ui.ktx.rememberNavigateToAppSettings
import org.michaelbel.movies.ui.ktx.rememberNavigateToDeveloperSettings
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun DebugActivityContent(
    viewModel: DebugViewModel = koinViewModel(),
    enableEdgeToEdge: (Any, Any) -> Unit
) {
    val themeData by viewModel.themeDataFlow.collectAsStateCommon()
    val firebaseToken by viewModel.firebaseTokenFlow.collectAsStateCommon()

    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val navigateToAppSettings = rememberNavigateToAppSettings()
    val navigateToDeveloperSettings = rememberNavigateToDeveloperSettings()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true }
    )

    MoviesTheme(
        themeData = themeData,
        enableEdgeToEdge = enableEdgeToEdge
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = stringResource(Res.string.debug_title),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = {
                        IconButton(
                            onClick = { (context as Activity).finish() },
                            modifier = Modifier.windowInsetsPadding(displayCutoutWindowInsets)
                        ) {
                            Icon(
                                imageVector = MoviesIcons.Close,
                                contentDescription = org.jetbrains.compose.resources.stringResource(MoviesContentDescriptionCommon.CloseIcon),
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.inversePrimary
                    ),
                    scrollBehavior = topAppBarScrollBehavior
                )
            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.navigationBarsPadding(),
                state = rememberLazyListState(),
                contentPadding = innerPadding
            ) {
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = navigateToAppSettings),
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.debug_app_settings),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(MoviesAndroidIcons.SettingsCinematicBlur24),
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        },
                        colors = ListItemDefaults.colors().copy(containerColor = Color.Transparent)
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = navigateToDeveloperSettings),
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.debug_developer_settings),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(MoviesAndroidIcons.SettingsAccountBox24),
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        },
                        colors = ListItemDefaults.colors().copy(containerColor = Color.Transparent)
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = viewModel::resetNotificationExpireTime),
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.debug_notification_dialog_expire_reset),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(MoviesAndroidIcons.MovieFilter24),
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )
                        },
                        colors = ListItemDefaults.colors().copy(containerColor = Color.Transparent)
                    )
                }
                if (viewModel.isFirebaseTokenFeatureEnabled) {
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    item {
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = { scope.launch { clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("", firebaseToken))) } }),
                            headlineContent = {
                                Text(
                                    text = stringResource(Res.string.debug_firebase_token),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(Res.string.debug_firebase_token_copy),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(MoviesAndroidIcons.Firebase24),
                                    contentDescription = null,
                                    modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                )
                            },
                            colors = ListItemDefaults.colors().copy(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}
