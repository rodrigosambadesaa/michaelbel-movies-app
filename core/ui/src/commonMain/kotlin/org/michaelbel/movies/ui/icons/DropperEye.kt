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
import org.michaelbel.movies.ui.theme.MoviesTheme

val MoviesIcons.DropperEye: ImageVector
    get() {
        if (_dropperEye != null) {
            return _dropperEye!!
        }
        _dropperEye = ImageVector.Builder(
            name = "DropperEye",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960F,
            viewportHeight = 960F
        ).apply {
            addPath(
                pathData = PathParser().parsePathString("m644 347 100-94-37-37-96 96 33 35Zm0 0-33-35 33 35ZM120 840v-190l357-357-57-57 57-56 77 76 124-124q5-5 12.5-8t15.5-3q8 0 15 3t13 8l94 94q5 6 8 13t3 15q0 8-3 15.5t-8 12.5L704 406l56 58-56 56-170-170-334 333v77h42q10 22 21.5 42t24.5 38H120Zm480 80q-91 0-168-48T320 740q35-84 112-132t168-48q91 0 168 48t112 132q-35 84-112 132T600 920Zm107.5-106q50.5-26 82.5-74-32-48-82.5-74T600 640q-57 0-107.5 26T410 740q32 48 82.5 74T600 840q57 0 107.5-26Zm-150-31.5Q540 765 540 740t17.5-42.5Q575 680 600 680t42.5 17.5Q660 715 660 740t-17.5 42.5Q625 800 600 800t-42.5-17.5Z").toNodes(),
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
        return _dropperEye!!
    }

private var _dropperEye: ImageVector? = null

@Preview
@Composable
private fun DropperEyeIconPreview() {
    MoviesTheme {
        Icon(
            imageVector = MoviesIcons.DropperEye,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.White
        )
    }
}
