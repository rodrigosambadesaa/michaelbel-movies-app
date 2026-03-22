package org.michaelbel.movies.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import org.michaelbel.movies.common.exceptions.PageEmptyException
import org.michaelbel.movies.common.platform.isDesktop

val gridColumnsCount: Int
    @Composable get() = when {
        isPortrait -> when {
            isWideFoldableMode -> 4
            else -> 2
        }
        else -> 4
    }

val useRailNavigation: Boolean
    @Composable get() = isWideFoldableMode || isDesktop

@Composable
fun calculateBottomContentPadding(
    innerPadding: PaddingValues,
    compactBottomPadding: Dp,
    bottomInsetPadding: Dp = 0.dp
): Dp {
    return when {
        useRailNavigation -> innerPadding.calculateBottomPadding() + bottomInsetPadding
        else -> innerPadding.calculateBottomPadding() + bottomInsetPadding + compactBottomPadding
    }
}

@Composable
fun calculatePageContentPadding(
    innerPadding: PaddingValues
): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val safeDrawingPadding = WindowInsets.safeDrawing.asPaddingValues()
    val topInsetPadding = safeDrawingPadding.calculateTopPadding()
    val bottomInsetPadding = safeDrawingPadding.calculateBottomPadding()
    return PaddingValues(
        start = innerPadding.calculateStartPadding(layoutDirection),
        top = innerPadding.calculateTopPadding() + topInsetPadding + pageContentTopPadding,
        end = innerPadding.calculateEndPadding(layoutDirection),
        bottom = calculateBottomContentPadding(
            innerPadding = innerPadding,
            compactBottomPadding = 72.dp + pageContentTopPadding,
            bottomInsetPadding = bottomInsetPadding
        )
    )
}

@Composable
fun OnResume(
    onResume: () -> Unit
) {
    OnLifecycleEvent(
        onEvent = { _, event ->
            onResume().takeIf { event == Lifecycle.Event.ON_RESUME }
        }
    )
}

@Composable
private fun OnLifecycleEvent(
    onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit
) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

fun Modifier.clickableWithoutRipple(
    block: () -> Unit
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickableWithoutRipple"
        value = block
    }
) {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = { block() }
    )
}

@Composable
fun pageContentColumnModifier(
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
): Modifier = Modifier
    .fillMaxWidth()
    .padding(horizontal = 8.dp, vertical = 4.dp)
    .clip(MaterialTheme.shapes.large)
    .background(cardColor)

@Composable
fun pageContentGridModifier(
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
): Modifier = Modifier
    .fillMaxWidth()
    .padding(bottom = 8.dp)
    .clip(MaterialTheme.shapes.large)
    .background(cardColor)

@Composable
fun pageContentStaggeredGridModifier(
    cardColor: Color = MaterialTheme.colorScheme.inversePrimary
): Modifier = Modifier
    .fillMaxWidth()
    .clip(MaterialTheme.shapes.large)
    .background(cardColor)

val <T: Any> LazyPagingItems<T>.isNotEmpty: Boolean
    get() = itemCount > 0

val <T: Any> LazyPagingItems<T>.isEmpty: Boolean
    get() = itemCount == 0

private val <T: Any> LazyPagingItems<T>.refreshState: LoadState
    get() = loadState.mediator?.refresh ?: loadState.refresh

private val <T: Any> LazyPagingItems<T>.appendState: LoadState
    get() = loadState.mediator?.append ?: loadState.append

val <T: Any> LazyPagingItems<T>.isRefreshLoading: Boolean
    get() = refreshState is LoadState.Loading

val <T: Any> LazyPagingItems<T>.isLoading: Boolean
    get() = isRefreshLoading

val <T: Any> LazyPagingItems<T>.isFailure: Boolean
    get() = refreshState is LoadState.Error && isEmpty

val <T: Any> LazyPagingItems<T>.isPagingLoading: Boolean
    get() = isNotEmpty && (isAppendLoading || isRefreshLoading)

val <T: Any> LazyPagingItems<T>.isPagingFailure: Boolean
    get() = isNotEmpty && (isAppendError && appendThrowable !is PageEmptyException || isRefreshError && refreshThrowable !is PageEmptyException)

val <T: Any> LazyPagingItems<T>.isRefreshError: Boolean
    get() = refreshState is LoadState.Error

val <T: Any> LazyPagingItems<T>.isAppendError: Boolean
    get() = appendState is LoadState.Error

val <T: Any> LazyPagingItems<T>.refreshThrowable: Throwable
    get() = (refreshState as LoadState.Error).error

val <T: Any> LazyPagingItems<T>.appendThrowable: Throwable
    get() = (appendState as LoadState.Error).error

val <T: Any> LazyPagingItems<T>.isAppendLoading: Boolean
    get() = appendState is LoadState.Loading

val <T: Any> LazyPagingItems<T>.isAppendRefresh: Boolean
    get() = isRefreshLoading

fun <T: Any> fadeTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
}

fun <T: Any> fadePredictiveTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform = { _: Int ->
    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
}
