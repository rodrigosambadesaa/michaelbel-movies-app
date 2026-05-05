package org.michaelbel.movies.common.viewmodel

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import org.michaelbel.movies.common.dispatchers.uiDispatcher
import org.michaelbel.movies.common.log.log
import kotlin.coroutines.CoroutineContext

abstract class CoroutineViewModel: ViewModel(
    viewModelScope = CoroutineScope(uiDispatcher)
), CoroutineScope {

    private val scopeJob: Job = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable -> catch(throwable) }

    override val coroutineContext: CoroutineContext
        get() = viewModelScope.coroutineContext + scopeJob + exceptionHandler

    override fun onCleared() {
        coroutineContext.cancelChildren()
        super.onCleared()
    }

    @CallSuper
    protected open fun catch(throwable: Throwable) {
        log(throwable)
    }
}
