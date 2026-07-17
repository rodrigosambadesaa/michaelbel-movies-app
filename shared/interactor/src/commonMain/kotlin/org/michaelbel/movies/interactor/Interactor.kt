package org.michaelbel.movies.interactor

class Interactor(
    movieInteractor: MovieInteractor,
    searchInteractor: SearchInteractor,
    localeInteractor: LocaleInteractor
): MovieInteractor by movieInteractor,
    SearchInteractor by searchInteractor,
    LocaleInteractor by localeInteractor
