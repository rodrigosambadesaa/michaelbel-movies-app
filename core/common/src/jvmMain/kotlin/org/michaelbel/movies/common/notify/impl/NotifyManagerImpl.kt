package org.michaelbel.movies.common.notify.impl

import org.michaelbel.movies.common.notify.NotifyManager

class NotifyManagerImpl: NotifyManager {

    override val areNotificationsEnabled: Boolean = false

    override val isDoNotDisturbAccessGranted: Boolean = false

    override val isDoNotDisturbEnabled: Boolean = false

    override fun setDoNotDisturbEnabled(enabled: Boolean) {}
}
