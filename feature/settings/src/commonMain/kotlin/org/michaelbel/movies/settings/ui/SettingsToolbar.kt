@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.ui.accessibility.MoviesContentDescriptionCommon
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.clickableWithoutRipple
import org.michaelbel.movies.ui.ktx.modifierDisplayCutoutWindowInsets
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
internal fun SettingsToolbar(
    modifier: Modifier = Modifier,
    topAppBarScrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    isNavigationIconVisible: Boolean,
    onNavigationIconClick: () -> Unit,
    onClick: () -> Unit,
) {
    LargeTopAppBar(
        title = {
            Text(
                text = stringResource(MoviesStrings.settings_title)
            )
        },
        modifier = modifier.clickableWithoutRipple(onClick),
        navigationIcon = if (isNavigationIconVisible) {
            {
                IconButton(
                    onClick = onNavigationIconClick,
                    modifier = Modifier.then(modifierDisplayCutoutWindowInsets)
                ) {
                    Image(
                        imageVector = MoviesIcons.ArrowBack,
                        contentDescription = stringResource(MoviesContentDescriptionCommon.BackIcon),
                        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }
            }
        } else {
            {}
        },
        scrollBehavior = topAppBarScrollBehavior
    )
}

@Preview
@Composable
private fun SettingsToolbarPreview() {
    MoviesTheme {
        SettingsToolbar(
            modifier = Modifier.statusBarsPadding(),
            isNavigationIconVisible = true,
            onNavigationIconClick = {},
            onClick = {}
        )
    }
}
