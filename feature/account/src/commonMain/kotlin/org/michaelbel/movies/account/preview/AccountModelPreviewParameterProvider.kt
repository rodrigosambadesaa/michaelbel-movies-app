package org.michaelbel.movies.account.preview

import kotlinx.coroutines.Job
import org.michaelbel.movies.account.model.AccountModel
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
import org.michaelbel.movies.ui.preview.base.CollectionPreviewParameterProvider

class AccountModelPreviewParameterProvider : CollectionPreviewParameterProvider<AccountModel>(
    listOf(
        AccountModel(
            accountPojo = AccountPojo(
                accountId = 0,
                avatarUrl = "",
                language = "",
                country = "Finland",
                name = "Michael Bely",
                adult = true,
                username = "michaelbel"
            )
        ),
        AccountModel(
            accountPojo = AccountPojo(
                accountId = 0,
                avatarUrl = "",
                language = "",
                country = "Finland",
                name = "Michael Bely",
                adult = true,
                username = "michaelbel"
            ),
            logoutJob = Job()
        )
    )
)
