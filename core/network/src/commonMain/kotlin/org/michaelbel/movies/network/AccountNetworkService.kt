package org.michaelbel.movies.network

import org.michaelbel.movies.network.ktor.KtorAccountService
import org.michaelbel.movies.network.model.Account
import org.michaelbel.movies.network.model.Fave
import org.michaelbel.movies.network.model.Mark
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.network.model.Result

class AccountNetworkService internal constructor(
    private val ktorAccountService: KtorAccountService
) {

    suspend fun accountDetails(sessionId: String): Account {
        return ktorAccountService.accountDetails(sessionId)
    }

    suspend fun favoriteMovies(accountId: Int, sessionId: String, language: String, page: Int): Result<MovieResponse> {
        return ktorAccountService.favoriteMovies(accountId, sessionId, language, page)
    }

    suspend fun markAsFavorite(accountId: Int, sessionId: String, fave: Fave): Mark {
        return ktorAccountService.markAsFavorite(accountId, sessionId, fave)
    }
}
