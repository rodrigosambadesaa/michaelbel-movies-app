package org.michaelbel.movies.di

import org.koin.dsl.module
import org.michaelbel.movies.account.di.accountKoinModule
import org.michaelbel.movies.auth.di.authKoinModule
import org.michaelbel.movies.debug.di.debugKoinModule
import org.michaelbel.movies.details.di.detailsKoinModule
import org.michaelbel.movies.feed.di.feedKoinModule
import org.michaelbel.movies.gallery.di.galleryKoinModule
import org.michaelbel.movies.main.di.mainKoinModule
import org.michaelbel.movies.main.tabs.di.mainTabsKoinModule
import org.michaelbel.movies.platform.inject.flavorServiceKtorModule
import org.michaelbel.movies.settings.di.settingsKoinModule
import org.michaelbel.movies.widget.di.glanceKoinModule

val appKoinModule = module {
    includes(
        flavorServiceKtorModule,
        mainKoinModule,
        mainTabsKoinModule,
        accountKoinModule,
        authKoinModule,
        detailsKoinModule,
        feedKoinModule,
        galleryKoinModule,
        settingsKoinModule,
        debugKoinModule,
        glanceKoinModule
    )
}
