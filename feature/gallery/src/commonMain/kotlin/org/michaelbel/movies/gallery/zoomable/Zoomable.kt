package org.michaelbel.movies.gallery.zoomable

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.launch
import kotlin.math.abs

private suspend fun PointerInputScope.detectTransformGestures(
    onGesture: (centroid: Offset, pan: Offset, zoom: Float, timeMillis: Long) -> Boolean,
    onGestureStart: () -> Unit = {},
    onGestureEnd: () -> Unit = {},
    onTap: (position: Offset) -> Unit = {},
    onDoubleTap: (position: Offset) -> Unit = {},
    enableOneFingerZoom: Boolean = true,
) = awaitEachGesture {
    val firstDown = awaitFirstDown(requireUnconsumed = false)
    onGestureStart()

    var firstUp: PointerInputChange = firstDown
    var isTap = true
    val touchSlop = TouchSlop(viewConfiguration.touchSlop)
    forEachPointerEventUntilReleased { event ->
        if (touchSlop.isPast(event)) {
            val zoomChange = event.calculateZoom()
            val panChange = event.calculatePan()
            if (zoomChange != 1F || panChange != Offset.Zero) {
                val centroid = event.calculateCentroid(useCurrent = false)
                val timeMillis = event.changes[0].uptimeMillis
                val canConsume = onGesture(centroid, panChange, zoomChange, timeMillis)
                if (canConsume) {
                    event.consumePositionChanges()
                }
            }
            isTap = false
        }
        if (event.changes.size > 1) {
            isTap = false
        }
        firstUp = event.changes[0]
    }

    if (firstUp.uptimeMillis - firstDown.uptimeMillis > viewConfiguration.longPressTimeoutMillis) {
        isTap = false
    }

    if (isTap) {
        val secondDown = awaitSecondDown(firstUp)
        if (secondDown == null) {
            onTap(firstUp.position)
        } else {
            var isDoubleTap = true
            var secondUp: PointerInputChange = secondDown
            val secondTouchSlop = TouchSlop(viewConfiguration.touchSlop)
            forEachPointerEventUntilReleased { event ->
                if (secondTouchSlop.isPast(event)) {
                    if (enableOneFingerZoom) {
                        val panChange = event.calculatePan()
                        val zoomChange = 1F + panChange.y * .004F
                        if (zoomChange != 1F) {
                            val centroid = event.calculateCentroid(useCurrent = false)
                            val timeMillis = event.changes[0].uptimeMillis
                            val canConsume = onGesture(centroid, Offset.Zero, zoomChange, timeMillis)
                            if (canConsume) {
                                event.consumePositionChanges()
                            }
                        }
                    }
                    isDoubleTap = false
                }
                if (event.changes.size > 1) {
                    isDoubleTap = false
                }
                secondUp = event.changes[0]
            }

            if (secondUp.uptimeMillis - secondDown.uptimeMillis > viewConfiguration.longPressTimeoutMillis) {
                isDoubleTap = false
            }

            if (isDoubleTap) {
                onDoubleTap(secondUp.position)
            }
        }
    }
    onGestureEnd()
}

private suspend fun AwaitPointerEventScope.forEachPointerEventUntilReleased(
    action: (PointerEvent) -> Unit,
) {
    do {
        val event = awaitPointerEvent()
        if (event.changes.fastAny { it.isConsumed }) {
            break
        }
        action(event)
    } while (event.changes.fastAny { it.pressed })
}

private suspend fun AwaitPointerEventScope.awaitSecondDown(
    firstUp: PointerInputChange
): PointerInputChange? = withTimeoutOrNull(viewConfiguration.doubleTapTimeoutMillis) {
    val minUptime = firstUp.uptimeMillis + viewConfiguration.doubleTapMinTimeMillis
    var change: PointerInputChange
    do {
        change = awaitFirstDown()
    } while (change.uptimeMillis < minUptime)
    change
}

private fun PointerEvent.consumePositionChanges() {
    changes.fastForEach {
        if (it.positionChanged()) {
            it.consume()
        }
    }
}

private class TouchSlop(private val threshold: Float) {
    private var zoom = 1F
    private var pan = Offset.Zero
    private var _isPast = false


    fun isPast(event: PointerEvent): Boolean {
        if (_isPast) {
            return true
        }

        zoom *= event.calculateZoom()
        pan += event.calculatePan()
        val zoomMotion = abs(1 - zoom) * event.calculateCentroidSize(useCurrent = false)
        val panMotion = pan.getDistance()
        _isPast = zoomMotion > threshold || panMotion > threshold

        return _isPast
    }
}

fun Modifier.zoomable(
    zoomState: ZoomState,
    enableOneFingerZoom: Boolean = true,
    onTap: (position: Offset) -> Unit = {},
    onDoubleTap: suspend (position: Offset) -> Unit = { position -> zoomState.toggleScale(2.5F, position) }
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "zoomable"
        properties["zoomState"] = zoomState
    }
) {
    val scope = rememberCoroutineScope()
    Modifier
        .onSizeChanged { size ->
            zoomState.setLayoutSize(size.toSize())
        }
        .pointerInput(zoomState) {
            detectTransformGestures(
                onGestureStart = { zoomState.startGesture() },
                onGesture = { centroid, pan, zoom, timeMillis ->
                    val canConsume = zoomState.canConsumeGesture(pan = pan, zoom = zoom)
                    if (canConsume) {
                        scope.launch {
                            zoomState.applyGesture(
                                pan = pan,
                                zoom = zoom,
                                position = centroid,
                                timeMillis = timeMillis,
                            )
                        }
                    }
                    canConsume
                },
                onGestureEnd = {
                    scope.launch {
                        zoomState.endGesture()
                    }
                },
                onTap = onTap,
                onDoubleTap = { position ->
                    scope.launch {
                        onDoubleTap(position)
                    }
                },
                enableOneFingerZoom = enableOneFingerZoom,
            )
        }
        .graphicsLayer {
            scaleX = zoomState.scale
            scaleY = zoomState.scale
            translationX = zoomState.offsetX
            translationY = zoomState.offsetY
        }
}

suspend fun ZoomState.toggleScale(
    targetScale: Float = 2.5F,
    position: Offset,
    animationSpec: AnimationSpec<Float> = spring(),
) {
    val newScale = if (scale == 1F) targetScale else 1F
    changeScale(newScale, position, animationSpec)
}
