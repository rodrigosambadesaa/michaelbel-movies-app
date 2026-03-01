package org.michaelbel.movies.ui.ktx

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import org.michaelbel.movies.common.exceptions.PageEmptyException

val <T: Any> LazyPagingItems<T>.isNotEmpty: Boolean
    get() = itemCount > 0

val <T: Any> LazyPagingItems<T>.isEmpty: Boolean
    get() = itemCount == 0

private val <T: Any> LazyPagingItems<T>.refreshState: LoadState
    get() = loadState.mediator?.refresh ?: loadState.refresh

private val <T: Any> LazyPagingItems<T>.appendState: LoadState
    get() = loadState.mediator?.append ?: loadState.append

val <T: Any> LazyPagingItems<T>.isRefreshLoading: Boolean
    get() = refreshState is LoadState.Loading

val <T: Any> LazyPagingItems<T>.isLoading: Boolean
    get() = isRefreshLoading

val <T: Any> LazyPagingItems<T>.isFailure: Boolean
    get() = refreshState is LoadState.Error && isEmpty

val <T: Any> LazyPagingItems<T>.isPagingLoading: Boolean
    get() = isNotEmpty && (isAppendLoading || isRefreshLoading)

internal val <T: Any> LazyPagingItems<T>.isPagingFailure: Boolean
    get() = isNotEmpty && (isAppendError && appendThrowable !is PageEmptyException || isRefreshError && refreshThrowable !is PageEmptyException)

val <T: Any> LazyPagingItems<T>.isRefreshError: Boolean
    get() = refreshState is LoadState.Error

val <T: Any> LazyPagingItems<T>.isAppendError: Boolean
    get() = appendState is LoadState.Error

val <T: Any> LazyPagingItems<T>.refreshThrowable: Throwable
    get() = (refreshState as LoadState.Error).error

val <T: Any> LazyPagingItems<T>.appendThrowable: Throwable
    get() = (appendState as LoadState.Error).error

val <T: Any> LazyPagingItems<T>.isAppendLoading: Boolean
    get() = appendState is LoadState.Loading

val <T: Any> LazyPagingItems<T>.isAppendRefresh: Boolean
    get() = isRefreshLoading
