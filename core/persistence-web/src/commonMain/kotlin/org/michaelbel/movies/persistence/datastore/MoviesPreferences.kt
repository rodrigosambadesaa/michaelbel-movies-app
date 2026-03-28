package org.michaelbel.movies.persistence.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class MoviesPreferences {

    private val store = MutableStateFlow<Map<String, Any?>>(emptyMap())

    fun <T> getValueFlow(key: PreferenceKey<T>): Flow<T?> {
        @Suppress("UNCHECKED_CAST")
        return store.map { it[key.name] as? T }
    }

    suspend fun <T> getValue(key: PreferenceKey<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return store.value[key.name] as? T
    }

    suspend fun <T> setValue(key: PreferenceKey<T>, value: T) {
        store.value = store.value + (key.name to value)
    }

    suspend fun <T> removeValue(key: PreferenceKey<T>) {
        store.value = store.value - key.name
    }

    suspend fun removeValues(vararg keys: PreferenceKey<*>) {
        store.value = store.value - keys.map { it.name }.toSet()
    }

    sealed class PreferenceKey<T>(val name: String) {
        data object PreferenceThemeKey: PreferenceKey<String>("theme")
        data object PreferenceFeedViewKey: PreferenceKey<String>("feed_view")
        data object PreferenceMovieListKey: PreferenceKey<String>("movie_list")
        data object PreferenceDynamicColorsKey: PreferenceKey<Boolean>("dynamic_colors")
        data object PreferencePaletteColorsKey: PreferenceKey<Boolean>("palette_colors")
        data object PreferenceSessionIdKey: PreferenceKey<String>("session_id")
        data object PreferenceAccountKey: PreferenceKey<Int>("account_id")
        data object PreferenceAccountExpireTimeKey: PreferenceKey<Long>("account_expire_time")
        data object PreferenceNotificationExpireTimeKey: PreferenceKey<Long>("notification_expire_time")
        data object PreferenceBiometricKey: PreferenceKey<Boolean>("biometric")
        data object PreferencePaletteKey: PreferenceKey<Int>("palette")
        data object PreferenceSeedColorKey: PreferenceKey<Int>("seed_color")
        data object PreferenceScreenshotBlockKey: PreferenceKey<Boolean>("screenshot_block")
    }
}
