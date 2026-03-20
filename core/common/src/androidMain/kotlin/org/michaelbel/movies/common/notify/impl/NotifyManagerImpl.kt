package org.michaelbel.movies.common.notify.impl

import android.app.NotificationManager
import android.content.Context
import androidx.core.content.ContextCompat
import org.michaelbel.movies.common.ktx.notificationManager
import org.michaelbel.movies.common.notify.NotifyManager

class NotifyManagerImpl(
    private val context: Context
): NotifyManager {

    override val areNotificationsEnabled: Boolean
        get() = context.notificationManager.areNotificationsEnabled()

    override val isDoNotDisturbAccessGranted: Boolean
        get() = ContextCompat.getSystemService(context, NotificationManager::class.java)?.isNotificationPolicyAccessGranted == true

    override val isDoNotDisturbEnabled: Boolean
        get() {
            val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) ?: return false
            return when (notificationManager.currentInterruptionFilter) {
                NotificationManager.INTERRUPTION_FILTER_PRIORITY,
                NotificationManager.INTERRUPTION_FILTER_ALARMS,
                NotificationManager.INTERRUPTION_FILTER_NONE -> true
                else -> false
            }
        }

    override fun setDoNotDisturbEnabled(enabled: Boolean) {
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) ?: return
        if (!notificationManager.isNotificationPolicyAccessGranted) return
        notificationManager.setInterruptionFilter(
            if (enabled) NotificationManager.INTERRUPTION_FILTER_PRIORITY else NotificationManager.INTERRUPTION_FILTER_ALL
        )
    }
}
