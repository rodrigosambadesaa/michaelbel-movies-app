@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.debug

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import movies.feature.debug.generated.resources.Res
import movies.feature.debug.generated.resources.debug_app_settings
import movies.feature.debug.generated.resources.debug_developer_settings
import movies.feature.debug.generated.resources.debug_firebase_token_copy
import movies.feature.debug.generated.resources.debug_notification_dialog_expire_reset
import movies.feature.debug.generated.resources.debug_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.debug.intent.DebugIntent
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.ui.accessibility.MoviesContentDescription
import org.michaelbel.movies.ui.collectAsStateCommon
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.icons.SettingsAccountBox
import org.michaelbel.movies.ui.icons.SettingsCinematic
import org.michaelbel.movies.ui.theme.bottomListItemShape
import org.michaelbel.movies.ui.theme.middleExtraSmallListItemShape
import org.michaelbel.movies.ui.theme.topListItemShape

@Composable
fun DebugScreen(
    viewModel: DebugViewModel = koinViewModel(),
    uiInteractor: UiInteractor = koinInject()
) {
    val state by viewModel.stateFlow.collectAsStateCommon()

    val copyToClipboard = uiInteractor.rememberCopyToClipboardHandler()
    val navigateToAppSettings = uiInteractor.navigateToAppSettings()
    val navigateToDeveloperSettings = uiInteractor.navigateToDeveloperSettings()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { viewModel.dispatch(DebugIntent.DismissRequest) },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.debug_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.weight(1F)
                    )

                    FilledIconButton(
                        onClick = { viewModel.dispatch(DebugIntent.DismissRequest) },
                        shapes = IconButtonDefaults.shapes(
                            shape = IconButtonDefaults.smallRoundShape,
                            pressedShape = IconButtonDefaults.smallPressedShape
                        ),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = .08F
                            ),
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = MoviesIcons.Close,
                            contentDescription = stringResource(MoviesContentDescription.CloseIcon),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            item {
                ListItem(
                    modifier = Modifier
                        .clip(topListItemShape)
                        .clickable(onClick = navigateToAppSettings),
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.debug_app_settings),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = MoviesIcons.SettingsCinematic,
                            contentDescription = null,
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.inversePrimary,
                        headlineColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
            item {
                ListItem(
                    modifier = Modifier
                        .clip(middleExtraSmallListItemShape)
                        .clickable(onClick = navigateToDeveloperSettings),
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.debug_developer_settings),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = MoviesIcons.SettingsAccountBox,
                            contentDescription = null,
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.inversePrimary,
                        headlineColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
            item {
                ListItem(
                    modifier = Modifier
                        .clip(if (state.isFirebaseTokenFeatureEnabled) middleExtraSmallListItemShape else topListItemShape)
                        .clickable(onClick = { viewModel.dispatch(DebugIntent.ResetNotificationExpireTime) }),
                    headlineContent = {
                        Text(
                            text = stringResource(Res.string.debug_notification_dialog_expire_reset),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = MoviesIcons.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.inversePrimary,
                        headlineColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
            if (state.isFirebaseTokenFeatureEnabled) {
                item {
                    ListItem(
                        modifier = Modifier
                            .clip(bottomListItemShape)
                            .clickable(onClick = { copyToClipboard(state.firebaseToken) }),
                        headlineContent = {
                            Text(
                                text = stringResource(Res.string.debug_firebase_token_copy),
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = MoviesIcons.Token,
                                contentDescription = null,
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.inversePrimary,
                            headlineColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }
    }
}
