package org.michaelbel.movies.repository.impl

import org.michaelbel.movies.persistence.database.entity.pojo.PagingKeyPojo
import org.michaelbel.movies.persistence.database.typealiases.Page
import org.michaelbel.movies.persistence.database.typealiases.PagingKey
import org.michaelbel.movies.repository.PagingKeyRepository

class PagingKeyRepositoryImpl(
    private val repositoryWebStore: RepositoryWebStore
): PagingKeyRepository {

    override suspend fun page(pagingKey: PagingKey): Int? {
        return repositoryWebStore.pagingKey(pagingKey)?.page
    }

    override suspend fun totalPages(pagingKey: PagingKey): Int? {
        return repositoryWebStore.pagingKey(pagingKey)?.totalPages
    }

    override suspend fun prevPage(pagingKey: PagingKey): Int? {
        return null
    }

    override suspend fun removePagingKey(pagingKey: PagingKey) {
        repositoryWebStore.removePagingKey(pagingKey)
    }

    override suspend fun insertPagingKey(pagingKey: PagingKey, page: Page, totalPages: Int) {
        repositoryWebStore.updatePagingKey(
            PagingKeyPojo(
                pagingKey = pagingKey,
                page = page,
                totalPages = totalPages
            )
        )
    }
}
