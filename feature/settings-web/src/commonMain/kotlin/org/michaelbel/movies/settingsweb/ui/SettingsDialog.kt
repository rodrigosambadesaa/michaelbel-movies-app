@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.settingsweb.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import org.michaelbel.movies.common.SealedString
import org.michaelbel.movies.settingsweb.ktx.stringText

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
                    text = settingsActionCancelText,
                    style = MaterialTheme.typography.titleMediumEmphasized.copy(
                        textAlign = TextAlign.Center
                    )
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
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(items) { index, item ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                settingsListItemShape(
                                    isFirst = index == 0,
                                    isLast = index == items.lastIndex
                                )
                            )
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
                                    unselectedColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                        alpha = .6F
                                    )
                                )
                            )
                        }
                    )
                }
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

private const val settingsActionCancelText = "Cancel"
