package org.michaelbel.movies.common.log

import kotlin.js.Console

actual fun log(throwable: Throwable) {
    Console.error(throwable.toString())
}

actual fun log(message: String) {
    Console.log(message)
}
