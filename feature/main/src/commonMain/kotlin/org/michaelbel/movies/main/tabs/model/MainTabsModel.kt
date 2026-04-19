package org.michaelbel.movies.main.tabs.model

import org.michaelbel.movies.common.mvi.model.Model
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo

data class MainTabsModel(
    val isFaveFeatureEnabled: Boolean = false,
    val isAuthorized: Boolean = false,
    val accountPojo: AccountPojo = AccountPojo.Empty
): Model
