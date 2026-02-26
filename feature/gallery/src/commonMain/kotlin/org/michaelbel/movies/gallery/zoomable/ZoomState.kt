package org.michaelbel.movies.gallery.zoomable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

@Stable
class ZoomState(
    private val maxScale: Float = 5F,
    private var contentSize: Size = Size.Zero,
    private val velocityDecay: DecayAnimationSpec<Float> = exponentialDecay(),
) {
    init {
        require(maxScale >= 1F) { "maxScale must be at least 1.0." }
    }

    private var _scale = Animatable(1F).apply {
        updateBounds(.9F, maxScale)
    }

    val scale: Float
        get() = _scale.value

    private var _offsetX = Animatable(0F)

    val offsetX: Float
        get() = _offsetX.value

    private var _offsetY = Animatable(0F)

    val offsetY: Float
        get() = _offsetY.value

    private var layoutSize = Size.Zero

    val isScaled: Boolean
        get() = scale != 1F

    fun setLayoutSize(size: Size) {
        layoutSize = size
        updateFitContentSize()
    }

    fun setContentSize(size: Size) {
        contentSize = size
        updateFitContentSize()
    }

    private var fitContentSize = Size.Zero
    private fun updateFitContentSize() {
        if (layoutSize == Size.Zero) {
            fitContentSize = Size.Zero
            return
        }

        if (contentSize == Size.Zero) {
            fitContentSize = layoutSize
            return
        }

        val contentAspectRatio = contentSize.width / contentSize.height
        val layoutAspectRatio = layoutSize.width / layoutSize.height

        fitContentSize = if (contentAspectRatio > layoutAspectRatio) {
            contentSize * (layoutSize.width / contentSize.width)
        } else {
            contentSize * (layoutSize.height / contentSize.height)
        }
    }

    suspend fun reset(
        animationSpec: AnimationSpec<Float> = spring()
    ) = coroutineScope {
        launch {
            _offsetX.updateBounds(null, null)
            _offsetX.animateTo(0F, animationSpec)
            _offsetX.updateBounds(0F, 0F)
        }
        launch {
            _offsetX.updateBounds(null, null)
            _offsetY.animateTo(0F, animationSpec)
            _offsetY.updateBounds(0F, 0F)
        }
        _scale.animateTo(1F, animationSpec)
    }

    private var shouldConsumeEvent: Boolean? = null
    private val velocityTracker = VelocityTracker()

    fun startGesture() {
        shouldConsumeEvent = null
        velocityTracker.resetTracking()
    }

    fun canConsumeGesture(pan: Offset, zoom: Float): Boolean {
        return shouldConsumeEvent ?: run {
            var consume = true
            if (zoom == 1F) {
                if (scale == 1F) {
                    consume = false
                } else {
                    val ratio = (abs(pan.x) / abs(pan.y))
                    if (ratio > 3) {
                        if ((pan.x < 0) && (_offsetX.value == _offsetX.lowerBound)) {
                            consume = false
                        }
                        if ((pan.x > 0) && (_offsetX.value == _offsetX.upperBound)) {
                            consume = false
                        }
                    } else if (ratio < .33) {
                        if ((pan.y < 0) && (_offsetY.value == _offsetY.lowerBound)) {
                            consume = false
                        }
                        if ((pan.y > 0) && (_offsetY.value == _offsetY.upperBound)) {
                            consume = false
                        }
                    }
                }
            }
            shouldConsumeEvent = consume
            consume
        }
    }

    suspend fun applyGesture(
        pan: Offset,
        zoom: Float,
        position: Offset,
        timeMillis: Long
    ) = coroutineScope {
        val newScale = (scale * zoom).coerceIn(.9F, maxScale)
        val newOffset = calculateNewOffset(newScale, position, pan)
        val newBounds = calculateNewBounds(newScale)

        _offsetX.updateBounds(newBounds.left, newBounds.right)
        launch {
            _offsetX.snapTo(newOffset.x)
        }

        _offsetY.updateBounds(newBounds.top, newBounds.bottom)
        launch {
            _offsetY.snapTo(newOffset.y)
        }

        launch {
            _scale.snapTo(newScale)
        }

        if (zoom == 1F) {
            velocityTracker.addPosition(timeMillis, position)
        } else {
            velocityTracker.resetTracking()
        }
    }

    suspend fun changeScale(
        targetScale: Float,
        position: Offset,
        animationSpec: AnimationSpec<Float> = spring(),
    ) = coroutineScope {
        val newScale = targetScale.coerceIn(1F, maxScale)
        val newOffset = calculateNewOffset(newScale, position, Offset.Zero)
        val newBounds = calculateNewBounds(newScale)

        val x = newOffset.x.coerceIn(newBounds.left, newBounds.right)
        launch {
            _offsetX.updateBounds(null, null)
            _offsetX.animateTo(x, animationSpec)
            _offsetX.updateBounds(newBounds.left, newBounds.right)
        }

        val y = newOffset.y.coerceIn(newBounds.top, newBounds.bottom)
        launch {
            _offsetY.updateBounds(null, null)
            _offsetY.animateTo(y, animationSpec)
            _offsetY.updateBounds(newBounds.top, newBounds.bottom)
        }

        launch {
            _scale.animateTo(newScale, animationSpec)
        }
    }

    private fun calculateNewOffset(
        newScale: Float,
        position: Offset,
        pan: Offset,
    ): Offset {
        val size = fitContentSize * scale
        val newSize = fitContentSize * newScale
        val deltaWidth = newSize.width - size.width
        val deltaHeight = newSize.height - size.height

        val xInContent = position.x - offsetX + (size.width - layoutSize.width) * .5F
        val yInContent = position.y - offsetY + (size.height - layoutSize.height) * .5F
        val deltaX = (deltaWidth * .5F) - (deltaWidth * xInContent / size.width)
        val deltaY = (deltaHeight * .5F) - (deltaHeight * yInContent / size.height)

        val x = offsetX + pan.x + deltaX
        val y = offsetY + pan.y + deltaY

        return Offset(x, y)
    }

    private fun calculateNewBounds(
        newScale: Float,
    ): Rect {
        val newSize = fitContentSize * newScale
        val boundX = max((newSize.width - layoutSize.width), 0F) * .5F
        val boundY = max((newSize.height - layoutSize.height), 0F) * .5F
        return Rect(-boundX, -boundY, boundX, boundY)
    }

    suspend fun endGesture() = coroutineScope {
        val velocity = velocityTracker.calculateVelocity()
        if (velocity.x != 0F) {
            launch {
                _offsetX.animateDecay(velocity.x, velocityDecay)
            }
        }
        if (velocity.y != 0F) {
            launch {
                _offsetY.animateDecay(velocity.y, velocityDecay)
            }
        }

        if (_scale.value < 1F) {
            launch {
                _scale.animateTo(1F)
            }
        }
    }

    suspend fun centerByContentCoordinate(
        offset: Offset,
        scale: Float = 3F,
        animationSpec: AnimationSpec<Float> = tween(700),
    ) = coroutineScope {
        val fitContentSizeFactor = fitContentSize.width / contentSize.width

        val boundX = max((fitContentSize.width * scale - layoutSize.width), 0f) / 2F
        val boundY = max((fitContentSize.height * scale - layoutSize.height), 0f) / 2F

        suspend fun executeZoomWithAnimation() {
            listOf(
                async {
                    val fixedTargetOffsetX =
                        ((fitContentSize.width / 2 - offset.x * fitContentSizeFactor) * scale)
                            .coerceIn(
                                minimumValue = -boundX,
                                maximumValue = boundX,
                            )
                    _offsetX.animateTo(fixedTargetOffsetX, animationSpec)
                },
                async {
                    val fixedTargetOffsetY = ((fitContentSize.height / 2 - offset.y * fitContentSizeFactor) * scale)
                        .coerceIn(minimumValue = -boundY, maximumValue = boundY)
                    _offsetY.animateTo(fixedTargetOffsetY, animationSpec)
                },
                async {
                    _scale.animateTo(scale, animationSpec)
                },
            ).awaitAll()
        }

        if (scale > _scale.value) {
            _offsetX.updateBounds(-boundX, boundX)
            _offsetY.updateBounds(-boundY, boundY)
            executeZoomWithAnimation()
        } else {
            executeZoomWithAnimation()
            _offsetX.updateBounds(-boundX, boundX)
            _offsetY.updateBounds(-boundY, boundY)
        }
    }

    suspend fun centerByLayoutCoordinate(
        offset: Offset,
        scale: Float = 3F,
        animationSpec: AnimationSpec<Float> = tween(700),
    ) = coroutineScope {
        val boundX = max((fitContentSize.width * scale - layoutSize.width), 0f) / 2F
        val boundY = max((fitContentSize.height * scale - layoutSize.height), 0f) / 2F

        suspend fun executeZoomWithAnimation() {
            listOf(
                async {
                    val fixedTargetOffsetX =
                        ((layoutSize.width / 2 - offset.x) * scale)
                            .coerceIn(
                                minimumValue = -boundX,
                                maximumValue = boundX,
                            )
                    _offsetX.animateTo(fixedTargetOffsetX, animationSpec)
                },
                async {
                    val fixedTargetOffsetY = ((layoutSize.height / 2 - offset.y) * scale)
                        .coerceIn(minimumValue = -boundY, maximumValue = boundY)
                    _offsetY.animateTo(fixedTargetOffsetY, animationSpec)
                },
                async {
                    _scale.animateTo(scale, animationSpec)
                },
            ).awaitAll()
        }

        if (scale > _scale.value) {
            _offsetX.updateBounds(-boundX, boundX)
            _offsetY.updateBounds(-boundY, boundY)
            executeZoomWithAnimation()
        } else {
            executeZoomWithAnimation()
            _offsetX.updateBounds(-boundX, boundX)
            _offsetY.updateBounds(-boundY, boundY)
        }
    }
}

@Composable
fun rememberZoomState(
    maxScale: Float = 5F,
    contentSize: Size = Size.Zero,
    velocityDecay: DecayAnimationSpec<Float> = exponentialDecay()
) = remember {
    ZoomState(maxScale, contentSize, velocityDecay)
}
