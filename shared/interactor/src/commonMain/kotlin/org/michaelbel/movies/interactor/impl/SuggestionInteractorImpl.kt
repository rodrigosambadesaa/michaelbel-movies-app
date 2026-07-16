package org.michaelbel.movies.interactor.impl

import kotlinx.coroutines.withContext
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.interactor.LocaleInteractor
import org.michaelbel.movies.interactor.SuggestionInteractor
import org.michaelbel.movies.repository.SuggestionRepository

class SuggestionInteractorImpl(
    private val dispatchers: SharedDispatchers,
    private val localeInteractor: LocaleInteractor,
    private val suggestionRepository: SuggestionRepository
): SuggestionInteractor {

    override suspend fun updateSuggestions() {
        withContext(dispatchers.io) { suggestionRepository.updateSuggestions(localeInteractor.language) }
    }
}
