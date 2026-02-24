@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package org.michaelbel.movies.account

import androidx.compose.foundation.Image
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.account.intent.AccountIntent
import org.michaelbel.movies.account.model.AccountModel
import org.michaelbel.movies.persistence.database.entity.pojo.AccountPojo
import org.michaelbel.movies.ui.accessibility.MoviesContentDescriptionCommon
import org.michaelbel.movies.ui.compose.AccountAvatar
import org.michaelbel.movies.ui.icons.Adult
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
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(MoviesStrings.account_title),
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {
                IconButton(
                    onClick = { dispatch(AccountIntent.BackClick) }
                ) {
                    Image(
                        imageVector = MoviesIcons.Close,
                        contentDescription = stringResource(MoviesContentDescriptionCommon.CloseIcon),
                        modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
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
                        imageVector = MoviesIcons.Adult,
                        contentDescription = stringResource(MoviesContentDescriptionCommon.AdultIcon),
                        modifier = Modifier
                            .size(IconButtonDefaults.smallIconSize)
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
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MoviesIcons.LocationOn,
                    contentDescription = stringResource(MoviesContentDescriptionCommon.UserLocationIcon),
                    modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = state.accountPojo.country,
                    modifier = Modifier.padding(start = 4.dp),
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
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
                LoadingIndicator(
                    modifier = Modifier.size(40.dp),
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
