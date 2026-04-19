@file:OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3Api::class
)

package org.michaelbel.movies.main.tabs.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.main.tabs.intent.MainTabsIntent
import org.michaelbel.movies.main.tabs.model.MainTabsModel
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.isNavigationRail
import org.michaelbel.movies.ui.navigation.AppRoute
import org.michaelbel.movies.ui.navigation.FaveDestination
import org.michaelbel.movies.ui.navigation.FeedDestination
import org.michaelbel.movies.ui.navigation.SettingsDestination
import org.michaelbel.movies.ui.strings.MoviesStrings

@Composable
fun MainTabsBottomBar(
    state: MainTabsModel,
    currentDestination: AppRoute?,
    feedDestination: FeedDestination,
    dispatch: (MainTabsIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = if (isNavigationRail) 16.dp else 0.dp
            )
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalFloatingToolbar(
            expanded = true
        ) {
            Row(
                modifier = Modifier.animateContentSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        positioning = TooltipAnchorPosition.Above
                    ),
                    tooltip = {
                        PlainTooltip {
                            Text(
                                text = stringResource(MoviesStrings.main_nav_feed)
                            )
                        }
                    },
                    state = rememberTooltipState(),
                    enableUserInput = currentDestination != feedDestination && !isNavigationRail
                ) {
                    ToggleButton(
                        checked = currentDestination == feedDestination,
                        onCheckedChange = { dispatch(MainTabsIntent.FeedClick) },
                        shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(
                            imageVector = MoviesIcons.GridView,
                            contentDescription = stringResource(MoviesStrings.main_nav_feed),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                        )

                        if (currentDestination == feedDestination || isNavigationRail) {
                            Text(
                                text = stringResource(MoviesStrings.main_nav_feed),
                                style = MaterialTheme.typography.titleSmallEmphasized.copy(letterSpacing = .4.sp),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Clip,
                                modifier = Modifier.padding(start = ToggleButtonDefaults.IconSpacing)
                            )
                        }
                    }
                }

                if (state.isFaveFeatureEnabled) {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                            positioning = TooltipAnchorPosition.Above
                        ),
                        tooltip = {
                            PlainTooltip {
                                Text(
                                    text = stringResource(MoviesStrings.main_nav_fave)
                                )
                            }
                        },
                        state = rememberTooltipState(),
                        enableUserInput = currentDestination != FaveDestination && !isNavigationRail
                    ) {
                        ToggleButton(
                            checked = currentDestination == FaveDestination,
                            onCheckedChange = { dispatch(MainTabsIntent.FaveClick) },
                            shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Icon(
                                imageVector = MoviesIcons.Favorite,
                                contentDescription = stringResource(MoviesStrings.main_nav_fave),
                                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                            )

                            if (currentDestination == FaveDestination || isNavigationRail) {
                                Text(
                                    text = stringResource(MoviesStrings.main_nav_fave),
                                    style = MaterialTheme.typography.titleSmallEmphasized.copy(letterSpacing = .4.sp),
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Clip,
                                    modifier = Modifier.padding(start = ToggleButtonDefaults.IconSpacing)
                                )
                            }
                        }
                    }
                }

                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        positioning = TooltipAnchorPosition.Above
                    ),
                    tooltip = {
                        PlainTooltip {
                            Text(
                                text = stringResource(MoviesStrings.main_nav_settings)
                            )
                        }
                    },
                    state = rememberTooltipState(),
                    enableUserInput = currentDestination != SettingsDestination && !isNavigationRail
                ) {
                    ToggleButton(
                        checked = currentDestination == SettingsDestination,
                        onCheckedChange = { dispatch(MainTabsIntent.SettingsClick) },
                        shapes = ToggleButtonDefaults.shapes(CircleShape, CircleShape, CircleShape),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(
                            imageVector = MoviesIcons.Settings,
                            contentDescription = stringResource(MoviesStrings.main_nav_settings),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                        )

                        if (currentDestination == SettingsDestination || isNavigationRail) {
                            Text(
                                text = stringResource(MoviesStrings.main_nav_settings),
                                style = MaterialTheme.typography.titleSmallEmphasized.copy(letterSpacing = .4.sp),
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Clip,
                                modifier = Modifier.padding(start = ToggleButtonDefaults.IconSpacing)
                            )
                        }
                    }
                }
            }
        }
    }
}
