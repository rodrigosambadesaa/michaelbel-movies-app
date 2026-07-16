package org.michaelbel.movies.interactor

class Interactor(
    authenticationInteractor: AuthenticationInteractor,
    movieInteractor: MovieInteractor,
    searchInteractor: SearchInteractor,
    settingsInteractor: SettingsInteractor,
    localeInteractor: LocaleInteractor
): AuthenticationInteractor by authenticationInteractor,
    MovieInteractor by movieInteractor,
    SearchInteractor by searchInteractor,
    SettingsInteractor by settingsInteractor,
    LocaleInteractor by localeInteractor
