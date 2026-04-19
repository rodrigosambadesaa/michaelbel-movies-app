package org.michaelbel.movies.ui.icons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.michaelbel.movies.ui.theme.AppTheme

val MoviesIcons.TileSmall: ImageVector
    get() {
        if (_tileSmall != null) {
            return _tileSmall!!
        }
        _tileSmall = ImageVector.Builder(
            name = "TileSmall",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960F,
            viewportHeight = 960F
        ).apply {
            addPath(
                pathData = PathParser().parsePathString("M120 720v-160q0-17 11.5-28.5T160 520h240q17 0 28.5 11.5T440 560v160q0 17-11.5 28.5T400 760H160q-17 0-28.5-11.5T120 720Zm400 0v-160q0-17 11.5-28.5T560 520h240q17 0 28.5 11.5T840 560v160q0 17-11.5 28.5T800 760H560q-17 0-28.5-11.5T520 720ZM120 400v-160q0-17 11.5-28.5T160 200h240q17 0 28.5 11.5T440 240v160q0 17-11.5 28.5T400 440H160q-17 0-28.5-11.5T120 400Zm400 0v-160q0-17 11.5-28.5T560 200h240q17 0 28.5 11.5T840 240v160q0 17-11.5 28.5T800 440H560q-17 0-28.5-11.5T520 400Z").toNodes(),
                fill = SolidColor(Color.Black),
                fillAlpha = 1F,
                stroke = null,
                strokeAlpha = 1F,
                strokeLineWidth = 1F,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1F,
                pathFillType = PathFillType.NonZero
            )
        }.build()
        return _tileSmall!!
    }

private var _tileSmall: ImageVector? = null

@Preview
@Composable
private fun TileSmallIconPreview() {
    AppTheme {
        Icon(
            imageVector = MoviesIcons.TileSmall,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.White
        )
    }
}
