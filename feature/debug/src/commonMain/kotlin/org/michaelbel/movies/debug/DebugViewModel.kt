package org.michaelbel.movies.debug

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.mvi.Event
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.debug.intent.DebugIntent
import org.michaelbel.movies.debug.model.DebugModel
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.platform.Flavor
import org.michaelbel.movies.platform.app.AppService
import org.michaelbel.movies.platform.messaging.MessagingService
import org.michaelbel.movies.ui.navigation.MainNavigator

class DebugViewModel(
    private val interactor: Interactor,
    private val appService: AppService,
    private val messagingService: MessagingService
): MoviesViewModel<DebugModel, DebugIntent, Event>(DebugModel()) {

    init {
        dispatch(DebugIntent.CollectThemeData)
        dispatch(DebugIntent.CollectFirebaseTokenFeatureEnabled)
    }

    override fun dispatch(intent: DebugIntent) {
        when (intent) {
            is DebugIntent.DismissRequest -> launch { MainNavigator.back() }
            is DebugIntent.CollectThemeData -> {
                launch {
                    interactor.themeData.collectLatest { themeData ->
                        reduce { it.copy(themeData = themeData) }
                    }
                }
            }
            is DebugIntent.CollectFirebaseTokenFeatureEnabled -> {
                val isFirebaseTokenFeatureEnabled = appService.flavor == Flavor.Gms
                reduce { it.copy(isFirebaseTokenFeatureEnabled = isFirebaseTokenFeatureEnabled) }
                if (isFirebaseTokenFeatureEnabled) {
                    dispatch(DebugIntent.CollectFirebaseToken)
                }
            }
            is DebugIntent.CollectFirebaseToken -> {
                launch {
                    val firebaseToken = messagingService.awaitToken()
                    reduce { it.copy(firebaseToken = firebaseToken) }
                }
            }
            is DebugIntent.ResetNotificationExpireTime -> launch { interactor.resetNotificationExpireTime() }
        }
    }
}
