package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.PagingKeyPersistence
import org.michaelbel.movies.persistence.database.entity.pojo.PagingKeyPojo
import org.michaelbel.movies.persistence.database.typealiases.Page
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.domain.usecase.InsertPagingKeyUseCase.Params

class InsertPagingKeyUseCase(
    private val pagingKeyPersistence: PagingKeyPersistence,
    dispatchers: SharedDispatchers
): UseCase<Params, Unit>(dispatchers.io) {

    override suspend fun execute(params: Params) {
        pagingKeyPersistence.upsertPagingKey(
            PagingKeyPojo(
                pagingKey = params.pagingKey,
                page = params.page,
                totalPages = params.totalPages
            )
        )
    }

    data class Params(
        val pagingKey: PagingKey,
        val page: Page,
        val totalPages: Int
    )
}
