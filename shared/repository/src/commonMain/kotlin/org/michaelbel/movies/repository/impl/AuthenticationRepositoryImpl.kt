package org.michaelbel.movies.repository.impl

import org.michaelbel.movies.common.exceptions.CreateSessionWithLoginException
import org.michaelbel.movies.network.AuthenticationNetworkService
import org.michaelbel.movies.network.model.Token
import org.michaelbel.movies.network.model.Username
import org.michaelbel.movies.repository.AuthenticationRepository

class AuthenticationRepositoryImpl(
    private val authenticationNetworkService: AuthenticationNetworkService
): AuthenticationRepository {

    override suspend fun createSessionWithLogin(username: String, password: String, requestToken: String): Token {
        return try {
            val token = authenticationNetworkService.createSessionWithLogin(
                username = Username(
                    username = username,
                    password = password,
                    requestToken = requestToken
                )
            )
            if (!token.success) throw CreateSessionWithLoginException()
            token
        } catch (_: Exception) { throw CreateSessionWithLoginException() }
    }
}
