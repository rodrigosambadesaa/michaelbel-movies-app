@file:OptIn(ExperimentalTime::class)

package org.michaelbel.movies.repository.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.michaelbel.movies.common.exceptions.AccountDetailsException
import org.michaelbel.movies.network.AccountNetworkService
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
import org.michaelbel.movies.persistence.database.ktx.accountPojo
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.datastore.MoviesPreferences
import org.michaelbel.movies.repository.AccountRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AccountRepositoryImpl(
    private val accountNetworkService: AccountNetworkService,
    private val repositoryWebStore: RepositoryWebStore,
    private val preferences: MoviesPreferences
): AccountRepository {

    override val accountPojoFlow: Flow<AccountPojo> = combine(
        preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceAccountKey)
            .map(Int?::orEmpty),
        repositoryWebStore.accountFlow()
    ) { accountId, accountPojo ->
        when (accountPojo.accountId == accountId) {
            true -> accountPojo
            false -> AccountPojo.Empty
        }
    }

    override suspend fun accountId(): Int {
        return preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceAccountKey).orEmpty()
    }

    override suspend fun accountExpireTime(): Long {
        return preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceAccountExpireTimeKey).orEmpty()
    }

    override suspend fun accountDetails() {
        try {
            val sessionId = preferences.getValue(MoviesPreferences.PreferenceKey.PreferenceSessionIdKey).orEmpty()
            val accountPojo = accountNetworkService.accountDetails(sessionId).accountPojo
            preferences.run {
                setValue(MoviesPreferences.PreferenceKey.PreferenceAccountKey, accountPojo.accountId)
                setValue(MoviesPreferences.PreferenceKey.PreferenceAccountExpireTimeKey, Clock.System.now().toEpochMilliseconds())
            }
            repositoryWebStore.updateAccount(accountPojo)
        } catch (_: Exception) {
            throw AccountDetailsException()
        }
    }
}
