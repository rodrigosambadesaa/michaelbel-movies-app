@file:SuppressLint("MissingPermission")

package org.michaelbel.movies.interactor.impl

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import org.michaelbel.movies.common.ktx.isPostNotificationsPermissionGranted
import org.michaelbel.movies.common.ktx.notificationManager
import org.michaelbel.movies.interactor.DebugNotificationInteractor
import org.michaelbel.movies.ui.icons.MoviesAndroidIcons
import org.michaelbel.movies.ui.navigation.DEBUG_DEEP_LINK_EXTRA
import org.michaelbel.movies.ui.navigation.DEBUG_DEEP_LINK_URI

class DebugNotificationInteractorImpl(
    private val context: Context
): DebugNotificationInteractor {

    override fun showDebugNotification() {
        createChannel()

        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        ).apply {
            priority = NotificationCompat.PRIORITY_MIN
            setContentTitle(NOTIFICATION_TITLE)
            setContentText(NOTIFICATION_DESCRIPTION)
            setSmallIcon(MoviesAndroidIcons.MovieFilter24)
            setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            setGroupSummary(true)
            setGroup(GROUP_NAME)
            setContentIntent(pendingIntent())
            setAutoCancel(true)
            setSound(null)
            setVibrate(null)
            setSilent(true)
        }.build()

        if (context.isPostNotificationsPermissionGranted) {
            context.notificationManager.notify(TAG, ID, notification)
        }
    }

    private fun createChannel() {
        val notificationChannel = NotificationChannelCompat.Builder(
            CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_MIN
        ).apply {
            setName(CHANNEL_NAME)
            setDescription(CHANNEL_DESCRIPTION)
            setShowBadge(false)
        }.build()
        context.notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun pendingIntent(): PendingIntent {
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.setAction(Intent.ACTION_VIEW)
            ?.setData(DEBUG_DEEP_LINK_URI.toUri())
            ?.putExtra(DEBUG_DEEP_LINK_EXTRA, true)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            ?: Intent(
                Intent.ACTION_VIEW,
                DEBUG_DEEP_LINK_URI.toUri()
            ).apply {
                setPackage(context.packageName)
                putExtra(DEBUG_DEEP_LINK_EXTRA, true)
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

        return PendingIntent.getActivity(
            context,
            ID,
            launchIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private companion object {
        private const val TAG = "debug notification tag"
        private const val ID = 420
        private const val GROUP_NAME = "Debug"
        private const val CHANNEL_ID = "debug_channel_id"
        private const val CHANNEL_NAME = "Debug Settings"
        private const val CHANNEL_DESCRIPTION = "Debug Settings"
        private const val NOTIFICATION_TITLE = "Movies Debug Settings"
        private const val NOTIFICATION_DESCRIPTION = "Click to open"
    }
}
