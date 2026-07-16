package org.michaelbel.movies.interactor

import org.michaelbel.movies.persistence.database.typealiases.AccountId

interface AccountInteractor {

    suspend fun accountId(): AccountId?

    suspend fun accountExpireTime(): Long

    suspend fun accountDetails()
}
