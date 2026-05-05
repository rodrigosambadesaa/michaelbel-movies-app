package org.michaelbel.movies.common.notify

interface NotifyManager {

    val areNotificationsEnabled: Boolean

    val isDoNotDisturbAccessGranted: Boolean

    val isDoNotDisturbEnabled: Boolean

    fun setDoNotDisturbEnabled(enabled: Boolean)
}
