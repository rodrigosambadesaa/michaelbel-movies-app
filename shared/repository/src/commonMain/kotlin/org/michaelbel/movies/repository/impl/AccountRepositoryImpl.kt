@file:OptIn(ExperimentalTime::class)

package org.michaelbel.movies.repository.impl

import org.michaelbel.movies.common.exceptions.AccountDetailsException
import org.michaelbel.movies.network.AccountNetworkService
import org.michaelbel.movies.persistence.database.AccountPersistence
import org.michaelbel.movies.persistence.database.ktx.accountPojo
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.datastore.MoviesPreferences
import org.michaelbel.movies.repository.AccountRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AccountRepositoryImpl(
    private val accountNetworkService: AccountNetworkService,
    private val accountPersistence: AccountPersistence,
    private val preferences: MoviesPreferences
): AccountRepository {

    override suspend fun accountId(): Int {
        return preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceAccountKey).orEmpty()
    }

    override suspend fun accountExpireTime(): Long {
        return preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceAccountExpireTimeKey).orEmpty()
    }

    override suspend fun accountDetails() {
        try {
            val sessionId = preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceSessionIdKey).orEmpty()
            val account = accountNetworkService.accountDetails(sessionId)
            preferences.run {
                setValue(MoviesPreferences.PreferenceKey.PreferenceAccountKey, account.id)
                setValue(MoviesPreferences.PreferenceKey.PreferenceAccountExpireTimeKey, Clock.System.now().toEpochMilliseconds())
            }
            accountPersistence.upsert(account.accountPojo)
        } catch (_: Exception) { throw AccountDetailsException() }
    }
}
