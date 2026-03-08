package org.michaelbel.movies.debug.model

import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.mvi.model.Model

data class DebugModel(
    val themeData: ThemeData = ThemeData.Default,
    val firebaseToken: String = "",
    val isFirebaseTokenFeatureEnabled: Boolean = false
): Model
