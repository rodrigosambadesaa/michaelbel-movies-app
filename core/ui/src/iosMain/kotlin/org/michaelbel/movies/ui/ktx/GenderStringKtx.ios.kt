package org.michaelbel.movies.ui.ktx

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import org.michaelbel.movies.ui.strings.MoviesStrings

val SettingsGenderText: String
    @Composable get() = stringResource(MoviesStrings.settings_gender)
