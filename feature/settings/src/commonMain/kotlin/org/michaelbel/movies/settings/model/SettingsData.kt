package org.michaelbel.movies.settings.model

import org.michaelbel.movies.ui.appicon.IconAlias

@Deprecated("")
data class SettingsData(
    val notificationsData: NotificationsData,
    val tileData: RequestedData,
    val appIconData: ListData<IconAlias>,
    val githubData: RequestedData
) {
    interface Listed<T> {
        val current: T
        val onSelect: (T) -> Unit
    }

    interface Requested {
        val onRequest: () -> Unit
    }

    data class ListData<T>(
        override val current: T,
        override val onSelect: (T) -> Unit = {}
    ): Listed<T>

    data class RequestedData(
        override val onRequest: () -> Unit = {}
    ): Requested

    data class NotificationsData(
        val isEnabled: Boolean,
        val onClick: () -> Unit = {}
    )

    data class AboutData(
        val versionName: String,
        val versionCode: Long,
        val flavor: String,
        val isDebug: Boolean
    )
}