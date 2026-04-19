@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.main.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.michaelbel.movies.feed.FeedScreen
import org.michaelbel.movies.main.MainWebDestination
import org.michaelbel.movies.main.mainWebFeedText
import org.michaelbel.movies.main.mainWebSettingsText
import org.michaelbel.movies.main.mainWebWindowWidth
import org.michaelbel.movies.persistence.database.typealiases.MovieId
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.settingsweb.SettingsScreen

@Composable
fun MainWebTabsScreen(
    currentDestination: MainWebDestination,
    onDestinationChange: (MainWebDestination) -> Unit,
    onMovieClick: (PagingKey, MovieId) -> Unit
) {
    val toggleButtonColors = ToggleButtonDefaults.toggleButtonColors()
    val navigationRailItemColors = NavigationRailItemDefaults.colors(
        selectedIconColor = toggleButtonColors.checkedContentColor,
        selectedTextColor = toggleButtonColors.checkedContentColor,
        indicatorColor = toggleButtonColors.checkedContainerColor,
        unselectedIconColor = toggleButtonColors.contentColor,
        unselectedTextColor = toggleButtonColors.contentColor,
        disabledIconColor = toggleButtonColors.disabledContentColor,
        disabledTextColor = toggleButtonColors.disabledContentColor
    )

    val windowWidth = mainWebWindowWidth

    when {
        windowWidth >= 1200.dp -> {
            NavigationSuiteScaffoldLayout(
                navigationSuite = {
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(192.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(
                                space = 8.dp,
                                alignment = Alignment.CenterVertically
                            )
                        ) {
                            Surface(
                                onClick = { onDestinationChange(MainWebDestination.Feed) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = when {
                                    currentDestination == MainWebDestination.Feed -> toggleButtonColors.checkedContainerColor
                                    else -> Color.Transparent
                                },
                                contentColor = when {
                                    currentDestination == MainWebDestination.Feed -> toggleButtonColors.checkedContentColor
                                    else -> toggleButtonColors.contentColor
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.GridView,
                                        contentDescription = mainWebFeedText,
                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                    )

                                    Text(
                                        text = mainWebFeedText,
                                        modifier = Modifier.padding(start = ToggleButtonDefaults.IconSpacing),
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip,
                                        style = MaterialTheme.typography.titleSmallEmphasized.copy(
                                            letterSpacing = .4.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                onClick = { onDestinationChange(MainWebDestination.Settings) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = when {
                                    currentDestination == MainWebDestination.Settings -> toggleButtonColors.checkedContainerColor
                                    else -> Color.Transparent
                                },
                                contentColor = when {
                                    currentDestination == MainWebDestination.Settings -> toggleButtonColors.checkedContentColor
                                    else -> toggleButtonColors.contentColor
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Settings,
                                        contentDescription = mainWebSettingsText,
                                        modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                                    )

                                    Text(
                                        text = mainWebSettingsText,
                                        modifier = Modifier.padding(start = ToggleButtonDefaults.IconSpacing),
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Clip,
                                        style = MaterialTheme.typography.titleSmallEmphasized.copy(
                                            letterSpacing = .4.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                },
                navigationSuiteType = NavigationSuiteType.WideNavigationRailExpanded
            ) {
                when (currentDestination) {
                    MainWebDestination.Feed -> FeedScreen(onMovieClick = onMovieClick)
                    MainWebDestination.Settings -> SettingsScreen()
                    is MainWebDestination.Details -> Unit
                }
            }
        }
        windowWidth >= 840.dp -> {
            NavigationSuiteScaffold(
                navigationItems = {
                    NavigationRailItem(
                        selected = currentDestination == MainWebDestination.Feed,
                        onClick = { onDestinationChange(MainWebDestination.Feed) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.GridView,
                                contentDescription = mainWebFeedText
                            )
                        },
                        colors = navigationRailItemColors
                    )

                    NavigationRailItem(
                        selected = currentDestination == MainWebDestination.Settings,
                        onClick = { onDestinationChange(MainWebDestination.Settings) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = mainWebSettingsText
                            )
                        },
                        colors = navigationRailItemColors
                    )
                },
                modifier = Modifier.fillMaxSize(),
                navigationSuiteType = NavigationSuiteType.WideNavigationRailCollapsed,
                navigationSuiteColors = NavigationSuiteDefaults.colors(
                    wideNavigationRailColors = WideNavigationRailDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    navigationRailContainerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationItemVerticalArrangement = Arrangement.Center
            ) {
                when (currentDestination) {
                    is MainWebDestination.Feed -> {
                        FeedScreen(
                            onMovieClick = onMovieClick
                        )
                    }
                    is MainWebDestination.Settings -> SettingsScreen()
                    is MainWebDestination.Details -> Unit
                }
            }
        }
        else -> {
            NavigationSuiteScaffold(
                modifier = Modifier.fillMaxSize(),
                layoutType = if (windowWidth >= 600.dp) NavigationSuiteType.ShortNavigationBarMedium else NavigationSuiteType.ShortNavigationBarCompact,
                navigationSuiteItems = {
                    item(
                        selected = currentDestination == MainWebDestination.Feed,
                        onClick = { onDestinationChange(MainWebDestination.Feed) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.GridView,
                                contentDescription = mainWebFeedText
                            )
                        },
                        label = {
                            Text(
                                text = mainWebFeedText
                            )
                        }
                    )
                    item(
                        selected = currentDestination == MainWebDestination.Settings,
                        onClick = { onDestinationChange(MainWebDestination.Settings) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = mainWebSettingsText
                            )
                        },
                        label = {
                            Text(
                                text = mainWebSettingsText
                            )
                        }
                    )
                }
            ) {
                when (currentDestination) {
                    MainWebDestination.Feed -> FeedScreen(onMovieClick = onMovieClick)
                    MainWebDestination.Settings -> SettingsScreen()
                    is MainWebDestination.Details -> Unit
                }
            }
        }
    }
}
