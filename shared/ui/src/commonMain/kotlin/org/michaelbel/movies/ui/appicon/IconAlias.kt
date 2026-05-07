package org.michaelbel.movies.ui.appicon

import movies.shared.ui.generated.resources.Res
import movies.shared.ui.generated.resources.ic_launcher_icon_aqua
import movies.shared.ui.generated.resources.ic_launcher_icon_black
import movies.shared.ui.generated.resources.ic_launcher_icon_blue
import movies.shared.ui.generated.resources.ic_launcher_icon_blue_grey
import movies.shared.ui.generated.resources.ic_launcher_icon_brown
import movies.shared.ui.generated.resources.ic_launcher_icon_deep_orange
import movies.shared.ui.generated.resources.ic_launcher_icon_deep_purple
import movies.shared.ui.generated.resources.ic_launcher_icon_emerald
import movies.shared.ui.generated.resources.ic_launcher_icon_green
import movies.shared.ui.generated.resources.ic_launcher_icon_grey
import movies.shared.ui.generated.resources.ic_launcher_icon_lime
import movies.shared.ui.generated.resources.ic_launcher_icon_orange
import movies.shared.ui.generated.resources.ic_launcher_icon_pink
import movies.shared.ui.generated.resources.ic_launcher_icon_purple
import movies.shared.ui.generated.resources.ic_launcher_icon_red
import movies.shared.ui.generated.resources.ic_launcher_icon_red_dark
import movies.shared.ui.generated.resources.ic_launcher_icon_rose
import movies.shared.ui.generated.resources.ic_launcher_icon_sand
import movies.shared.ui.generated.resources.ic_launcher_icon_sky
import movies.shared.ui.generated.resources.ic_launcher_icon_teal
import movies.shared.ui.generated.resources.ic_launcher_icon_yellow
import org.jetbrains.compose.resources.DrawableResource

sealed class IconAlias(
    val key: String,
    val iconRes: DrawableResource,
    val title: String
) {

    data object Red: IconAlias(
        key = "RedIcon",
        iconRes = Res.drawable.ic_launcher_icon_red,
        title = "Red"
    )

    data object RedDark: IconAlias(
        key = "RedDarkIcon",
        iconRes = Res.drawable.ic_launcher_icon_red_dark,
        title = "RedDark"
    )

    data object Pink: IconAlias(
        key = "PinkIcon",
        iconRes = Res.drawable.ic_launcher_icon_pink,
        title = "Pink"
    )

    data object Rose: IconAlias(
        key = "RoseIcon",
        iconRes = Res.drawable.ic_launcher_icon_rose,
        title = "Rose"
    )

    data object Purple: IconAlias(
        key = "PurpleIcon",
        iconRes = Res.drawable.ic_launcher_icon_purple,
        title = "Purple"
    )

    data object DeepPurple: IconAlias(
        key = "DeepPurpleIcon",
        iconRes = Res.drawable.ic_launcher_icon_deep_purple,
        title = "DeepPurple"
    )

    data object Blue: IconAlias(
        key = "BlueIcon",
        iconRes = Res.drawable.ic_launcher_icon_blue,
        title = "Blue"
    )

    data object Sky: IconAlias(
        key = "SkyIcon",
        iconRes = Res.drawable.ic_launcher_icon_sky,
        title = "Sky"
    )

    data object Teal: IconAlias(
        key = "TealIcon",
        iconRes = Res.drawable.ic_launcher_icon_teal,
        title = "Teal"
    )

    data object Aqua: IconAlias(
        key = "AquaIcon",
        iconRes = Res.drawable.ic_launcher_icon_aqua,
        title = "Aqua"
    )

    data object Green: IconAlias(
        key = "GreenIcon",
        iconRes = Res.drawable.ic_launcher_icon_green,
        title = "Green"
    )

    data object Emerald: IconAlias(
        key = "EmeraldIcon",
        iconRes = Res.drawable.ic_launcher_icon_emerald,
        title = "Emerald"
    )

    data object Lime: IconAlias(
        key = "LimeIcon",
        iconRes = Res.drawable.ic_launcher_icon_lime,
        title = "Lime"
    )

    data object Yellow: IconAlias(
        key = "YellowIcon",
        iconRes = Res.drawable.ic_launcher_icon_yellow,
        title = "Yellow"
    )

    data object Orange: IconAlias(
        key = "OrangeIcon",
        iconRes = Res.drawable.ic_launcher_icon_orange,
        title = "Orange"
    )

    data object DeepOrange: IconAlias(
        key = "DeepOrangeIcon",
        iconRes = Res.drawable.ic_launcher_icon_deep_orange,
        title = "DeepOrange"
    )

    data object Brown: IconAlias(
        key = "BrownIcon",
        iconRes = Res.drawable.ic_launcher_icon_brown,
        title = "Brown"
    )

    data object Sand: IconAlias(
        key = "SandIcon",
        iconRes = Res.drawable.ic_launcher_icon_sand,
        title = "Sand"
    )

    data object Grey: IconAlias(
        key = "GreyIcon",
        iconRes = Res.drawable.ic_launcher_icon_grey,
        title = "Grey"
    )

    data object BlueGrey: IconAlias(
        key = "BlueGreyIcon",
        iconRes = Res.drawable.ic_launcher_icon_blue_grey,
        title = "BlueGrey"
    )

    data object Black: IconAlias(
        key = "BlackIcon",
        iconRes = Res.drawable.ic_launcher_icon_black,
        title = "Black"
    )

    companion object {
        val VALUES = listOf(
            Red,
            RedDark,
            Pink,
            Rose,
            Purple,
            DeepPurple,
            Blue,
            Sky,
            Teal,
            Aqua,
            Green,
            Emerald,
            Lime,
            Yellow,
            Orange,
            DeepOrange,
            Brown,
            Sand,
            Grey,
            BlueGrey,
            Black
        )
    }
}
