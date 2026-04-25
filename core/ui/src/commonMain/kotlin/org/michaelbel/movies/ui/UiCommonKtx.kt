package org.michaelbel.movies.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
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

val navigationSuiteType: NavigationSuiteType
    @Composable get() = when {
        windowWidth >= 1200.dp -> NavigationSuiteType.WideNavigationRailExpanded
        else -> NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfoV2())
    }

val isShortNavigationBarCompact: Boolean
    @Composable get() = navigationSuiteType == NavigationSuiteType.ShortNavigationBarCompact

val isShortNavigationBarMedium: Boolean
    @Composable get() = navigationSuiteType == NavigationSuiteType.ShortNavigationBarMedium

val isNavigationBar: Boolean
    @Composable get() = isShortNavigationBarCompact || isShortNavigationBarMedium

val isWideNavigationRailCollapsed: Boolean
    @Composable get() = navigationSuiteType == NavigationSuiteType.WideNavigationRailCollapsed

val isWideNavigationRailExpanded: Boolean
    @Composable get() = navigationSuiteType == NavigationSuiteType.WideNavigationRailExpanded

val isNavigationRail: Boolean
    @Composable get() = isWideNavigationRailCollapsed || isWideNavigationRailExpanded

val navigationBarPadding: Dp
    @Composable get() = if (isNavigationBar) 72.dp else 0.dp

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

val <T: Any> LazyPagingItems<T>.isLoading: Boolean
    get() = refreshState is LoadState.Loading

val <T: Any> LazyPagingItems<T>.isFailure: Boolean
    get() = refreshState is LoadState.Error && itemCount == 0

val <T: Any> LazyPagingItems<T>.isPagingLoading: Boolean
    get() = itemCount > 0 && (appendState is LoadState.Loading || isLoading)

val <T: Any> LazyPagingItems<T>.isPagingFailure: Boolean
    get() = itemCount > 0 && (appendState is LoadState.Error && appendThrowable !is PageEmptyException || refreshState is LoadState.Error && refreshThrowable !is PageEmptyException)

val <T: Any> LazyPagingItems<T>.refreshThrowable: Throwable
    get() = (refreshState as LoadState.Error).error

val <T: Any> LazyPagingItems<T>.appendThrowable: Throwable
    get() = (appendState as LoadState.Error).error

private val <T: Any> LazyPagingItems<T>.refreshState: LoadState
    get() = loadState.mediator?.refresh ?: loadState.refresh

private val <T: Any> LazyPagingItems<T>.appendState: LoadState
    get() = loadState.mediator?.append ?: loadState.append

private val windowWidth: Dp
    @Composable get() = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }

fun <T: Any> fadeTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
}

fun <T: Any> fadePredictiveTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform = { _: Int ->
    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
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
