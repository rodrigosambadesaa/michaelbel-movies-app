package org.michaelbel.movies.repository

interface AccountRepository {

    suspend fun accountExpireTime(): Long

    suspend fun accountDetails()
}
