package org.michaelbel.movies.analytics.impl

import android.os.Bundle
import org.michaelbel.movies.analytics.MoviesAnalytics
import org.michaelbel.movies.analytics.constants.MoviesParams
import org.michaelbel.movies.analytics.model.BaseEvent
import org.michaelbel.movies.platform.analytics.AnalyticsService

class MoviesAnalyticsImpl(
    private val analyticsService: AnalyticsService
): MoviesAnalytics {

    override fun trackDestination(route: String?, arguments: HashMap<String, String>) {
        val args = Bundle()
        arguments.forEach { (key, value) ->
            args.putString(key, value)
        }
        val bundle = Bundle().apply {
            putString(analyticsService.screenName, route)
            putBundle(MoviesParams.PARAM_ARGUMENTS, args)
        }
        analyticsService.logEvent(analyticsService.screenView, bundle)
    }

    override fun logEvent(event: BaseEvent) {
        val bundle = Bundle()
        event.params.forEach { (key, value) ->
            bundle.putString(key, value)
        }
        analyticsService.logEvent(event.name, bundle)
    }
}
