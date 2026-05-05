package org.michaelbel.movies.details.model

import kotlinx.coroutines.Job
import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.network.config.ScreenState
import org.michaelbel.movies.network.connectivity.NetworkStatus

data class DetailsModel(
    val networkStatus: NetworkStatus = NetworkStatus.Unavailable,
    val detailsState: ScreenState = ScreenState.Loading,
    val isAuthorized: Boolean = false,
    val isFavorite: Boolean = false,
    val favoriteJob: Job? = null,
    val isDetailsFavoriteFeatureEnabled: Boolean = false,
    val isDetailsGalleryFeatureEnabled: Boolean = false,
    val isDetailsShareFeatureEnabled: Boolean = false
): Model {

    val isFavoriteJobActive: Boolean
        get() = favoriteJob != null && favoriteJob.isActive
}
