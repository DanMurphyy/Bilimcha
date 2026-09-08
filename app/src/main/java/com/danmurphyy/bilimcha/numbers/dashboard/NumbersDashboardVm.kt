package com.danmurphyy.bilimcha.numbers.dashboard

import androidx.lifecycle.viewModelScope
import com.danmurphyy.bilimcha.numbers.NumbersRepository
import com.danmurphyy.bilimcha.uibases.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class NumbersDashboardVm @Inject constructor() : BaseVM<NumbersDashboardContracts.Intent,
        NumbersDashboardContracts.State,
        NumbersDashboardContracts.Effect>(
    NumbersDashboardContracts.State()
) {

    override fun onCreate() {
        super.onCreate()
        viewModelScope.launch {
            val (available, initialFrom, initialTo, fromOptions, toOptions) = withContext(Dispatchers.Default) {
                val numbers = NumbersRepository.numbers.map { it.value }
                val from = numbers.first()
                val to = numbers[10]
                val fOptions = numbers.filter { n -> n <= to - 4 }
                val tOptions = numbers.filter { n -> n >= from + 4 }
                
                listOf(numbers, from, to, fOptions, tOptions)
            }

            @Suppress("UNCHECKED_CAST")
            updateState {
                it.copy(
                    availableNumbers = available as List<Int>,
                    selectedFrom = initialFrom as Int,
                    selectedTo = initialTo as Int,
                    fromOptions = fromOptions as List<Int>,
                    toOptions = toOptions as List<Int>,
                    progress = 0.15f // Mock progress
                )
            }

            delay(1500.milliseconds)
            updateState { it.copy(isAdditionalVisible = false) }
        }
    }

    override fun handleIntent(intent: NumbersDashboardContracts.Intent) {
        when (intent) {
            is NumbersDashboardContracts.Intent.ChangeLanguage -> {
                updateState { it.copy(selectedLanguage = intent.code) }
            }

            is NumbersDashboardContracts.Intent.ChangeVisuality -> {
                updateState { it.copy(visualityType = intent.type) }
            }

            is NumbersDashboardContracts.Intent.ToggleAutoMode -> {
                updateState { it.copy(isAutoMode = intent.enabled) }
            }

            is NumbersDashboardContracts.Intent.SelectFrom -> {
                updateState {
                    val newState = it.copy(selectedFrom = intent.value)
                    newState.copy(
                        toOptions = newState.availableNumbers.filter { n -> n >= intent.value + 4 }
                    )
                }
            }

            is NumbersDashboardContracts.Intent.SelectTo -> {
                updateState {
                    val newState = it.copy(selectedTo = intent.value)
                    newState.copy(
                        fromOptions = newState.availableNumbers.filter { n -> n <= intent.value - 4 }
                    )
                }
            }

            NumbersDashboardContracts.Intent.ToggleExpandedFrom -> {
                updateState { it.copy(expandedFrom = !it.expandedFrom) }
            }

            NumbersDashboardContracts.Intent.ToggleExpandedTo -> {
                updateState { it.copy(expandedTo = !it.expandedTo) }
            }

            NumbersDashboardContracts.Intent.ToggleAdditionalVisibility -> {
                updateState { it.copy(isAdditionalVisible = !it.isAdditionalVisible) }
            }

            is NumbersDashboardContracts.Intent.ToggleRepeat -> {
                updateState { it.copy(isRepeat = intent.enabled) }
            }

            NumbersDashboardContracts.Intent.StartPractice -> {
                val s = getState()
                sendEffect {
                    NumbersDashboardContracts.Effect.NavigateToPractice(
                        from = s.selectedFrom,
                        to = s.selectedTo,
                        language = s.selectedLanguage,
                        visualityType = s.visualityType,
                        isRepeat = s.isRepeat
                    )
                }
            }

            NumbersDashboardContracts.Intent.StartTest -> {
                val s = getState()
                sendEffect {
                    NumbersDashboardContracts.Effect.NavigateToTest(
                        from = s.selectedFrom,
                        to = s.selectedTo,
                        language = s.selectedLanguage,
                        visualityType = s.visualityType,
                        isRepeat = s.isRepeat
                    )
                }
            }
        }
    }
}
