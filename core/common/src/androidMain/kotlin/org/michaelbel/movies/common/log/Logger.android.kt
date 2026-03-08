package org.michaelbel.movies.common.log

import timber.log.Timber

actual fun log(throwable: Throwable) {
    Timber.e(throwable)
}

actual fun log(message: String) {
    Timber.d(message)
}
