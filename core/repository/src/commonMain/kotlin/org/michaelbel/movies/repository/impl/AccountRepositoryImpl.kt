@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)

package org.michaelbel.movies.repository.impl

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.michaelbel.movies.common.exceptions.AccountDetailsException
import org.michaelbel.movies.network.AccountNetworkService
import org.michaelbel.movies.persistence.database.AccountPersistence
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
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

    override val accountPojoFlow: Flow<AccountPojo> = preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceAccountKey)
        .map { accountId -> accountId.orEmpty() }
        .flatMapLatest(accountPersistence::accountByIdFlow)

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
