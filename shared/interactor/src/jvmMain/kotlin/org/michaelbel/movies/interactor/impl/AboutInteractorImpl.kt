package org.michaelbel.movies.interactor.impl

import org.michaelbel.movies.interactor.AboutInteractor

class AboutInteractorImpl: AboutInteractor {

    override val versionName: String = System.getProperty("movies.version", "3.1.0")

    override val versionCode: Long = System.getProperty("movies.build", "1").toLongOrNull() ?: 1L
}
