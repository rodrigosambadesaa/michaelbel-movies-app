package org.michaelbel.movies.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import org.jetbrains.compose.resources.painterResource
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.theme.AppTheme
import org.michaelbel.movies.ui.theme.middleLargeIncreasedListItemShape

@Composable
fun AboutDialog(
    themeData: ThemeData,
    theme: AppTheme,
    versionName: String,
    versionCode: Long,
    onDismissRequest: () -> Unit
) {
    DialogWindow(
        onCloseRequest = onDismissRequest,
        state = DialogState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(280.dp, 180.dp)
        ),
        title = "About Movies",
        resizable = false,
        icon = painterResource(MoviesIcons.LauncherRed)
    ) {
        AppTheme(
            themeData = themeData,
            theme = theme,
            enableEdgeToEdge = { _,_ -> }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val listItemShape = middleLargeIncreasedListItemShape

                Surface(
                    shape = listItemShape,
                    shadowElevation = 12.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Image(
                        painter = painterResource(MoviesIcons.LauncherRed),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(listItemShape)
                            .graphicsLayer {
                                shadowElevation = 12.dp.toPx()
                                shape = listItemShape
                                clip = true
                            }
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Movies",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Version $versionName ($versionCode)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}
