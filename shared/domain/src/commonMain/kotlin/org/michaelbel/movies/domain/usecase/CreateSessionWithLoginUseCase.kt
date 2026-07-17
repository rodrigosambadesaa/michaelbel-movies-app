package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.network.AuthenticationNetworkService
import org.michaelbel.movies.network.model.Token
import org.michaelbel.movies.network.model.Username
import org.michaelbel.movies.domain.usecase.CreateSessionWithLoginUseCase.Params

class CreateSessionWithLoginUseCase(
    private val authenticationNetworkService: AuthenticationNetworkService,
    dispatchers: SharedDispatchers
): UseCase<Params, Token>(dispatchers.io) {

    override suspend fun execute(params: Params): Token {
        return try {
            val token = authenticationNetworkService.createSessionWithLogin(
                username = Username(
                    username = params.username,
                    password = params.password,
                    requestToken = params.requestToken
                )
            )
            if (!token.success) throw CreateSessionWithLoginException("")
            token
        } catch (exception: Exception) {
            throw CreateSessionWithLoginException(exception.message.orEmpty())
        }
    }

    data class CreateSessionWithLoginException(
        override val message: String
    ): Exception(message)

    data class Params(
        val username: String,
        val password: String,
        val requestToken: String
    )
}
