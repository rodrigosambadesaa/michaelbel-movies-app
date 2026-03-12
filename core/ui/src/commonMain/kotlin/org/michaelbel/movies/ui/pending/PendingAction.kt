package org.michaelbel.movies.ui.pending

import org.michaelbel.movies.persistence.database.typealiases.MovieId

sealed interface PendingAction {
    data object OpenFave: PendingAction
    data class AddFavorite(val movieId: MovieId): PendingAction
}
