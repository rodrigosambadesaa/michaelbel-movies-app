@file:OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package org.michaelbel.movies.settingsweb

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.LocalMovies
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.michaelbel.movies.common.MOVIES_GITHUB_URL
import org.michaelbel.movies.common.MOVIES_TELEGRAM_URL
import org.michaelbel.movies.common.SealedString
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.interactor.AboutInteractor
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.interactor.UiInteractor

@Composable
fun SettingsWebScreen(
    onFeedClick: () -> Unit = {},
    onFaveClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    interactor: Interactor = koinInject(),
    uiInteractor: UiInteractor = koinInject(),
    aboutInteractor: AboutInteractor = koinInject()
) {
    val themeData by interactor.themeData.collectAsState(initial = ThemeData.Default)
    val feedView by interactor.currentFeedView.collectAsState(initial = FeedView.FeedList)
    val movieList by interactor.currentMovieList.collectAsState(initial = MovieList.NowPlaying())
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    BoxWithConstraints {
        val useRailNavigation = maxWidth >= 1100.dp

        MaterialTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (!useRailNavigation) {
                        SettingsBottomBar(
                            onFeedClick = onFeedClick,
                            onFaveClick = onFaveClick,
                            onSettingsClick = onSettingsClick
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) { outerPadding ->
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (useRailNavigation) {
                        SettingsNavigationRail(
                            onFeedClick = onFeedClick,
                            onFaveClick = onFaveClick,
                            onSettingsClick = onSettingsClick
                        )
                    }

                    SettingsScreenContent(
                        modifier = Modifier.weight(1F),
                        outerPadding = outerPadding,
                        themeData = themeData,
                        feedView = feedView,
                        movieList = movieList,
                        versionName = aboutInteractor.versionName,
                        versionCode = aboutInteractor.versionCode,
                        isThemeFeatureEnabled = uiInteractor.isThemeFeatureEnabled,
                        isFeedViewFeatureEnabled = uiInteractor.isFeedViewFeatureEnabled,
                        isMovieListFeatureEnabled = uiInteractor.isMovieListFeatureEnabled,
                        isGithubFeatureEnabled = uiInteractor.isGithubFeatureEnabled,
                        isTelegramFeatureEnabled = uiInteractor.isTelegramFeatureEnabled,
                        isAboutFeatureEnabled = uiInteractor.isAboutFeatureEnabled,
                        onSelectTheme = { theme ->
                            scope.launch { interactor.selectTheme(theme) }
                        },
                        onSelectFeedView = { localFeedView ->
                            scope.launch { interactor.selectFeedView(localFeedView) }
                        },
                        onSelectMovieList = { localMovieList ->
                            scope.launch { interactor.selectMovieList(localMovieList) }
                        },
                        onGithubClick = { uriHandler.openUri(MOVIES_GITHUB_URL) },
                        onTelegramClick = { uriHandler.openUri(MOVIES_TELEGRAM_URL) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsScreenContent(
    modifier: Modifier,
    outerPadding: PaddingValues,
    themeData: ThemeData,
    feedView: FeedView,
    movieList: MovieList,
    versionName: String,
    versionCode: Long,
    isThemeFeatureEnabled: Boolean,
    isFeedViewFeatureEnabled: Boolean,
    isMovieListFeatureEnabled: Boolean,
    isGithubFeatureEnabled: Boolean,
    isTelegramFeatureEnabled: Boolean,
    isAboutFeatureEnabled: Boolean,
    onSelectTheme: (AppTheme) -> Unit,
    onSelectFeedView: (FeedView) -> Unit,
    onSelectMovieList: (MovieList) -> Unit,
    onGithubClick: () -> Unit,
    onTelegramClick: () -> Unit
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = rememberTopAppBarState(),
        canScroll = { true }
    )
    val layoutDirection = LocalLayoutDirection.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val visibleItems = remember(
        isThemeFeatureEnabled,
        isMovieListFeatureEnabled,
        isFeedViewFeatureEnabled,
        isGithubFeatureEnabled,
        isTelegramFeatureEnabled
    ) {
        buildList {
            if (isThemeFeatureEnabled) {
                add(SettingsListItem.Theme)
            }
            if (isMovieListFeatureEnabled) {
                add(SettingsListItem.MovieList)
            }
            if (isFeedViewFeatureEnabled) {
                add(SettingsListItem.Appearance)
            }
            if (isGithubFeatureEnabled) {
                add(SettingsListItem.Github)
            }
            if (isTelegramFeatureEnabled) {
                add(SettingsListItem.Telegram)
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxWidth()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(
                        text = settingsTitleText
                    )
                },
                modifier = Modifier.clickable {
                    scope.launch { listState.animateScrollToItem(0) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.inversePrimary
                ),
                windowInsets = WindowInsets(0, 0, 0, 0),
                scrollBehavior = topAppBarScrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                top = innerPadding.calculateTopPadding() + 16.dp,
                end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
                bottom = innerPadding.calculateBottomPadding() +
                    outerPadding.calculateBottomPadding() +
                    16.dp
            )
        ) {
            itemsIndexed(visibleItems) { index, item ->
                when (item) {
                    SettingsListItem.Theme -> SettingsThemeItem(
                        itemShape = itemShape(
                            currentItem = item,
                            items = visibleItems
                        ),
                        currentTheme = themeData.appTheme,
                        onSelectTheme = onSelectTheme
                    )
                    SettingsListItem.MovieList -> SettingsMovieListItem(
                        itemShape = itemShape(
                            currentItem = item,
                            items = visibleItems
                        ),
                        currentMovieList = movieList,
                        onSelectMovieList = onSelectMovieList
                    )
                    SettingsListItem.Appearance -> SettingsAppearanceItem(
                        itemShape = itemShape(
                            currentItem = item,
                            items = visibleItems
                        ),
                        currentFeedView = feedView,
                        onSelectFeedView = onSelectFeedView
                    )
                    SettingsListItem.Github -> SettingsActionItem(
                        itemShape = itemShape(
                            currentItem = item,
                            items = visibleItems
                        ),
                        icon = Icons.Outlined.Code,
                        title = settingsGithubText,
                        subtitle = settingsGithubDescriptionText,
                        onClick = onGithubClick
                    )
                    SettingsListItem.Telegram -> SettingsActionItem(
                        itemShape = itemShape(
                            currentItem = item,
                            items = visibleItems
                        ),
                        icon = Icons.AutoMirrored.Outlined.Send,
                        title = settingsTelegramText,
                        subtitle = settingsTelegramDescriptionText,
                        onClick = onTelegramClick
                    )
                }

                if (index != visibleItems.lastIndex) {
                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )
                }
            }

            if (isAboutFeatureEnabled) {
                item {
                    AboutItem(
                        versionName = versionName,
                        versionCode = versionCode
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsThemeItem(
    itemShape: RoundedCornerShape,
    currentTheme: AppTheme,
    onSelectTheme: (AppTheme) -> Unit
) {
    var dialogVisible by remember { mutableStateOf(false) }

    if (dialogVisible) {
        SettingsSelectionDialog(
            icon = Icons.Outlined.Palette,
            title = settingsThemeText,
            items = AppTheme.VALUES,
            currentItem = currentTheme,
            itemText = { theme -> theme.stringText() },
            onItemSelect = onSelectTheme,
            onDismissRequest = { dialogVisible = false }
        )
    }

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(itemShape)
            .clickable { dialogVisible = true },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
        headlineContent = {
            Text(
                text = settingsThemeText,
                style = MaterialTheme.typography.titleLarge
            )
        },
        supportingContent = {
            Text(
                text = currentTheme.stringText(),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Palette,
                contentDescription = null,
                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
            )
        }
    )
}

@Composable
private fun SettingsMovieListItem(
    itemShape: RoundedCornerShape,
    currentMovieList: MovieList,
    onSelectMovieList: (MovieList) -> Unit
) {
    var dialogVisible by remember { mutableStateOf(false) }

    if (dialogVisible) {
        SettingsSelectionDialog(
            icon = Icons.Outlined.LocalMovies,
            title = settingsMovieListText,
            items = MovieList.VALUES,
            currentItem = currentMovieList,
            itemText = { it.stringText() },
            onItemSelect = onSelectMovieList,
            onDismissRequest = { dialogVisible = false }
        )
    }

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(itemShape)
            .clickable { dialogVisible = true },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
        headlineContent = {
            Text(
                text = settingsMovieListText,
                style = MaterialTheme.typography.titleLarge
            )
        },
        supportingContent = {
            Text(
                text = currentMovieList.stringText(),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.LocalMovies,
                contentDescription = null,
                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
            )
        }
    )
}

@Composable
private fun SettingsAppearanceItem(
    itemShape: RoundedCornerShape,
    currentFeedView: FeedView,
    onSelectFeedView: (FeedView) -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(itemShape),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
        headlineContent = {
            Text(
                text = settingsAppearanceText,
                style = MaterialTheme.typography.titleLarge
            )
        },
        supportingContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
            ) {
                ToggleButton(
                    checked = currentFeedView == FeedView.FeedList,
                    onCheckedChange = { onSelectFeedView(FeedView.FeedList) },
                    modifier = Modifier
                        .weight(1F)
                        .height(40.dp),
                    shapes = ButtonGroupDefaults.connectedLeadingButtonShapes()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ViewAgenda,
                        contentDescription = null,
                        modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                    )

                    Spacer(
                        modifier = Modifier.size(ToggleButtonDefaults.IconSpacing)
                    )

                    Text(
                        text = settingsAppearanceListText
                    )
                }

                ToggleButton(
                    checked = currentFeedView == FeedView.FeedGrid,
                    onCheckedChange = { onSelectFeedView(FeedView.FeedGrid) },
                    modifier = Modifier
                        .weight(1F)
                        .height(40.dp),
                    shapes = ButtonGroupDefaults.connectedTrailingButtonShapes()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Dashboard,
                        contentDescription = null,
                        modifier = Modifier.size(IconButtonDefaults.smallIconSize)
                    )

                    Spacer(
                        modifier = Modifier.size(ToggleButtonDefaults.IconSpacing)
                    )

                    Text(
                        text = settingsAppearanceGridText
                    )
                }
            }
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.GridView,
                contentDescription = null,
                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
            )
        }
    )
}

@Composable
private fun SettingsActionItem(
    itemShape: RoundedCornerShape,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clip(itemShape)
            .clickable(onClick = onClick),
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.inversePrimary),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconButtonDefaults.smallIconSize)
            )
        }
    )
}

@Composable
private fun AboutItem(
    versionName: String,
    versionCode: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.MovieFilter,
            contentDescription = null,
            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "$aboutAppNameText v$versionName",
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Text(
            text = "($versionCode)",
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.primary
            )
        )

        Text(
            text = aboutFlavorText,
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Text(
            text = aboutDebugText,
            modifier = Modifier.padding(start = 2.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }
}

@Composable
private fun <T: SealedString> SettingsSelectionDialog(
    icon: ImageVector,
    title: String,
    items: List<T>,
    currentItem: T,
    itemText: (T) -> String,
    onItemSelect: (T) -> Unit,
    onDismissRequest: () -> Unit
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(
                    text = settingsActionCancelText,
                    style = MaterialTheme.typography.titleMediumEmphasized.copy(textAlign = TextAlign.Center)
                )
            }
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconButtonDefaults.largeIconSize)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                items.forEachIndexed { index, item ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                dialogItemShape(
                                    index = index,
                                    lastIndex = items.lastIndex
                                )
                            )
                            .clickable {
                                onItemSelect(item)
                                onDismissRequest()
                            },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
                        headlineContent = {
                            Text(
                                text = itemText(item),
                                style = MaterialTheme.typography.titleMediumEmphasized
                            )
                        },
                        leadingContent = {
                            RadioButton(
                                selected = currentItem == item,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = .6F)
                                )
                            )
                        }
                    )

                    if (index != items.lastIndex) {
                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )
                    }
                }
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun SettingsNavigationRail(
    onFeedClick: () -> Unit,
    onFaveClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val railItems = settingsRailItems(
        onFeedClick = onFeedClick,
        onFaveClick = onFaveClick,
        onSettingsClick = onSettingsClick
    )

    NavigationRail(
        modifier = Modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Spacer(
            modifier = Modifier.weight(1F)
        )

        railItems.forEach { item ->
            NavigationRailItem(
                selected = item.selected,
                onClick = item.onClick,
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationRailItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.inversePrimary
                )
            )
        }

        Spacer(
            modifier = Modifier.weight(1F)
        )
    }
}

@Composable
private fun SettingsBottomBar(
    onFeedClick: () -> Unit,
    onFaveClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val railItems = settingsRailItems(
        onFeedClick = onFeedClick,
        onFaveClick = onFaveClick,
        onSettingsClick = onSettingsClick
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                railItems.forEach { item ->
                    SettingsBottomBarPill(
                        item = item
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsBottomBarPill(
    item: SettingsRailItem
) {
    Surface(
        color = when {
            item.selected -> MaterialTheme.colorScheme.inversePrimary
            else -> Color.Transparent
        },
        shape = RoundedCornerShape(22.dp),
        onClick = item.onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

private enum class SettingsListItem {
    Theme,
    MovieList,
    Appearance,
    Github,
    Telegram
}

private data class SettingsRailItem(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean = false,
    val onClick: () -> Unit = {}
)

private fun settingsRailItems(
    onFeedClick: () -> Unit,
    onFaveClick: () -> Unit,
    onSettingsClick: () -> Unit
): List<SettingsRailItem> {
    return listOf(
        SettingsRailItem(
            label = "Feed",
            icon = Icons.Outlined.GridView,
            onClick = onFeedClick
        ),
        SettingsRailItem(
            label = "Fave",
            icon = Icons.Outlined.Favorite,
            onClick = onFaveClick
        ),
        SettingsRailItem(
            label = "Settings",
            icon = Icons.Outlined.Settings,
            selected = true,
            onClick = onSettingsClick
        )
    )
}

@Composable
private fun itemShape(
    currentItem: SettingsListItem,
    items: List<SettingsListItem>
): RoundedCornerShape {
    return when {
        items.size == 1 -> middleLargeIncreasedListItemShape
        currentItem == items.first() -> topListItemShape
        currentItem == items.last() -> bottomListItemShape
        else -> middleExtraSmallListItemShape
    }
}

@Composable
private fun dialogItemShape(
    index: Int,
    lastIndex: Int
): RoundedCornerShape {
    return when {
        lastIndex == 0 -> middleLargeIncreasedListItemShape
        index == 0 -> topListItemShape
        index == lastIndex -> bottomListItemShape
        else -> middleExtraSmallListItemShape
    }
}

private fun SealedString.stringText(): String {
    return when (this) {
        is AppTheme.NightNo -> settingsThemeLightText
        is AppTheme.NightYes -> settingsThemeDarkText
        is AppTheme.FollowSystem -> settingsThemeSystemText
        is AppTheme.Amoled -> settingsThemeAmoledText
        is FeedView.FeedList -> settingsAppearanceListText
        is FeedView.FeedGrid -> settingsAppearanceGridText
        is MovieList.NowPlaying -> settingsMovieListNowPlayingText
        is MovieList.Popular -> settingsMovieListPopularText
        is MovieList.TopRated -> settingsMovieListTopRatedText
        is MovieList.Upcoming -> settingsMovieListUpcomingText
        else -> ""
    }
}

private const val settingsTitleText = "Settings"
private const val settingsActionCancelText = "Cancel"
private const val settingsThemeText = "Theme"
private const val settingsThemeSystemText = "Follow System"
private const val settingsThemeLightText = "Light"
private const val settingsThemeDarkText = "Dark"
private const val settingsThemeAmoledText = "Amoled"
private const val settingsAppearanceText = "Appearance"
private const val settingsAppearanceListText = "List"
private const val settingsAppearanceGridText = "Grid"
private const val settingsMovieListText = "List"
private const val settingsMovieListNowPlayingText = "Now Playing"
private const val settingsMovieListPopularText = "Popular"
private const val settingsMovieListTopRatedText = "Top Rated"
private const val settingsMovieListUpcomingText = "Upcoming"
private const val settingsGithubText = "GitHub"
private const val settingsGithubDescriptionText = "Check the Repository"
private const val settingsTelegramText = "Telegram"
private const val settingsTelegramDescriptionText = "Subscribe to Сhannel"
private const val aboutAppNameText = "Movies"
private const val aboutFlavorText = "Foss"
private const val aboutDebugText = "Debug"

private val topListItemShape: RoundedCornerShape
    @Composable get() = RoundedCornerShape(
        topStart = MaterialTheme.shapes.largeIncreased.topStart,
        topEnd = MaterialTheme.shapes.largeIncreased.topEnd,
        bottomStart = MaterialTheme.shapes.extraSmall.bottomStart,
        bottomEnd = MaterialTheme.shapes.extraSmall.bottomStart
    )

private val middleExtraSmallListItemShape: RoundedCornerShape
    @Composable get() = RoundedCornerShape(
        topStart = MaterialTheme.shapes.extraSmall.topStart,
        topEnd = MaterialTheme.shapes.extraSmall.topEnd,
        bottomStart = MaterialTheme.shapes.extraSmall.bottomStart,
        bottomEnd = MaterialTheme.shapes.extraSmall.bottomEnd
    )

private val middleLargeIncreasedListItemShape: RoundedCornerShape
    @Composable get() = RoundedCornerShape(
        topStart = MaterialTheme.shapes.largeIncreased.topStart,
        topEnd = MaterialTheme.shapes.largeIncreased.topEnd,
        bottomStart = MaterialTheme.shapes.largeIncreased.bottomStart,
        bottomEnd = MaterialTheme.shapes.largeIncreased.bottomEnd
    )

private val bottomListItemShape: RoundedCornerShape
    @Composable get() = RoundedCornerShape(
        topStart = MaterialTheme.shapes.extraSmall.topStart,
        topEnd = MaterialTheme.shapes.extraSmall.topEnd,
        bottomStart = MaterialTheme.shapes.largeIncreased.bottomStart,
        bottomEnd = MaterialTheme.shapes.largeIncreased.bottomEnd
    )
