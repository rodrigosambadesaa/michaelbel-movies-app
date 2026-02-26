package org.michaelbel.movies.main.tabs

import kotlinx.coroutines.launch
import org.michaelbel.movies.common.exceptions.AccountDetailsException
import org.michaelbel.movies.common.exceptions.CreateSessionException
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.common.mvi.model.EmptyModel
import org.michaelbel.movies.feed.event.FeedEvent
import org.michaelbel.movies.feed.event.FeedEventManager
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.main.tabs.event.MainTabsEvent
import org.michaelbel.movies.main.tabs.intent.MainTabsIntent
import org.michaelbel.movies.ui.strings.MoviesStrings

class MainTabsViewModel(
    private val interactor: Interactor
): MoviesViewModel<EmptyModel, MainTabsIntent, MainTabsEvent>(EmptyModel) {

    override fun dispatch(intent: MainTabsIntent) {
        when (intent) {
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

    fun onRedirect(requestToken: String?, approved: Boolean?) {
        if (requestToken == null || approved == null) return
        if (!approved) {
            launch { push(MainTabsEvent.ShowSnackbar(MoviesStrings.feed_auth_failure)) }
            return
        }
        authorizeAccount(requestToken)
    }

    private fun authorizeAccount(requestToken: String) {
        launch {
            interactor.run {
                createSession(requestToken)
                accountDetails()
                push(MainTabsEvent.ShowSnackbar(MoviesStrings.feed_auth_success))
            }
        }
    }
}