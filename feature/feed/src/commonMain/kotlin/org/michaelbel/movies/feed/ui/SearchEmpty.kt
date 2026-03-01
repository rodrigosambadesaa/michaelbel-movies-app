@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.feed.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.ui.accessibility.MoviesContentDescriptionCommon
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun SearchEmpty(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(112.dp),
            contentAlignment = Alignment.Center
        ) {
            MorphingPolygonBackground(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize()
            )

            Icon(
                imageVector = MoviesIcons.ManageSearch,
                contentDescription = MoviesContentDescriptionCommon.None,
                modifier = Modifier.size(54.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = stringResource(MoviesStrings.search_results_empty),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, top = 12.dp, end = 16.dp),
            style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        )
    }
}

@Composable
private fun MorphingPolygonBackground(
    color: Color,
    modifier: Modifier = Modifier
) {
    val shapes = remember {
        listOf(
            MaterialShapes.ClamShell,
            MaterialShapes.Pentagon,
            MaterialShapes.Cookie4Sided
        )
    }
    val morphs = remember(shapes) {
        listOf(
            Morph(start = shapes[0], end = shapes[1]),
            Morph(start = shapes[1], end = shapes[2]),
            Morph(start = shapes[2], end = shapes[0])
        )
    }
    val holdDurationMillis = 380
    val morphDurationMillis = 920
    val totalDurationMillis = (holdDurationMillis + morphDurationMillis) * morphs.size
    val transition = rememberInfiniteTransition(label = "search_empty_polygon_morph")
    val progress = transition.animateFloat(
        initialValue = 0F,
        targetValue = morphs.size.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = totalDurationMillis
                0F at 0
                0F at holdDurationMillis
                1F at holdDurationMillis + morphDurationMillis using LinearEasing
                1F at holdDurationMillis + morphDurationMillis + holdDurationMillis
                2F at (holdDurationMillis + morphDurationMillis) * 2 using LinearEasing
                2F at (holdDurationMillis + morphDurationMillis) * 2 + holdDurationMillis
                3F at totalDurationMillis using LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "search_empty_polygon_progress"
    ).value
    val cycleProgress = if (progress >= morphs.size) 0F else progress
    val morphIndex = when {
        cycleProgress < 1F -> 0
        cycleProgress < 2F -> 1
        else -> 2
    }
    val morphProgress = cycleProgress - morphIndex
    val path = remember { Path() }

    Canvas(
        modifier = modifier
    ) {
        morphs[morphIndex].toPath(progress = morphProgress, path = path)
        path.transform(Matrix().apply { scale(x = size.width, y = size.height) })
        path.translate(size.center - path.getBounds().center)
        drawPath(path = path, color = color)
    }
}

@Preview
@Composable
private fun SearchEmptyPreview() {
    MoviesTheme {
        SearchEmpty(
            modifier = Modifier.fillMaxSize()
        )
    }
}
