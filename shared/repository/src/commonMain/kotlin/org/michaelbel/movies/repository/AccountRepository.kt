package org.michaelbel.movies.repository

import org.michaelbel.movies.persistence.database.typealiases.AccountId

interface AccountRepository {

    suspend fun accountId(): AccountId

    suspend fun accountExpireTime(): Long

    suspend fun accountDetails()
}
