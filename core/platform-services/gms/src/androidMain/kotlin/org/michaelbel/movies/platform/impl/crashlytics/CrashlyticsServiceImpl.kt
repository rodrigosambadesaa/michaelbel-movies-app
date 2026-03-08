package org.michaelbel.movies.platform.impl.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.michaelbel.movies.platform.crashlytics.CrashlyticsService

class CrashlyticsServiceImpl: CrashlyticsService {

    private val firebaseCrashlytics: FirebaseCrashlytics?
        get() = runCatching { FirebaseCrashlytics.getInstance() }.getOrNull()

    override fun recordException(priority: Int, tag: String, message: String, exception: Throwable) {
        runCatching {
            firebaseCrashlytics?.run {
                setCustomKey(CRASHLYTICS_KEY_PRIORITY, priority)
                setCustomKey(CRASHLYTICS_KEY_TAG, tag)
                setCustomKey(CRASHLYTICS_KEY_MESSAGE, message)
                recordException(exception)
            }
        }
    }

    private companion object {
        private const val CRASHLYTICS_KEY_PRIORITY = "priority"
        private const val CRASHLYTICS_KEY_TAG = "tag"
        private const val CRASHLYTICS_KEY_MESSAGE = "message"
    }
}
