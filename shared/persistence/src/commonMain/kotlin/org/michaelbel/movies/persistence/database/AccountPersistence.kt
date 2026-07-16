package org.michaelbel.movies.persistence.database

import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
import org.michaelbel.movies.persistence.database.ktx.accountDb
import org.michaelbel.movies.persistence.database.typealiases.AccountId

class AccountPersistence(
    private val moviesDatabase: MoviesDatabase
) {

    suspend fun upsert(account: AccountPojo) {
        moviesDatabase.accountDao.upsert(account.accountDb)
    }

    suspend fun removeById(accountId: AccountId) {
        moviesDatabase.accountDao.removeById(accountId)
    }
}
