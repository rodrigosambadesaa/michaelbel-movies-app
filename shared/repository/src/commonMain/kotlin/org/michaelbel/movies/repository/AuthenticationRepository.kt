package org.michaelbel.movies.repository

import org.michaelbel.movies.network.model.Token

interface AuthenticationRepository {

    suspend fun createSessionWithLogin(username: String, password: String, requestToken: String): Token
}
