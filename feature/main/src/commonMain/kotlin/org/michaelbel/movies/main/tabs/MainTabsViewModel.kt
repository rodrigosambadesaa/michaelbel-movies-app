package org.michaelbel.movies.main.tabs

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.michaelbel.movies.common.exceptions.AccountDetailsException
import org.michaelbel.movies.common.exceptions.CreateSessionException
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.feed.event.FeedEvent
import org.michaelbel.movies.feed.event.FeedEventManager
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.main.event.MainEvent
import org.michaelbel.movies.main.tabs.event.MainTabsEvent
import org.michaelbel.movies.main.tabs.event.MainTabsEventManager
import org.michaelbel.movies.main.tabs.intent.MainTabsIntent
import org.michaelbel.movies.main.tabs.model.MainTabsModel
import org.michaelbel.movies.persistence.database.ktx.isNotEmpty
import org.michaelbel.movies.ui.navigation.AuthDestination
import org.michaelbel.movies.ui.navigation.MainNavigator
import org.michaelbel.movies.ui.pending.PendingAction
import org.michaelbel.movies.ui.pending.PendingActionStore
import org.michaelbel.movies.ui.strings.MoviesStrings

class MainTabsViewModel(
    private val interactor: Interactor,
    private val uiInteractor: UiInteractor
): MoviesViewModel<MainTabsModel, MainTabsIntent, MainTabsEvent>(MainTabsModel()) {

    init {
        dispatch(MainTabsIntent.CollectFaveFeatureEnabled)
        dispatch(MainTabsIntent.CollectAuthorizedState)
    }

    override fun dispatch(intent: MainTabsIntent) {
        when (intent) {
            is MainTabsIntent.CollectFaveFeatureEnabled -> {
                reduce { it.copy(isFaveFeatureEnabled = uiInteractor.isFaveFeatureEnabled) }
            }
            is MainTabsIntent.CollectAuthorizedState -> {
                launch {
                    interactor.accountPojoFlow.collectLatest { accountPojo ->
                        val isAuthorized = accountPojo.isNotEmpty
                        if (isAuthorized && !stateFlow.value.isAuthorized) {
                            when (val pendingAuthAction = PendingActionStore.action) {
                                PendingAction.OpenFave -> {
                                    PendingActionStore.clear()
                                    MainTabsEventManager.push(MainEvent.OpenFave)
                                }
                                is PendingAction.AddFavorite -> {
                                    PendingActionStore.clear()
                                    interactor.updateFavorite(pendingAuthAction.movieId, favorite = true)
                                }
                                else -> Unit
                            }
                        }
                        reduce { it.copy(isAuthorized = isAuthorized) }
                    }
                }
            }
            is MainTabsIntent.HandleRedirect -> {
                when {
                    intent.requestToken == null || intent.approved == null -> Unit
                    !intent.approved -> {
                        PendingActionStore.clear()
                        launch { push(MainTabsEvent.ShowSnackbar(MoviesStrings.feed_auth_failure)) }
                    }
                    else -> dispatch(MainTabsIntent.AuthorizeAccount(intent.requestToken))
                }
            }
            is MainTabsIntent.AuthorizeAccount -> {
                launch {
                    interactor.run {
                        createSession(intent.requestToken)
                        accountDetails()
                    }
                    push(MainTabsEvent.ShowSnackbar(MoviesStrings.feed_auth_success))
                    MainNavigator.back()
                }
            }
            is MainTabsIntent.FeedClick -> {
                launch { FeedEventManager.push(FeedEvent.ReselectFeed) }
                launch { MainTabsEventManager.push(MainEvent.OpenFeed) }
            }
            is MainTabsIntent.FaveClick -> {
                launch {
                    when {
                        stateFlow.value.isAuthorized -> {
                            PendingActionStore.clear()
                            MainTabsEventManager.push(MainEvent.OpenFave)
                        }
                        else -> {
                            PendingActionStore.set(PendingAction.OpenFave)
                            MainNavigator.forward(AuthDestination)
                        }
                    }
                }
            }
            is MainTabsIntent.SettingsClick -> launch { MainTabsEventManager.push(MainEvent.OpenSettings) }
        }
    }

    override fun catch(throwable: Throwable) {
        when (throwable) {
            is CreateSessionException -> launch { push(MainTabsEvent.ShowSnackbar(MoviesStrings.feed_auth_failure)) }
            is AccountDetailsException -> launch { push(MainTabsEvent.ShowSnackbar(MoviesStrings.feed_auth_failure)) }
            else -> super.catch(throwable)
        }
    }
}
