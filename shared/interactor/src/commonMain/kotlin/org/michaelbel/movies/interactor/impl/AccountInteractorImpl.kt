package org.michaelbel.movies.interactor.impl

import kotlinx.coroutines.withContext
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.interactor.AccountInteractor
import org.michaelbel.movies.repository.AccountRepository

class AccountInteractorImpl(
    private val dispatchers: SharedDispatchers,
    private val accountRepository: AccountRepository
): AccountInteractor {

    override suspend fun accountDetails() {
        return withContext(dispatchers.io) { accountRepository.accountDetails() }
    }
}
