@file:OptIn(ExperimentalComposeUiApi::class)

package org.michaelbel.movies.ui.ktx

import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent

actual fun Modifier.onSecondaryClick(
    onClick: () -> Unit
): Modifier = onPointerEvent(PointerEventType.Press) { event ->
    if (event.buttons.isSecondaryPressed) {
        onClick()
    }
}
