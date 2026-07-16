package org.michaelbel.movies.interactor

class Interactor(
    movieInteractor: MovieInteractor,
    searchInteractor: SearchInteractor,
    settingsInteractor: SettingsInteractor,
    localeInteractor: LocaleInteractor
): MovieInteractor by movieInteractor,
    SearchInteractor by searchInteractor,
    SettingsInteractor by settingsInteractor,
    LocaleInteractor by localeInteractor
