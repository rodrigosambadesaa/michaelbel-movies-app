package org.michaelbel.movies.common.theme

import org.michaelbel.movies.common.SealedString
import org.michaelbel.movies.common.exceptions.InvalidThemeException

sealed interface AppTheme: SealedString {

    data object NightNo: AppTheme

    data object NightYes: AppTheme

    data object FollowSystem: AppTheme

    companion object {
        val VALUES = listOf(
            NightNo,
            NightYes,
            FollowSystem
        )

        fun transform(name: String): AppTheme {
            return when (name) {
                NightNo.toString() -> NightNo
                NightYes.toString() -> NightYes
                FollowSystem.toString() -> FollowSystem
                else -> throw InvalidThemeException()
            }
        }
    }
}
