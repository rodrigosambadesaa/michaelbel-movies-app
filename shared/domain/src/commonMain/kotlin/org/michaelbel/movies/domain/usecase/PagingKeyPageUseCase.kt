@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package org.michaelbel.movies.domain.usecase

import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.persistence.database.PagingKeyPersistence
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

class PagingKeyPageUseCase(
    private val pagingKeyPersistence: PagingKeyPersistence,
    dispatchers: SharedDispatchers
): UseCase<PagingKey, Int?>(dispatchers.io) {

    override suspend fun execute(pagingKey: PagingKey): Int? {
        return pagingKeyPersistence.page(pagingKey)
    }
}
