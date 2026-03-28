package org.michaelbel.movies.common.log

import kotlin.js.console

actual fun log(throwable: Throwable) {
    console.error(throwable.toString())
}

actual fun log(message: String) {
    console.log(message)
}
