package org.michaelbel.movies.platform.impl.messaging.ktx

import com.google.firebase.messaging.RemoteMessage
import org.michaelbel.movies.interactor.model.MoviesPush
import org.michaelbel.movies.persistence.database.ktx.orEmpty

val RemoteMessage.mapToMoviesPush: MoviesPush
    get() {
        return MoviesPush(
            notificationId = 0,
            notificationTitle = notification?.title.orEmpty(),
            notificationDescription = notification?.body.orEmpty(),
            movieId = data["movieId"]?.toIntOrNull().orEmpty()
        )
    }
