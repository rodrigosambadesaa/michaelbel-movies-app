package org.michaelbel.movies.debug.intent

import org.michaelbel.movies.common.mvi.Intent

sealed interface DebugIntent: Intent {
    data object DismissRequest: DebugIntent
    data object CollectThemeData: DebugIntent
    data object CollectFirebaseTokenFeatureEnabled: DebugIntent
    data object CollectFirebaseToken: DebugIntent
    data object ResetNotificationExpireTime: DebugIntent
}
