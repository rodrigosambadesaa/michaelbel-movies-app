package org.michaelbel.movies.interactor

interface AccountInteractor {

    suspend fun accountExpireTime(): Long

    suspend fun accountDetails()
}
