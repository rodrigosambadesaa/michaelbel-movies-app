package org.michaelbel.movies.ui.ktx

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun navigateToImageUri(): (uri: String) -> Unit = { uri ->
    val nsUrl = NSURL.URLWithString(uri)
    if (nsUrl != null) {
        UIApplication.sharedApplication().openURL(
            nsUrl,
            options = emptyMap<Any?, Any?>(),
            completionHandler = null
        )
    }
}
