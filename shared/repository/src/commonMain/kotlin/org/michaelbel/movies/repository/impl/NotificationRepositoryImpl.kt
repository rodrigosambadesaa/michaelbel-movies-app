@file:OptIn(ExperimentalTime::class)

package org.michaelbel.movies.repository.impl

import org.michaelbel.movies.persistence.datastore.MoviesPreferences
import org.michaelbel.movies.repository.NotificationRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class NotificationRepositoryImpl(
    private val preferences: MoviesPreferences
): NotificationRepository {

    override suspend fun updateNotificationExpireTime() {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        preferences.setValue(MoviesPreferences.PreferenceKey.PreferenceNotificationExpireTimeKey, currentTime)
    }
}
