package org.michaelbel.movies.widget.configure

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.michaelbel.movies.common.ThemeData
import org.michaelbel.movies.common.viewmodel.CoroutineViewModel
import org.michaelbel.movies.domain.usecase.ThemeDataFlowUseCase
import org.michaelbel.movies.interactor.UiInteractor

class AppWidgetConfigureViewModel(
    uiInteractor: UiInteractor,
    themeDataFlowUseCase: ThemeDataFlowUseCase
): CoroutineViewModel() {

    val themeData: StateFlow<ThemeData> = themeDataFlowUseCase(uiInteractor.defaultDynamicColorsEnabled)
        .stateIn(
            scope = this,
            started = SharingStarted.Lazily,
            initialValue = ThemeData.Default
        )
}
