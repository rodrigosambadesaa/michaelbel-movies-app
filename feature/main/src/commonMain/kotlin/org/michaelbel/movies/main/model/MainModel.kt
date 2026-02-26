package org.michaelbel.movies.main.model

import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.mvi.model.Model

data class MainModel(
    val splashLoading: Boolean = true,
    val themeData: ThemeData = ThemeData.Default,
    val isScreenshotBlockEnabled: Boolean = false
): Model
