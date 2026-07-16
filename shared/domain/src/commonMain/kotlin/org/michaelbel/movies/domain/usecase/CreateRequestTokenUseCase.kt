@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.common.exceptions.CreateRequestTokenException
import org.michaelbel.movies.network.AuthenticationNetworkService
import org.michaelbel.movies.network.model.Token

class CreateRequestTokenUseCase(
    private val authenticationNetworkService: AuthenticationNetworkService,
    dispatchers: SharedDispatchers
): UseCase<Boolean, Token>(dispatchers.io) {

    override suspend fun execute(loginViaTmdb: Boolean): Token {
        return try {
            val token = authenticationNetworkService.createRequestToken()
            if (!token.success) throw CreateRequestTokenException(loginViaTmdb)
            token
        } catch (exception: Exception) {
            throw CreateRequestTokenException(loginViaTmdb)
        }
    }

    data class CreateRequestTokenException(
        val loginViaTmdb: Boolean
    ): Exception()
}
