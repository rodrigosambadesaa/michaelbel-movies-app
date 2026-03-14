@file:Suppress("unused", "FunctionName")

package org.michaelbel.movies

import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.UIKit.UIViewController

object IosIncomingUrlStore {
    private val mutableUrl = MutableStateFlow<String?>(null)
    val url = mutableUrl.asStateFlow()

    fun update(url: String) {
        mutableUrl.value = url
    }

    fun clear(url: String) {
        if (mutableUrl.value == url) {
            mutableUrl.value = null
        }
    }
}

fun MainViewController(): UIViewController {
    return ComposeUIViewController { IosMainContent() }
}

fun handleIncomingUrl(url: String) {
    IosIncomingUrlStore.update(url)
}
