package org.michaelbel.movies.ui.ktx

import androidx.compose.runtime.Composable
import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Composable
actual fun rememberSpeechRecognitionLauncher(onInputText: (String) -> Unit): () -> Unit = {}

@Composable
actual fun shareText(text: String, title: String): () -> Unit = {
    val currentViewController = UIApplication.sharedApplication().keyWindow?.rootViewController
    val activityViewController = UIActivityViewController(listOf(text), null)
    currentViewController?.presentViewController(
        viewControllerToPresent = activityViewController,
        animated = true,
        completion = null
    )
}

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

@Composable
actual fun requestTileService(onSuccess: (String) -> Unit): () -> Unit {
    return {}
}

@Composable
actual fun rememberConnectivityClickHandler(): () -> Unit = {}
