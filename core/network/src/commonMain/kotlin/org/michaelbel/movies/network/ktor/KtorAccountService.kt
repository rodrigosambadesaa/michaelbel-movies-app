package org.michaelbel.movies.network.ktor

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import org.michaelbel.movies.network.model.Account
import org.michaelbel.movies.network.model.Fave
import org.michaelbel.movies.network.model.Mark
import org.michaelbel.movies.network.model.Movie
import org.michaelbel.movies.network.model.MovieResponse
import org.michaelbel.movies.network.model.Result

class KtorAccountService(
    private val ktorHttpClient: HttpClient
) {

    suspend fun accountDetails(sessionId: String): Account {
        return ktorHttpClient.get("account") {
            parameter("session_id", sessionId)
        }.body()
    }

    suspend fun favoriteMovies(accountId: Int, sessionId: String, language: String, page: Int): Result<MovieResponse> {
        return ktorHttpClient.get("account/$accountId/favorite/movies") {
            parameter("session_id", sessionId)
            parameter("language", language)
            parameter("sort_by", Movie.DESC)
            parameter("page", page)
        }.body()
    }

    suspend fun markAsFavorite(accountId: Int, sessionId: String, fave: Fave): Mark {
        return ktorHttpClient.post("account/$accountId/favorite") {
            parameter("session_id", sessionId)
            setBody(fave)
        }.body()
    }
}
