@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.settings.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.common.SealedString
import org.michaelbel.movies.interactor.entity.AppLanguage
import org.michaelbel.movies.settings.ktx.stringText
import org.michaelbel.movies.ui.accessibility.MoviesContentDescriptionCommon
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.preview.AppearancePreviewParameterProvider
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun <T: SealedString> SettingsDialog(
    icon: ImageVector,
    title: String,
    items: List<T>,
    currentItem: T,
    onItemSelect: (T) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(
                    text = stringResource(MoviesStrings.settings_action_cancel),
                    style = MaterialTheme.typography.titleMediumEmphasized.copy(textAlign = TextAlign.Center)
                )
            }
        },
        icon = {
            Icon(
                imageVector = icon,
                modifier = Modifier.size(IconButtonDefaults.largeIconSize),
                contentDescription = MoviesContentDescriptionCommon.None
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier.verticalScroll(scrollState)
            ) {
                items.forEachIndexed { index, item ->
                    val itemShape = when {
                        items.size == 1 -> RoundedCornerShape(16.dp)
                        index == 0 -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                        index == items.lastIndex -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        else -> RoundedCornerShape(4.dp)
                    }

                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(itemShape)
                            .clickable {
                                onItemSelect(item)
                                onDismissRequest()
                            },
                        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
                        headlineContent = {
                            Text(
                                text = item.stringText,
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
        containerColor = MaterialTheme.colorScheme.surface,
        iconContentColor = MaterialTheme.colorScheme.secondary,
        titleContentColor = MaterialTheme.colorScheme.onSurface
    )
}

@Preview
@Composable
private fun SettingDialogPreview(
    @PreviewParameter(AppearancePreviewParameterProvider::class) appLanguage: AppLanguage
) {
    MoviesTheme {
        SettingsDialog(
            icon = MoviesIcons.Language,
            title = stringResource(MoviesStrings.settings_language),
            items = AppLanguage.VALUES,
            currentItem = appLanguage,
            onItemSelect = {},
            onDismissRequest = {}
        )
    }
}
