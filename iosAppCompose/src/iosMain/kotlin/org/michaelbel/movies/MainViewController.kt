@file:Suppress("unused", "FunctionName")

package org.michaelbel.movies

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    return ComposeUIViewController { IosMainContent() }
}
