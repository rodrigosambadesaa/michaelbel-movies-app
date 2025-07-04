package org.michaelbel.movies.persistence.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import org.michaelbel.movies.persistence.database.entity.PagingKeyDb
import org.michaelbel.movies.persistence.database.typealiases.PagingKey

/**
 * The Data Access Object for the [PagingKeyDb] class.
 */
@Dao
interface PagingKeyDao {

    @Transaction
    @Query("SELECT page FROM pagingkeys WHERE pagingKey = :pagingKey")
    suspend fun page(pagingKey: PagingKey): Int?

    @Transaction
    @Query("SELECT totalPages FROM pagingkeys WHERE pagingKey = :pagingKey")
    suspend fun totalPages(pagingKey: PagingKey): Int?

    @Transaction
    @Query("DELETE FROM pagingkeys WHERE pagingKey = :pagingKey")
    suspend fun removePagingKey(pagingKey: PagingKey)

    @Transaction
    @Upsert
    suspend fun upsertPagingKey(pagingKey: PagingKeyDb)
}