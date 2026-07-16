package org.michaelbel.movies.auth

import kotlinx.coroutines.launch
import org.michaelbel.movies.auth.intent.AuthIntent
import org.michaelbel.movies.auth.model.AuthModel
import org.michaelbel.movies.common.mvi.Event
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.domain.usecase.AccountDetailsUseCase
import org.michaelbel.movies.domain.usecase.AccountDetailsUseCase.AccountDetailsException
import org.michaelbel.movies.domain.usecase.CreateRequestTokenUseCase
import org.michaelbel.movies.domain.usecase.CreateRequestTokenUseCase.CreateRequestTokenException
import org.michaelbel.movies.domain.usecase.CreateSessionUseCase
import org.michaelbel.movies.domain.usecase.CreateSessionUseCase.CreateSessionException
import org.michaelbel.movies.domain.usecase.CreateSessionWithLoginUseCase
import org.michaelbel.movies.domain.usecase.CreateSessionWithLoginUseCase.CreateSessionWithLoginException
import org.michaelbel.movies.ui.navigation.MainNavigator
import org.michaelbel.movies.ui.pending.PendingActionStore

class AuthViewModel(
    private val accountDetailsUseCase: AccountDetailsUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val createRequestTokenUseCase: CreateRequestTokenUseCase,
    private val createSessionWithLoginUseCase: CreateSessionWithLoginUseCase
): MoviesViewModel<AuthModel, AuthIntent, Event>(AuthModel()) {

    override fun dispatch(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.BackClick -> {
                PendingActionStore.clear()
                launch { MainNavigator.back() }
            }
            is AuthIntent.LoginClick -> {
                val job = launch {
                    reduce { it.copy(error = null) }
                    val requestToken = createRequestTokenUseCase(true).getOrThrow().requestToken
                    reduce { it.copy(requestToken = requestToken) }
                }
                reduce { it.copy(loginJob = job) }
            }
            is AuthIntent.ResetRequestToken -> {
                stateFlow.value.loginJob?.cancel()
                reduce { it.copy(requestToken = null, loginJob = null) }
            }
            is AuthIntent.UsernameChange -> reduce { it.copy(username = intent.username) }
            is AuthIntent.PasswordChange -> reduce { it.copy(password = intent.password) }
            is AuthIntent.SignInClick -> {
                reduce { it.copy(error = null) }
                val job = launch {
                    val token = createRequestTokenUseCase(false).getOrThrow()
                    val params = CreateSessionWithLoginUseCase.Params(
                        username = intent.username.value,
                        password = intent.password.value,
                        requestToken = token.requestToken
                    )
                    val sessionToken = createSessionWithLoginUseCase(params).getOrThrow()
                    createSessionUseCase(sessionToken.requestToken).getOrThrow()
                    accountDetailsUseCase(Unit).getOrThrow()
                    MainNavigator.back()
                }
                reduce { it.copy(signInJob = job) }
            }
        }
    }

    override fun catch(throwable: Throwable) {
        when (throwable) {
            is CreateRequestTokenException -> {
                when {
                    throwable.loginViaTmdb -> dispatch(AuthIntent.ResetRequestToken)
                    else -> {
                        stateFlow.value.signInJob?.cancel()
                        reduce { it.copy(requestToken = null, signInJob = null, error = throwable) }
                    }
                }
            }
            is CreateSessionWithLoginException -> {
                stateFlow.value.signInJob?.cancel()
                reduce { it.copy(requestToken = null, signInJob = null, error = throwable) }
            }
            is CreateSessionException -> {
                stateFlow.value.signInJob?.cancel()
                reduce { it.copy(requestToken = null, signInJob = null, error = throwable) }
            }
            is AccountDetailsException -> {
                stateFlow.value.signInJob?.cancel()
                reduce { it.copy(requestToken = null, signInJob = null, error = throwable) }
            }
            else -> super.catch(throwable)
        }
    }
}
