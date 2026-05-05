@file:OptIn(ExperimentalForeignApi::class)

package org.michaelbel.movies.ui

import androidx.compose.foundation.layout.WindowInsets
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation
import platform.UIKit.UIScreen
import kotlin.math.roundToInt

fun iosTopAndHorizontalCutoutWindowInsets(): WindowInsets {
    return iosSafeAreaWindowInsets(includeTopInset = true)
}

fun iosHorizontalCutoutWindowInsets(): WindowInsets {
    return iosSafeAreaWindowInsets(includeTopInset = false)
}

private fun iosSafeAreaWindowInsets(includeTopInset: Boolean): WindowInsets {
    val keyWindow = UIApplication.sharedApplication().keyWindow ?: return WindowInsets(0, 0, 0, 0)
    val scale = UIScreen.mainScreen.scale
    val orientation = UIDevice.currentDevice.orientation

    return keyWindow.safeAreaInsets.useContents {
        val leftInset = (left * scale).roundToInt()
        val topInset = if (includeTopInset) (top * scale).roundToInt() else 0
        val rightInset = (right * scale).roundToInt()

        when (orientation) {
            UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> WindowInsets(leftInset, topInset, 0, 0)
            UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> WindowInsets(0, topInset, rightInset, 0)
            else -> when {
                leftInset > rightInset -> WindowInsets(leftInset, topInset, 0, 0)
                rightInset > leftInset -> WindowInsets(0, topInset, rightInset, 0)
                else -> WindowInsets(0, topInset, 0, 0)
            }
        }
    }
}
