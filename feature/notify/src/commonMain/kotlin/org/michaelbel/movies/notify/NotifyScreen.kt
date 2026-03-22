@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package org.michaelbel.movies.notify

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumExtendedFloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.michaelbel.movies.interactor.UiInteractor
import org.michaelbel.movies.notify.event.NotifyEvent
import org.michaelbel.movies.notify.intent.NotifyIntent
import org.michaelbel.movies.notify.model.NotifyModel
import org.michaelbel.movies.ui.ObserveAsEvents
import org.michaelbel.movies.ui.accessibility.MoviesContentDescription
import org.michaelbel.movies.ui.collectAsStateCommon
import org.michaelbel.movies.ui.icons.MoviesIcons
import org.michaelbel.movies.ui.strings.MoviesStrings
import org.michaelbel.movies.ui.theme.MoviesTheme

@Composable
fun NotifyScreen(
    viewModel: NotifyViewModel = koinViewModel(),
    uiInteractor: UiInteractor = koinInject()
) {
    val state by viewModel.stateFlow.collectAsStateCommon()
    val navigateToAppNotificationSettings = uiInteractor.navigateToAppNotificationSettings()
    val requestPostNotificationsPermission = uiInteractor.rememberPostNotificationsPermissionHandler(
        enabled = false,
        onPermissionGranted = {},
        onPermissionDenied = navigateToAppNotificationSettings
    )

    NotifyScreenContent(
        state = state,
        dispatch = viewModel::dispatch
    )

    ObserveAsEvents(
        flow = viewModel.eventFlow,
        key1 = requestPostNotificationsPermission,
        key2 = navigateToAppNotificationSettings
    ) { event ->
        when (event) {
            is NotifyEvent.RequestPostNotificationsPermission -> requestPostNotificationsPermission()
            is NotifyEvent.OpenAppNotificationSettings -> navigateToAppNotificationSettings()
        }
    }
}

@Composable
private fun NotifyScreenContent(
    state: NotifyModel,
    dispatch: (NotifyIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val hapticFeedback = LocalHapticFeedback.current
    val bellRotation = remember { Animatable(0F) }

    LaunchedEffect(Unit) {
        while (isActive) {
            coroutineScope {
                launch {
                    bellRotation.snapTo(0F)
                    bellRotation.animateTo(
                        targetValue = 0F,
                        animationSpec = keyframes {
                            durationMillis = 2_000
                            14F at 180 using LinearOutSlowInEasing
                            (-13F) at 360 using LinearOutSlowInEasing
                            12F at 540 using LinearOutSlowInEasing
                            (-10F) at 740 using LinearOutSlowInEasing
                            8F at 980 using LinearOutSlowInEasing
                            (-6F) at 1_240 using LinearOutSlowInEasing
                            4F at 1_520 using LinearOutSlowInEasing
                            (-2F) at 1_760 using LinearOutSlowInEasing
                            0F at 2_000 using LinearOutSlowInEasing
                        }
                    )
                }

                launch {
                    delay(180)
                    repeat(5) {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        delay(360)
                    }
                }
            }

            delay(2_000)
        }
    }

    ModalBottomSheet(
        onDismissRequest = { dispatch(NotifyIntent.DismissRequest) },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color = MaterialTheme.colorScheme.inversePrimary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    imageVector = MoviesIcons.Notifications,
                    contentDescription = MoviesContentDescription.None,
                    modifier = Modifier
                        .size(IconButtonDefaults.smallIconSize)
                        .graphicsLayer(
                            transformOrigin = TransformOrigin(pivotFractionX = .5F, pivotFractionY = .0F),
                            rotationZ = bellRotation.value
                        ),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Text(
                text = stringResource(MoviesStrings.notification_enable_title),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.onPrimaryContainer, textAlign = TextAlign.Center)
            )

            Text(
                text = stringResource(MoviesStrings.notification_enable_subtitle),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer, textAlign = TextAlign.Center)
            )

            MediumExtendedFloatingActionButton(
                onClick = { dispatch(NotifyIntent.ActionClick) },
                shape = FloatingActionButtonDefaults.mediumExtendedFabShape,
                containerColor = MaterialTheme.colorScheme.surfaceTint,
                elevation = FloatingActionButtonDefaults.loweredElevation(),
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
            ) {
                Text(
                    text = stringResource(if (state.isNotificationsFeatureEnabled) MoviesStrings.notification_continue else MoviesStrings.notification_go_to_settings),
                    style = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )
            }
        }
    }
}

@Preview
@Composable
private fun NotifyScreenContentPreview() {
    MoviesTheme {
        NotifyScreenContent(
            state = NotifyModel(isNotificationsFeatureEnabled = true),
            dispatch = {}
        )
    }
}
