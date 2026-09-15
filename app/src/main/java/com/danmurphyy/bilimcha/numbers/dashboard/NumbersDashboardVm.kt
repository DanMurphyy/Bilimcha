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
                val numbers = NumbersRepository.numbers.map { it.value }
                val from = numbers.firstOrNull() ?: 1
                val to = numbers.getOrNull(10) ?: 20
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

            delay(1500.milliseconds)
            updateState { it.copy(isAdditionalVisible = false) }
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
                    val newState = it.copy(selectedFrom = intent.value)
                    newState.copy(
                        toOptions = newState.availableNumbers.filter { n -> n >= intent.value + 4 }
                    )
                }
            }

            is NumbersDashboardContract.Intent.SelectTo -> {
                updateState {
                    val newState = it.copy(selectedTo = intent.value)
                    newState.copy(
                        fromOptions = newState.availableNumbers.filter { n -> n <= intent.value - 4 }
                    )
                }
            }

            NumbersDashboardContract.Intent.ToggleExpandedFrom -> {
                updateState { it.copy(expandedFrom = !it.expandedFrom) }
            }

            NumbersDashboardContract.Intent.ToggleExpandedTo -> {
                updateState { it.copy(expandedTo = !it.expandedTo) }
            }

            NumbersDashboardContract.Intent.ToggleAdditionalVisibility -> {
                updateState { it.copy(isAdditionalVisible = !it.isAdditionalVisible) }
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
                        language = s.selectedLanguage,
                        visualityType = s.visualityType,
                        isRepeat = s.isRepeat
                    )
                }
            }
        }
    }
}
