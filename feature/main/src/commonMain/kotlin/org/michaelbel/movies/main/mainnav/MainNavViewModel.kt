package org.michaelbel.movies.main.mainnav

import kotlinx.coroutines.launch
import org.michaelbel.movies.common.exceptions.AccountDetailsException
import org.michaelbel.movies.common.exceptions.CreateSessionException
import org.michaelbel.movies.common.mvi.MoviesViewModel
import org.michaelbel.movies.common.mvi.model.EmptyModel
import org.michaelbel.movies.feed.event.FeedAppEvent
import org.michaelbel.movies.interactor.Interactor
import org.michaelbel.movies.main.intent.MainIntent
import org.michaelbel.movies.main.mainnav.event.MainNavEvent
import org.michaelbel.movies.ui.strings.MoviesStrings

class MainNavViewModel(
    private val interactor: Interactor
): MoviesViewModel<EmptyModel, MainIntent, MainNavEvent>(EmptyModel) {

    override fun dispatch(intent: MainIntent) {
        when (intent) {
            is MainIntent.FeedReselected -> launch { FeedAppEvent.push(FeedAppEvent.Event.ReselectFeed) }
        }
    }

    override fun catch(throwable: Throwable) {
        when (throwable) {
            is CreateSessionException -> launch { push(MainNavEvent.ShowSnackbar(MoviesStrings.feed_auth_failure)) }
            is AccountDetailsException -> launch { push(MainNavEvent.ShowSnackbar(MoviesStrings.feed_auth_failure)) }
            else -> super.catch(throwable)
        }
    }

    fun onRedirect(requestToken: String?, approved: Boolean?) {
        if (requestToken == null || approved == null) return
        if (!approved) {
            launch { push(MainNavEvent.ShowSnackbar(MoviesStrings.feed_auth_failure)) }
            return
        }
        authorizeAccount(requestToken)
    }

    private fun authorizeAccount(requestToken: String) {
        launch {
            interactor.run {
                createSession(requestToken)
                accountDetails()
                push(MainNavEvent.ShowSnackbar(MoviesStrings.feed_auth_success))
            }
        }
    }
}
