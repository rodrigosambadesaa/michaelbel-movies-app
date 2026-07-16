package org.michaelbel.movies.interactor.impl

import kotlinx.coroutines.withContext
import org.michaelbel.movies.common.dispatchers.SharedDispatchers
import org.michaelbel.movies.interactor.NotificationInteractor
import org.michaelbel.movies.repository.NotificationRepository

class NotificationInteractorImpl(
    private val dispatchers: SharedDispatchers,
    private val notificationRepository: NotificationRepository
): NotificationInteractor {

    override suspend fun updateNotificationExpireTime() {
        withContext(dispatchers.io) { notificationRepository.updateNotificationExpireTime() }
    }
}
