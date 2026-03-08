package org.michaelbel.movies.main.tabs.model

import org.michaelbel.movies.common.mvi.model.Model

data class MainTabsModel(
    val isFaveFeatureEnabled: Boolean = false,
    val isAuthorized: Boolean = false
): Model
