package org.michaelbel.movies.ui.entity

sealed interface MovieCardStyle {
    data object Row: MovieCardStyle
    data object Column: MovieCardStyle
}
