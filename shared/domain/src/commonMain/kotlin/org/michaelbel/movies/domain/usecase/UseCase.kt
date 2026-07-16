package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<in P, R>(
    private val dispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(params: P): Result<R> {
        return withContext(dispatcher) {
            try {
                Result.success(execute(params))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    protected abstract suspend fun execute(params: P): R
}
