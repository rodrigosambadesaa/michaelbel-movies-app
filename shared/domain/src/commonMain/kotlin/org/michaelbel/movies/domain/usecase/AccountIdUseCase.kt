package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.database.typealiases.AccountId
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class AccountIdUseCase(
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): UseCase<Unit, AccountId>(dispatchers.io) {

    override suspend fun execute(params: Unit): AccountId {
        return preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceAccountKey).orEmpty()
    }
}
