package com.danmurphyy.bilimcha.features.numbers.dashboard

import androidx.lifecycle.viewModelScope
import com.danmurphyy.bilimcha.features.numbers.NumbersRepository
import com.danmurphyy.bilimcha.uibases.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class NumbersDashboardVm @Inject constructor() : BaseVM<NumbersDashboardContract.Intent,
        NumbersDashboardContract.State,
        NumbersDashboardContract.Effect>(
    NumbersDashboardContract.State()
) {

    private data class InitialNumbersData(
        val available: List<Int>,
        val from: Int,
        val to: Int,
        val fromOptions: List<Int>,
        val toOptions: List<Int>,
    )

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                val numbers = (0..30).toList() + listOf(40, 50, 60, 70, 80, 90, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000)
                val from = 0
                val to = 10
                val fOptions = numbers.filter { n -> n <= to - 4 }
                val tOptions = numbers.filter { n -> n >= from + 4 }

                InitialNumbersData(
                    available = numbers,
                    from = from,
                    to = to,
                    fromOptions = fOptions,
                    toOptions = tOptions
                )
            }

            updateState {
                it.copy(
                    isLoading = false,
                    availableNumbers = data.available,
                    selectedFrom = data.from,
                    selectedTo = data.to,
                    fromOptions = data.fromOptions,
                    toOptions = data.toOptions,
                    progress = 0.15f // Mock progress
                )
            }
        }
    }

    override fun handleIntent(intent: NumbersDashboardContract.Intent) {
        when (intent) {
            is NumbersDashboardContract.Intent.ChangeLanguage -> {
                updateState { it.copy(selectedLanguage = intent.code) }
            }

            is NumbersDashboardContract.Intent.ChangeVisuality -> {
                updateState { it.copy(visualityType = intent.type) }
            }

            is NumbersDashboardContract.Intent.ToggleAutoMode -> {
                updateState { it.copy(isAutoMode = intent.enabled) }
            }

            is NumbersDashboardContract.Intent.SelectFrom -> {
                updateState {
                    val newFrom = intent.value
                    val newTo = if (it.selectedTo < newFrom + 4) newFrom + 4 else it.selectedTo
                    val newState = it.copy(selectedFrom = newFrom, selectedTo = newTo, rangeMode = "all")
                    newState.copy(
                        fromOptions = newState.availableNumbers.filter { n -> n <= newTo - 4 },
                        toOptions = newState.availableNumbers.filter { n -> n >= newFrom + 4 }
                    )
                }
            }

            is NumbersDashboardContract.Intent.SelectTo -> {
                updateState {
                    val newTo = intent.value
                    val newFrom = if (it.selectedFrom > newTo - 4) newTo - 4 else it.selectedFrom
                    val newState = it.copy(selectedFrom = newFrom, selectedTo = newTo, rangeMode = "all")
                    newState.copy(
                        fromOptions = newState.availableNumbers.filter { n -> n <= newTo - 4 },
                        toOptions = newState.availableNumbers.filter { n -> n >= newFrom + 4 }
                    )
                }
            }

            is NumbersDashboardContract.Intent.SelectQuickRange -> {
                updateState {
                    it.copy(
                        selectedFrom = intent.from,
                        selectedTo = intent.to,
                        rangeMode = intent.mode
                    )
                }
            }

            NumbersDashboardContract.Intent.ToggleExpandedFrom -> {
                updateState { it.copy(expandedFrom = !it.expandedFrom) }
            }

            NumbersDashboardContract.Intent.ToggleExpandedTo -> {
                updateState { it.copy(expandedTo = !it.expandedTo) }
            }

            NumbersDashboardContract.Intent.ToggleSettingsDialog -> {
                updateState { it.copy(isSettingsDialogOpen = !it.isSettingsDialogOpen) }
            }

            is NumbersDashboardContract.Intent.ToggleRepeat -> {
                updateState { it.copy(isRepeat = intent.enabled) }
            }

            NumbersDashboardContract.Intent.StartPractice -> {
                val s = getState()
                sendEffect {
                    NumbersDashboardContract.Effect.NavigateToPractice(
                        from = s.selectedFrom,
                        to = s.selectedTo,
                        rangeMode = s.rangeMode,
                        language = s.selectedLanguage,
                        visualityType = s.visualityType,
                        isRepeat = s.isRepeat,
                        isAutoMode = s.isAutoMode
                    )
                }
            }

            NumbersDashboardContract.Intent.StartTest -> {
                val s = getState()
                sendEffect {
                    NumbersDashboardContract.Effect.NavigateToTest(
                        from = s.selectedFrom,
                        to = s.selectedTo,
                        rangeMode = s.rangeMode,
                        language = s.selectedLanguage,
                        visualityType = s.visualityType,
                        isRepeat = s.isRepeat
                    )
                }
            }
        }
    }
}
