package org.michaelbel.movies.settings.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.ui.accessibility.MoviesContentDescription
import org.michaelbel.movies.ui.appicon.IconAlias
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.preview.IconAliasPreviewParameterProvider
import org.michaelbel.movies.ui.theme.AppTheme
import org.michaelbel.movies.ui.theme.middleLargeIncreasedListItemShape

@Composable
fun RowScope.SettingAppIcon(
    iconAlias: IconAlias,
    isEnabled: Boolean,
    onClick: (IconAlias) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerSize by animateDpAsState(
        targetValue = if (isEnabled) 28.dp else 0.dp,
        label = "containerSize"
    )
    val iconSize by animateDpAsState(
        targetValue = if (isEnabled) 16.dp else 0.dp,
        label = "iconSize"
    )

    Box(
        modifier = modifier
            .requiredSize(80.dp)
            .clip(middleLargeIncreasedListItemShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .clickable { onClick(iconAlias) }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .matchParentSize()
                .padding(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Image(
                painter = painterResource(iconAlias.iconRes),
                contentDescription = stringResource(MoviesContentDescription.AppIcon),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.Center)
                    .matchParentSize()
                    .padding(2.dp)
                    .graphicsLayer(scaleX = 1.35F, scaleY = 1.35F)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .size(containerSize)
        ) {
            Icon(
                imageVector = MoviesIcons.Check,
                contentDescription = MoviesContentDescription.None,
                modifier = Modifier
                    .size(iconSize)
                    .align(Alignment.Center),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview
@Composable
private fun SettingAppIconPreview(
    @PreviewParameter(IconAliasPreviewParameterProvider::class) iconAlias: IconAlias
) {
    AppTheme {
        Row {
            SettingAppIcon(
                iconAlias = iconAlias,
                isEnabled = true,
                onClick = {}
            )
        }
    }
}
