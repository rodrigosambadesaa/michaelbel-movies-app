package org.michaelbel.movies.interactor.impl

import kotlinx.coroutines.withContext
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.interactor.AuthenticationInteractor
import org.michaelbel.movies.interactor.entity.Password
import org.michaelbel.movies.interactor.entity.Username
import org.michaelbel.movies.network.model.Token
import org.michaelbel.movies.repository.AuthenticationRepository

class AuthenticationInteractorImpl(
    private val dispatchers: SharedDispatchers,
    private val authenticationRepository: AuthenticationRepository
): AuthenticationInteractor {

    override suspend fun createRequestToken(
        loginViaTmdb: Boolean
    ): Token {
        return withContext(dispatchers.io) { authenticationRepository.createRequestToken(loginViaTmdb) }
    }

    override suspend fun createSessionWithLogin(
        username: Username,
        password: Password,
        requestToken: String
    ): Token {
        return withContext(dispatchers.io) {
            authenticationRepository.createSessionWithLogin(username.value, password.value, requestToken)
        }
    }
}
