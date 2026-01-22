package org.michaelbel.movies.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.account.intent.AccountIntent
import org.michaelbel.movies.account.model.AccountModel
import org.michaelbel.movies.account.ui.AccountCountryBox
import org.michaelbel.movies.account.ui.AccountToolbar
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
import org.michaelbel.movies.ui.accessibility.MoviesContentDescriptionCommon
import org.michaelbel.movies.ui.compose.AccountAvatar
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.isPortrait
import org.michaelbel.movies.ui.ktx.lettersTextFontSizeLarge
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun AccountScreen(
    viewModel: AccountViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateCommon()

    AccountScreenContent(
        state = state,
        dispatch = viewModel::dispatch
    )
}

@Composable
private fun AccountScreenContent(
    state: AccountModel,
    dispatch: (AccountIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = if (isPortrait) 16.dp else 64.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            )
    ) {
        AccountToolbar(
            onNavigationIconClick = { dispatch(AccountIntent.BackClick) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                AccountAvatar(
                    account = state.accountPojo,
                    fontSize = state.accountPojo.lettersTextFontSizeLarge,
                    modifier = Modifier.size(64.dp)
                )

                if (state.accountPojo.adult) {
                    Icon(
                        painter = painterResource(MoviesIcons.AdultOutline),
                        contentDescription = stringResource(MoviesContentDescriptionCommon.AdultIcon),
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.BottomEnd)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 12.dp, end = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                if (state.accountPojo.name.isNotEmpty()) {
                    Text(
                        text = state.accountPojo.name,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }

                if (state.accountPojo.username.isNotEmpty()) {
                    Text(
                        text = state.accountPojo.username,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }

        if (state.accountPojo.country.isNotEmpty()) {
            AccountCountryBox(
                country = state.accountPojo.country,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 16.dp, top = 8.dp)
            )
        }

        Button(
            onClick = { dispatch(AccountIntent.LogoutClick) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
            enabled = !state.isLogoutJobActive,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceTint
            ),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            if (state.isLogoutJobActive) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(MoviesStrings.account_logout)
                )
            }
        }
    }

}



@Preview
@Composable
private fun AccountScreenContentPreview() {
    MoviesTheme {
        AccountScreenContent(
            state = AccountModel(
                accountPojo = AccountPojo(
                    accountId = 0,
                    avatarUrl = "",
                    language = "",
                    country = "Finland",
                    name = "Michael Bely",
                    adult = true,
                    username = "michaelbel"
                ),
            ),
            dispatch = {}
        )
    }
}
