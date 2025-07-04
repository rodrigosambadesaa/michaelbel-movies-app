package org.michaelbel.movies.persistence.database

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
import org.michaelbel.movies.persistence.database.ktx.accountDb
import org.michaelbel.movies.persistence.database.typealiases.AccountId

class AccountPersistence internal constructor(
    private val moviesDatabase: MoviesDatabase
) {

    fun accountByIdFlow(accountId: AccountId): Flow<AccountPojo> {
        return moviesDatabase.accountDao.selectFlow(accountId).map { it ?: AccountPojo.Empty }
    }

    suspend fun upsert(account: AccountPojo) {
        moviesDatabase.accountDao.upsert(account.accountDb)
    }

    suspend fun removeById(accountId: AccountId) {
        moviesDatabase.accountDao.removeById(accountId)
    }
}