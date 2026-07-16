package org.michaelbel.movies.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class FlowUseCase<in P, R>(
    private val dispatcher: CoroutineDispatcher
) {

    operator fun invoke(params: P): Flow<R> {
        return execute(params).flowOn(dispatcher)
    }

    protected abstract fun execute(params: P): Flow<R>
}
