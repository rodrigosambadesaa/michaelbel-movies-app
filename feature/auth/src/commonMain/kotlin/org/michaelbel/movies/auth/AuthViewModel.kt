package org.michaelbel.movies.auth

import kotlinx.coroutines.launch
import org.michaelbel.movies.auth.intent.AuthIntent
import org.michaelbel.movies.auth.model.AuthModel
import org.michaelbel.movies.common.exceptions.AccountDetailsException
import org.michaelbel.movies.common.exceptions.CreateRequestTokenException
import org.michaelbel.movies.common.exceptions.CreateSessionException
import org.michaelbel.movies.common.exceptions.CreateSessionWithLoginException
import org.michaelbel.movies.common.mvi.Event
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.ui.navigation.MainNavigator

class AuthViewModel(
    private val interactor: Interactor
): MoviesViewModel<AuthModel, AuthIntent, Event>(AuthModel()) {

    override fun dispatch(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.BackClick -> launch { MainNavigator.back() }
            is AuthIntent.LoginClick -> {
                val job = launch {
                    reduce { it.copy(error = null) }
                    val requestToken = interactor.createRequestToken(loginViaTmdb = true).requestToken
                    reduce { it.copy(requestToken = requestToken) }
                }
                reduce { it.copy(loginJob = job) }
            }
            is AuthIntent.ResetRequestToken -> {
                stateFlow.value.loginJob?.cancel()
                reduce { it.copy(requestToken = null, loginJob = null) }
            }
            is AuthIntent.SignInClick -> {
                reduce { it.copy(error = null) }
                val job = launch {
                    val token = interactor.createRequestToken(loginViaTmdb = false)
                    val sessionToken = interactor.createSessionWithLogin(intent.username, intent.password, token.requestToken)
                    interactor.run {
                        createSession(sessionToken.requestToken)
                        accountDetails()
                    }
                    dispatch(AuthIntent.BackClick)
                }
                reduce { it.copy(signInJob = job) }
            }
        }
    }

    override fun catch(throwable: Throwable) {
        stateFlow.value.signInJob?.cancel()
        reduce { it.copy(requestToken = null, signInJob = null) }

        when (throwable) {
            is CreateRequestTokenException -> {
                when {
                    throwable.loginViaTmdb -> dispatch(AuthIntent.ResetRequestToken)
                    else -> reduce { it.copy(error = throwable) }
                }
            }
            is CreateSessionWithLoginException -> reduce { it.copy(error = throwable) }
            is CreateSessionException -> reduce { it.copy(error = throwable) }
            is AccountDetailsException -> reduce { it.copy(error = throwable) }
            else -> super.catch(throwable)
        }
    }
}
