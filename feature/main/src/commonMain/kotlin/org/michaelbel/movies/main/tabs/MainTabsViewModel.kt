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
import org.michaelbel.movies.main.tabs.event.MainTabsEvent
import org.michaelbel.movies.main.tabs.intent.MainTabsIntent
import org.michaelbel.movies.main.tabs.model.MainTabsModel
import org.michaelbel.movies.persistence.database.ktx.isEmpty
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
                        reduce { it.copy(isAuthorized = !accountPojo.isEmpty) }
                    }
                }
            }
            is MainTabsIntent.HandleRedirect -> {
                val requestToken = intent.requestToken
                val approved = intent.approved
                if (requestToken == null || approved == null) return
                if (!approved) {
                    launch { push(MainTabsEvent.ShowSnackbar(MoviesStrings.feed_auth_failure)) }
                    return
                }
                dispatch(MainTabsIntent.AuthorizeAccount(requestToken))
            }
            is MainTabsIntent.AuthorizeAccount -> {
                launch {
                    interactor.run {
                        createSession(intent.requestToken)
                        accountDetails()
                        push(MainTabsEvent.ShowSnackbar(MoviesStrings.feed_auth_success))
                    }
                }
            }
            is MainTabsIntent.FeedReselected -> launch { FeedEventManager.push(FeedEvent.ReselectFeed) }
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
