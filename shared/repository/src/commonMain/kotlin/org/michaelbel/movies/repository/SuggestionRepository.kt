package org.michaelbel.movies.repository

interface SuggestionRepository {

    suspend fun updateSuggestions(language: String)
}
