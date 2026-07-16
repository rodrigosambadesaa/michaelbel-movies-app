package org.michaelbel.movies.interactor

import org.michaelbel.movies.interactor.entity.Password
import org.michaelbel.movies.interactor.entity.Username
import org.michaelbel.movies.network.model.Token

interface AuthenticationInteractor {

    suspend fun createSessionWithLogin(username: Username, password: Password, requestToken: String): Token
}
