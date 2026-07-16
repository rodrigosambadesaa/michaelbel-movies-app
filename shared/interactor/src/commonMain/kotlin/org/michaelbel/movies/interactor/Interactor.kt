package org.michaelbel.movies.interactor

class Interactor(
    authenticationInteractor: AuthenticationInteractor,
    imageInteractor: ImageInteractor,
    movieInteractor: MovieInteractor,
    searchInteractor: SearchInteractor,
    settingsInteractor: SettingsInteractor,
    localeInteractor: LocaleInteractor
): AuthenticationInteractor by authenticationInteractor,
    ImageInteractor by imageInteractor,
    MovieInteractor by movieInteractor,
    SearchInteractor by searchInteractor,
    SettingsInteractor by settingsInteractor,
    LocaleInteractor by localeInteractor
