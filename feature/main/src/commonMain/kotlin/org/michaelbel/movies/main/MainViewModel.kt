package org.michaelbel.movies.main

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.michaelbel.movies.analytics.MoviesAnalytics
import org.michaelbel.movies.common.biometric.BiometricInteractor
import org.michaelbel.movies.common.biometric.BiometricListener
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.feed.event.FeedEvent
import org.michaelbel.movies.feed.event.FeedEventManager
import org.michaelbel.movies.interactor.DebugNotificationInteractor
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.main.event.MainEvent
import org.michaelbel.movies.main.intent.MainIntent
import org.michaelbel.movies.main.model.MainModel
import org.michaelbel.movies.main.tabs.event.MainTabsEventManager
import org.michaelbel.movies.platform.config.ConfigService
import org.michaelbel.movies.platform.messaging.MessagingService
import org.michaelbel.movies.platform.review.ReviewService
import org.michaelbel.movies.platform.update.UpdateService
import org.michaelbel.movies.ui.isDebug
import org.michaelbel.movies.ui.navigation.DetailsDestination
import org.michaelbel.movies.ui.navigation.MainDestination
import org.michaelbel.movies.ui.navigation.MainNavigator
import org.michaelbel.movies.work.WorkManagerInteractor

class MainViewModel(
    private val interactor: Interactor,
    private val biometricController: BiometricInteractor,
    private val analytics: MoviesAnalytics,
    private val messagingService: MessagingService,
    private val workManagerInteractor: WorkManagerInteractor,
    private val debugNotificationInteractor: DebugNotificationInteractor,
    private val configService: ConfigService,
    private val reviewService: ReviewService,
    private val updateService: UpdateService,
): MoviesViewModel<MainModel, MainIntent, MainEvent>(MainModel()) {

    init {
        dispatch(MainIntent.CollectThemeData)
        dispatch(MainIntent.CollectScreenshotBlockEnabled)
        dispatch(MainIntent.FetchBiometric)
        dispatch(MainIntent.FetchRemoteConfig)
        dispatch(MainIntent.FetchFirebaseMessagingToken)
        dispatch(MainIntent.PrepopulateDatabase)
        dispatch(MainIntent.UpdateAccountDetails)
        dispatch(MainIntent.ShowDebugNotification)
    }

    override fun dispatch(intent: MainIntent) {
        when (intent) {
            is MainIntent.OpenFeed -> launch { MainTabsEventManager.push(MainEvent.OpenFeed) }
            is MainIntent.OpenSettings -> launch { MainTabsEventManager.push(MainEvent.OpenSettings) }
            is MainIntent.CollectThemeData -> {
                launch {
                    interactor.themeData.collectLatest { themeData ->
                        reduce { it.copy(themeData = themeData) }
                    }
                }
            }
            is MainIntent.CollectScreenshotBlockEnabled -> {
                launch {
                    interactor.isScreenshotBlockEnabled.collectLatest { isScreenshotBlockEnabled ->
                        reduce { it.copy(isScreenshotBlockEnabled = isScreenshotBlockEnabled) }
                    }
                }
            }
            is MainIntent.RequestReview -> reviewService.requestReview(intent.activity)
            is MainIntent.RequestUpdate -> updateService.startUpdate(intent.activity)
            is MainIntent.FetchBiometric -> {
                launch {
                    val isBiometricEnabled = interactor.isBiometricEnabledAsync()
                    reduce { it.copy(splashLoading = isBiometricEnabled) }
                    if (isBiometricEnabled) {
                        push(MainEvent.BiometricAuthenticate)
                    }
                }
            }
            is MainIntent.FetchRemoteConfig -> launch { configService.fetchAndActivate() }
            is MainIntent.FetchFirebaseMessagingToken -> {
                /*messagingService.setTokenListener(object: TokenListener {
                    override fun onNewToken(token: String) {
                        printlnDebug("firebase messaging token: $token")
                    }
                })*/
            }
            is MainIntent.PrepopulateDatabase -> workManagerInteractor.prepopulateDatabase()
            is MainIntent.UpdateAccountDetails -> workManagerInteractor.updateAccountDetails()
            is MainIntent.ShowDebugNotification -> {
                if (isDebug) {
                    debugNotificationInteractor.showDebugNotification()
                }
            }
            is MainIntent.Authenticate -> {
                val biometricListener = object: BiometricListener {
                    override fun onSuccess() {
                        reduce { it.copy(splashLoading = false) }
                    }

                    override fun onCancel() {
                        launch { push(MainEvent.BiometricCancel) }
                    }
                }
                biometricController.authenticate(intent.activity, biometricListener)
            }
            is MainIntent.NavigateToDetails -> launch { MainNavigator.forward(DetailsDestination(movieList = null, movieId = intent.movieId)) }
            is MainIntent.NavigateToMain -> {
                launch {
                    MainNavigator.back()
                    MainNavigator.forward(MainDestination(intent.requestToken, intent.approved))
                }
            }
            is MainIntent.ShortcutSearchClick -> {
                launch {
                    MainNavigator.forward(MainDestination())
                    dispatch(MainIntent.OpenFeed)
                    FeedEventManager.push(FeedEvent.OpenSearch)
                }
            }
            is MainIntent.ShortcutSettingsClick -> {
                launch {
                    MainNavigator.forward(MainDestination())
                    dispatch(MainIntent.OpenSettings)
                }
            }
            is MainIntent.NavigateToDebug -> reduce { it.copy(openDebugSheet = true) }
            is MainIntent.ConsumeDebugNavigation -> reduce { it.copy(openDebugSheet = false) }
        }
    }
}
