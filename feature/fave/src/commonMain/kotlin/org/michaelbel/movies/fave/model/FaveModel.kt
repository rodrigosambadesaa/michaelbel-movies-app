package org.michaelbel.movies.fave.model

import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.mvi.model.Model

data class FaveModel(
    val feedView: FeedView = FeedView.FeedList,
    val isPageFailureButtonVisible: Boolean = false
): Model
