@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package org.michaelbel.movies.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.auth.intent.AuthIntent
import org.michaelbel.movies.auth.ktx.text
import org.michaelbel.movies.auth.model.AuthModel
import org.michaelbel.movies.common.browser.navigateToUrl
import org.michaelbel.movies.common.exceptions.CreateSessionWithLoginException
import org.michaelbel.movies.interactor.entity.Password
import org.michaelbel.movies.interactor.entity.Username
import org.michaelbel.movies.interactor.ktx.PasswordSaver
import org.michaelbel.movies.interactor.ktx.UsernameSaver
import org.michaelbel.movies.interactor.ktx.isNotEmpty
import org.michaelbel.movies.interactor.ktx.trim
import org.michaelbel.movies.network.config.TMDB_AUTH_REDIRECT_URL
import org.michaelbel.movies.network.config.TMDB_AUTH_URL_2
import org.michaelbel.movies.network.config.TMDB_AUTH_URL_3
import org.michaelbel.movies.network.config.TMDB_PRIVACY_POLICY
import org.michaelbel.movies.network.config.TMDB_REGISTER
import org.michaelbel.movies.network.config.TMDB_RESET_PASSWORD
import org.michaelbel.movies.network.config.TMDB_TERMS_OF_USE
import org.michaelbel.movies.network.config.TMDB_URL
import org.michaelbel.movies.ui.accessibility.MoviesContentDescriptionCommon
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.ktx.clickableWithoutRipple
import org.michaelbel.movies.ui.ktx.collectAsStateCommon
import org.michaelbel.movies.ui.ktx.isPortrait
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateCommon()

    AuthScreenContent(
        state = state,
        dispatch = viewModel::dispatch
    )
}

@Composable
private fun AuthScreenContent(
    state: AuthModel,
    dispatch: (AuthIntent) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    var username by rememberSaveable(saver = UsernameSaver) { mutableStateOf(Username("")) }
    var password by rememberSaveable(saver = PasswordSaver) { mutableStateOf(Password("")) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val navigateToTmdbUrl = navigateToUrl(TMDB_URL)
    val navigateToTmdbResetPasswordUrl = navigateToUrl(TMDB_RESET_PASSWORD)
    val navigateToTmdbRegisterUrl = navigateToUrl(TMDB_REGISTER)

    if (state.requestToken != null) {
        val signUrl = "$TMDB_AUTH_URL_2/${state.requestToken}$TMDB_AUTH_URL_3$TMDB_AUTH_REDIRECT_URL"
        navigateToUrl(signUrl)
        dispatch(AuthIntent.ResetRequestToken)
    }

    val navigateToTermsOfUseUrl = navigateToUrl(TMDB_TERMS_OF_USE)
    val navigateToPrivacyPolicyUrl = navigateToUrl(TMDB_PRIVACY_POLICY)

    Column(
        modifier = Modifier
            .padding(horizontal = if (isPortrait) 16.dp else 64.dp)
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            )
            .verticalScroll(scrollState)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = stringResource(MoviesStrings.auth_title),
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {
                IconButton(
                    onClick = { dispatch(AuthIntent.BackClick) }
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

        Icon(
            painter = painterResource(MoviesIcons.TmdbLogo),
            contentDescription = MoviesContentDescriptionCommon.None,
            modifier = Modifier
                .padding(top = 8.dp)
                .clickableWithoutRipple { navigateToTmdbUrl() }
                .align(Alignment.CenterHorizontally),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )

        OutlinedTextField(
            value = username.value,
            onValueChange = { value ->
                username = Username(value.filterNot(Char::isWhitespace))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                .semantics { contentType = ContentType.Username },
            label = {
                Text(
                    text = stringResource(MoviesStrings.auth_label_username)
                )
            },
            isError = state.error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { value -> password = Password(value.filterNot(Char::isWhitespace)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp)
                .semantics { contentType = ContentType.Password },
            label = {
                Text(
                    text = stringResource(MoviesStrings.auth_label_password)
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = password.isNotEmpty,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Image(
                            imageVector = if (passwordVisible) MoviesIcons.Visibility else MoviesIcons.VisibilityOff,
                            contentDescription = stringResource(if (passwordVisible) MoviesContentDescriptionCommon.PasswordIcon else MoviesContentDescriptionCommon.PasswordOffIcon),
                            modifier = Modifier.size(IconButtonDefaults.smallIconSize),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                        )
                    }
                }
            },
            supportingText = if (state.error != null) {
                {
                    Text(
                        text = state.error.text,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else null,
            isError = state.error != null,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    dispatch(AuthIntent.SignInClick(username, password))
                }
            ),
            singleLine = true
        )

        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = navigateToTmdbRegisterUrl,
                shapes = ButtonDefaults.shapes()
            ) {
                Text(
                    text = stringResource(MoviesStrings.auth_sign_up)
                )
            }

            AnimatedVisibility(
                visible = state.error != null && state.error is CreateSessionWithLoginException,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TextButton(
                    onClick = navigateToTmdbResetPasswordUrl,
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text(
                        text = stringResource(MoviesStrings.auth_reset_password)
                    )
                }
            }
        }

        Button(
            onClick = { dispatch(AuthIntent.SignInClick(username.trim, password.trim)) },
            shapes = ButtonDefaults.shapes(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp, end = 16.dp),
            enabled = username.isNotEmpty && password.isNotEmpty && !state.isSignInJobActive,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceTint
            )
        ) {
            if (state.isSignInJobActive) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(MoviesStrings.auth_sign_in)
                )
            }
        }

        Button(
            onClick = { dispatch(AuthIntent.LoginClick) },
            shapes = ButtonDefaults.shapes(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp),
            enabled = !state.isLoginJobActive,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceTint
            )
        ) {
            if (state.isLoginJobActive) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(MoviesStrings.auth_login)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider(
                thickness = .1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(MoviesStrings.auth_terms_of_use),
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .clickableWithoutRipple(navigateToTermsOfUseUrl),
                    style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer)
                )

                Text(
                    text = stringResource(MoviesStrings.auth_privacy_policy),
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .clickableWithoutRipple(navigateToPrivacyPolicyUrl),
                    style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
        }
    }
}

@Preview
@Composable
private fun AuthScreenContentPreview() {
    MoviesTheme {
        AuthScreenContent(
            state = AuthModel(),
            dispatch = {}
        )
    }
}
