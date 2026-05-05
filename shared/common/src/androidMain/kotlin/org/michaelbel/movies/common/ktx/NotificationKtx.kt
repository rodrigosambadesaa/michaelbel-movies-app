package org.michaelbel.movies.common.ktx

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat

val Context.notificationManager: NotificationManagerCompat
    get() = NotificationManagerCompat.from(this)

val Context.appNotificationSettingsIntent: Intent
    get() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return intent
    }
