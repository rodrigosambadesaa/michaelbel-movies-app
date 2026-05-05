package org.michaelbel.movies.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MoviesIcons.SettingsAccountBox: ImageVector
    get() {
        if (_settingsAccountBox != null) {
            return _settingsAccountBox!!
        }

        _settingsAccountBox = ImageVector.Builder(
            name = "settingsAccountBox24dpE3E3E3FILL0Wght400GRAD0Opsz24",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFe3e3e3))
            ) {
                moveTo(600f, 840f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(-33f)
                quadToRelative(-25f, -23f, -56f, -35f)
                reflectiveQuadToRelative(-64f, -12f)
                quadToRelative(-33f, 0f, -64f, 12f)
                reflectiveQuadToRelative(-56f, 35f)
                verticalLineToRelative(33f)
                close()
                moveToRelative(162.5f, -137.5f)
                quadTo(780f, 685f, 780f, 660f)
                reflectiveQuadToRelative(-17.5f, -42.5f)
                quadTo(745f, 600f, 720f, 600f)
                reflectiveQuadToRelative(-42.5f, 17.5f)
                quadTo(660f, 635f, 660f, 660f)
                reflectiveQuadToRelative(17.5f, 42.5f)
                quadTo(695f, 720f, 720f, 720f)
                reflectiveQuadToRelative(42.5f, -17.5f)
                close()
                moveTo(480f, 480f)
                close()
                moveToRelative(2f, -140f)
                quadToRelative(-58f, 0f, -99f, 41f)
                reflectiveQuadToRelative(-41f, 99f)
                quadToRelative(0f, 48f, 27f, 84f)
                reflectiveQuadToRelative(71f, 50f)
                quadToRelative(0f, -23f, 0.5f, -44f)
                reflectiveQuadToRelative(8.5f, -38f)
                quadToRelative(-14f, -8f, -20.5f, -22f)
                reflectiveQuadToRelative(-6.5f, -30f)
                quadToRelative(0f, -25f, 17.5f, -42.5f)
                reflectiveQuadTo(482f, 420f)
                quadToRelative(15f, 0f, 28.5f, 7.5f)
                reflectiveQuadTo(533f, 448f)
                quadToRelative(11f, -5f, 23f, -7f)
                reflectiveQuadToRelative(24f, -2f)
                horizontalLineToRelative(36f)
                quadToRelative(-13f, -43f, -49.5f, -71f)
                reflectiveQuadTo(482f, 340f)
                close()
                moveTo(370f, 880f)
                lineToRelative(-16f, -128f)
                quadToRelative(-13f, -5f, -24.5f, -12f)
                reflectiveQuadTo(307f, 725f)
                lineToRelative(-119f, 50f)
                lineTo(78f, 585f)
                lineToRelative(103f, -78f)
                quadToRelative(-1f, -7f, -1f, -13.5f)
                verticalLineToRelative(-27f)
                quadToRelative(0f, -6.5f, 1f, -13.5f)
                lineTo(78f, 375f)
                lineToRelative(110f, -190f)
                lineToRelative(119f, 50f)
                quadToRelative(11f, -8f, 23f, -15f)
                reflectiveQuadToRelative(24f, -12f)
                lineToRelative(16f, -128f)
                horizontalLineToRelative(220f)
                lineToRelative(16f, 128f)
                quadToRelative(13f, 5f, 24.5f, 12f)
                reflectiveQuadToRelative(22.5f, 15f)
                lineToRelative(119f, -50f)
                lineToRelative(110f, 190f)
                lineToRelative(-85f, 65f)
                horizontalLineTo(696f)
                quadToRelative(-1f, -5f, -2f, -10.5f)
                reflectiveQuadToRelative(-3f, -10.5f)
                lineToRelative(86f, -65f)
                lineToRelative(-39f, -68f)
                lineToRelative(-99f, 42f)
                quadToRelative(-22f, -23f, -48.5f, -38.5f)
                reflectiveQuadTo(533f, 266f)
                lineToRelative(-13f, -106f)
                horizontalLineToRelative(-79f)
                lineToRelative(-14f, 106f)
                quadToRelative(-31f, 8f, -57.5f, 23.5f)
                reflectiveQuadTo(321f, 327f)
                lineToRelative(-99f, -41f)
                lineToRelative(-39f, 68f)
                lineToRelative(86f, 64f)
                quadToRelative(-5f, 15f, -7f, 30f)
                reflectiveQuadToRelative(-2f, 32f)
                quadToRelative(0f, 16f, 2f, 31f)
                reflectiveQuadToRelative(7f, 30f)
                lineToRelative(-86f, 65f)
                lineToRelative(39f, 68f)
                lineToRelative(99f, -42f)
                quadToRelative(24f, 25f, 54f, 42f)
                reflectiveQuadToRelative(65f, 22f)
                verticalLineToRelative(184f)
                horizontalLineToRelative(-70f)
                close()
                moveToRelative(210f, 40f)
                quadToRelative(-25f, 0f, -42.5f, -17.5f)
                reflectiveQuadTo(520f, 860f)
                verticalLineToRelative(-280f)
                quadToRelative(0f, -25f, 17.5f, -42.5f)
                reflectiveQuadTo(580f, 520f)
                horizontalLineToRelative(280f)
                quadToRelative(25f, 0f, 42.5f, 17.5f)
                reflectiveQuadTo(920f, 580f)
                verticalLineToRelative(280f)
                quadToRelative(0f, 25f, -17.5f, 42.5f)
                reflectiveQuadTo(860f, 920f)
                horizontalLineTo(580f)
                close()
            }
        }.build()

        return _settingsAccountBox!!
    }

private var _settingsAccountBox: ImageVector? = null
