package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.dao.SuggestionDao
import org.michaelbel.movies.persistence.database.entity.pojo.SuggestionPojo

class SuggestionPojosFlowUseCase(
    private val suggestionDao: SuggestionDao,
    dispatchers: SharedDispatchers
): FlowUseCase<Unit, List<SuggestionPojo>>(dispatchers.io) {

    override fun execute(params: Unit): Flow<List<SuggestionPojo>> {
        return suggestionDao.selectFlow()
    }
}
