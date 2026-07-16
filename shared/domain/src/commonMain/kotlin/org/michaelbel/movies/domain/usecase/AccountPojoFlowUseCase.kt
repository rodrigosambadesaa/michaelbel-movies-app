@file:OptIn(ExperimentalCoroutinesApi::class)

package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.dao.AccountDao
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
import org.michaelbel.movies.persistence.database.ktx.orEmpty
import org.michaelbel.movies.persistence.datastore.MoviesPreferences

class AccountPojoFlowUseCase(
    private val accountDao: AccountDao,
    private val preferences: MoviesPreferences,
    dispatchers: SharedDispatchers
): FlowUseCase<Unit, AccountPojo>(dispatchers.io) {

    override fun execute(params: Unit): Flow<AccountPojo> {
        val preferenceAccountKeyFlow = preferences.getValueFlow(MoviesPreferences.PreferenceKey.PreferenceAccountKey)
        return preferenceAccountKeyFlow
            .map { accountId -> accountId.orEmpty() }
            .flatMapLatest { accountId ->
                accountDao.selectFlow(accountId).map { it ?: AccountPojo.Empty }
            }
    }
}
