package org.michaelbel.movies.ui.ktx

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent

fun <T: Any> fadeTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
}

fun <T: Any> fadePredictiveTransitionSpec(): AnimatedContentTransitionScope<Scene<T>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform = { _: Int ->
    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
}
