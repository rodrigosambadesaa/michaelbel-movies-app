package org.michaelbel.movies.common.log

actual fun log(throwable: Throwable) {
    println("MoviesLog (Throwable): ${throwable.message ?: throwable.toString()}")
}

actual fun log(message: String) {
    println("MoviesLog: $message")
}
