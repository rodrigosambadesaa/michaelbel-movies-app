package org.michaelbel.movies.interactor.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.michaelbel.movies.analytics.di.moviesAnalyticsKoinModule
import org.michaelbel.movies.common.dispatchers.di.dispatchersKoinModule
import org.michaelbel.movies.domain.usecase.di.useCaseKoinModule
import org.michaelbel.movies.interactor.AboutInteractor
import org.michaelbel.movies.interactor.AppNotificationInteractor
import org.michaelbel.movies.interactor.LocaleInteractor
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.interactor.impl.AboutInteractorImpl
import org.michaelbel.movies.interactor.impl.AppNotificationInteractorImpl
import org.michaelbel.movies.interactor.impl.LocaleInteractorImpl
import org.michaelbel.movies.interactor.impl.UiInteractorImpl
import org.michaelbel.movies.network.di.networkKoinModule
import org.michaelbel.movies.persistence.database.di.moviesDatabaseKoinModule
import org.michaelbel.movies.persistence.database.di.persistenceKoinModule
import org.michaelbel.movies.persistence.datastore.di.moviesPreferencesKoinModule

actual val localeInteractorKoinModule = module {
    includes(
        dispatchersKoinModule,
        networkKoinModule,
        persistenceKoinModule,
        moviesPreferencesKoinModule,
        moviesDatabaseKoinModule,
        moviesAnalyticsKoinModule
    )
    singleOf(::LocaleInteractorImpl) { bind<LocaleInteractor>() }
}

actual val aboutInteractorKoinModule = module {
    singleOf(::AboutInteractorImpl) { bind<AboutInteractor>() }
}

actual val uiInteractorKoinModule = module {
    singleOf(::UiInteractorImpl) { bind<UiInteractor>() }
}

actual val appNotificationInteractorKoinModule = module {
    includes(
        useCaseKoinModule
    )
    singleOf(::AppNotificationInteractorImpl) { bind<AppNotificationInteractor>() }
}
