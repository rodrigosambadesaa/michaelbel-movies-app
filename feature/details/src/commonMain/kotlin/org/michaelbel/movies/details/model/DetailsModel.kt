package org.michaelbel.movies.details.model

import kotlinx.coroutines.Job
import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.network.config.ScreenState
import org.michaelbel.movies.network.connectivity.NetworkStatus

data class DetailsModel(
    val appTheme: AppTheme = AppTheme.FollowSystem,
    val networkStatus: NetworkStatus = NetworkStatus.Unavailable,
    val detailsState: ScreenState = ScreenState.Loading,
    val isAuthorized: Boolean = false,
    val isFavorite: Boolean = false,
    val favoriteJob: Job? = null,
    val isDetailsGalleryFeatureEnabled: Boolean = false,
    val isDetailsShareFeatureEnabled: Boolean = false
): Model {

    val isFavoriteJobActive: Boolean
        get() = favoriteJob != null && favoriteJob.isActive
}
