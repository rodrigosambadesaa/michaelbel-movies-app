package org.michaelbel.movies.interactor

class Interactor(
    searchInteractor: SearchInteractor,
    localeInteractor: LocaleInteractor
): SearchInteractor by searchInteractor,
    LocaleInteractor by localeInteractor
