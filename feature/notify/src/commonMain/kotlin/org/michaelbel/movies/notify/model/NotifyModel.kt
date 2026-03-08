package org.michaelbel.movies.notify.model

import org.michaelbel.movies.common.mvi.model.Model

data class NotifyModel(
    val isNotificationsFeatureEnabled: Boolean = false
): Model
