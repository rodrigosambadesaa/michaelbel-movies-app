package org.michaelbel.movies.debug

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.viewmodel.CoroutineViewModel
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.platform.Flavor
import org.michaelbel.movies.platform.app.AppService
import org.michaelbel.movies.platform.messaging.MessagingService

class DebugViewModel(
    private val interactor: Interactor,
    appService: AppService,
    private val messagingService: MessagingService
): CoroutineViewModel() {

    val isFirebaseTokenFeatureEnabled = appService.flavor == Flavor.Gms

    val themeDataFlow: StateFlow<ThemeData> = interactor.themeData
        .stateIn(
            scope = this,
            started = SharingStarted.Lazily,
            initialValue = ThemeData.Default
        )

    val firebaseTokenFlow: StateFlow<String> = flow { emit(messagingService.awaitToken()) }
        .stateIn(
            scope = this,
            started = SharingStarted.Lazily,
            initialValue = ""
        )

    fun resetNotificationExpireTime() {
        launch {
            interactor.resetNotificationExpireTime()
        }
    }
}
