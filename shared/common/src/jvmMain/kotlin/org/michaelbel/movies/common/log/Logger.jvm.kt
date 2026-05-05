package org.michaelbel.movies.common.log

actual fun log(throwable: Throwable) {
    println(throwable.stackTraceToString())
}

actual fun log(message: String) {
    println(message)
}
