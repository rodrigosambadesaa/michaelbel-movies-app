package org.michaelbel.movies.account

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.michaelbel.movies.account.intent.AccountIntent
import org.michaelbel.movies.account.model.AccountModel
import org.michaelbel.movies.common.exceptions.DeleteSessionException
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.ui.navigation.MainNavigator

class AccountViewModel(
    private val interactor: Interactor
): MoviesViewModel<AccountModel, AccountIntent>(AccountModel()) {

    init {
        dispatch(AccountIntent.CollectAccountPojo)
    }

    override fun dispatch(intent: AccountIntent) {
        when (intent) {
            is AccountIntent.CollectAccountPojo -> {
                launch {
                    interactor.accountPojoFlow.collectLatest { accountPojo ->
                        reduce { it.copy(accountPojo = accountPojo) }
                    }
                }
            }
            is AccountIntent.BackClick -> launch { MainNavigator.back() }
            is AccountIntent.LogoutClick -> {
                val job = launch {
                    interactor.deleteSession()
                    MainNavigator.back()
                }
                reduce { it.copy(logoutJob = job) }
            }
        }
    }

    override fun catch(throwable: Throwable) {
        when (throwable) {
            is DeleteSessionException -> reduce { it.copy(logoutJob = null) }
            else -> super.catch(throwable)
        }
    }
}
