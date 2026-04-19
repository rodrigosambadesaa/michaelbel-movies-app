package org.michaelbel.movies.settingsweb.ktx

import org.michaelbel.movies.common.SealedString
import org.michaelbel.movies.common.appearance.FeedView
import org.michaelbel.movies.common.list.MovieList
import org.michaelbel.movies.common.theme.AppTheme

val SealedString.stringText: String
    get() = when (this) {
        is AppTheme.NightNo -> settingsThemeLightText
        is AppTheme.NightYes -> settingsThemeDarkText
        is AppTheme.FollowSystem -> settingsThemeSystemText
        is AppTheme.Amoled -> settingsThemeAmoledText
        is FeedView.FeedList -> settingsAppearanceListText
        is FeedView.FeedGrid -> settingsAppearanceGridText
        is MovieList.NowPlaying -> settingsMovieListNowPlayingText
        is MovieList.Popular -> settingsMovieListPopularText
        is MovieList.TopRated -> settingsMovieListTopRatedText
        is MovieList.Upcoming -> settingsMovieListUpcomingText
        else -> ""
    }

private const val settingsThemeSystemText = "Follow System"
private const val settingsThemeLightText = "Light"
private const val settingsThemeDarkText = "Dark"
private const val settingsThemeAmoledText = "Amoled"
private const val settingsAppearanceListText = "List"
private const val settingsAppearanceGridText = "Grid"
private const val settingsMovieListNowPlayingText = "Now Playing"
private const val settingsMovieListPopularText = "Popular"
private const val settingsMovieListTopRatedText = "Top Rated"
private const val settingsMovieListUpcomingText = "Upcoming"
