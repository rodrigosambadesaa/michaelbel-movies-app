package org.michaelbel.movies.ui.pending

object PendingActionStore {

    var action: PendingAction? = null
        private set

    fun clear() {
        action = null
    }

    fun set(action: PendingAction) {
        this.action = action
    }
}
