package org.michaelbel.movies.account.model

import kotlinx.coroutines.Job
import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo

data class AccountModel(
    val accountPojo: AccountPojo = AccountPojo.Empty,
    val logoutJob: Job? = null
): Model {

    val isLogoutJobActive: Boolean
        get() = logoutJob != null && logoutJob.isActive
}