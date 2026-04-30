package org.michaelbel.movies.ui.shortcuts

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import org.michaelbel.movies.ui.R
import org.michaelbel.movies.ui.navigation.INTENT_ACTION_SEARCH
import org.michaelbel.movies.ui.navigation.INTENT_ACTION_SETTINGS

private const val SEARCH_SHORTCUT_ID = "searchShortcutId"
private const val SETTINGS_SHORTCUT_ID = "settingsShortcutId"

/**
 * See [App Shortcuts Design Guidelines](https://commondatastorage.googleapis.com/androiddevelopers/shareables/design/app-shortcuts-design-guidelines.pdf)
 */
fun Context.installShortcuts() {
    val searchShortcut = ShortcutInfoCompat.Builder(this, SEARCH_SHORTCUT_ID)
        .setShortLabel(getString(R.string.shortcuts_search_title))
        .setLongLabel(getString(R.string.shortcuts_search_title))
        .setRank(1)
        .setIcon(IconCompat.createWithResource(this, R.drawable.ic_shortcut_search_48))
        .setIntent(Intent(Intent.ACTION_VIEW, INTENT_ACTION_SEARCH.toUri()))
        .build()
    val settingsShortcut = ShortcutInfoCompat.Builder(this, SETTINGS_SHORTCUT_ID)
        .setShortLabel(getString(R.string.shortcuts_settings_title))
        .setLongLabel(getString(R.string.shortcuts_settings_title))
        .setRank(1)
        .setIcon(IconCompat.createWithResource(this, R.drawable.ic_shortcut_settings_48))
        .setIntent(Intent(Intent.ACTION_VIEW, INTENT_ACTION_SETTINGS.toUri()))
        .build()
    ShortcutManagerCompat.pushDynamicShortcut(this, searchShortcut)
    ShortcutManagerCompat.pushDynamicShortcut(this, settingsShortcut)
}
