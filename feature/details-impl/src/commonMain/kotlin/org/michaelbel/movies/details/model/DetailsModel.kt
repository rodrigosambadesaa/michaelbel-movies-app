package org.michaelbel.movies.details.model

import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.common.theme.AppTheme
import org.michaelbel.movies.network.config.ScreenState
import org.michaelbel.movies.network.connectivity.NetworkStatus

data class DetailsModel(
    val appTheme: AppTheme = AppTheme.FollowSystem,
    val networkStatus: NetworkStatus = NetworkStatus.Unavailable,
    val detailsState: ScreenState = ScreenState.Loading
): Model