package org.michaelbel.movies.interactor.impl

import org.michaelbel.movies.interactor.AboutInteractor
import platform.Foundation.NSBundle

class AboutInteractorImpl: AboutInteractor {

    override val versionName: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "1.0.0"

    override val versionCode: Long
        get() = (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String)?.toLongOrNull() ?: 1L
}
