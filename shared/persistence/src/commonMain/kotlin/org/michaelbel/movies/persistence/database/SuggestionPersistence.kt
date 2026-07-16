package org.michaelbel.movies.persistence.database

import org.michaelbel.movies.persistence.database.entity.pojo.SuggestionPojo
import org.michaelbel.movies.persistence.database.ktx.suggestionDb

class SuggestionPersistence(
    private val moviesDatabase: MoviesDatabase
) {

    suspend fun insert(suggestions: List<SuggestionPojo>) {
        moviesDatabase.suggestionDao.upsert(suggestions.map(SuggestionPojo::suggestionDb))
    }

    suspend fun removeAll() {
        moviesDatabase.suggestionDao.removeAll()
    }
}
