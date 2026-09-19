package com.danmurphyy.bilimcha.features.numbers.dashboard

import androidx.lifecycle.viewModelScope
import com.danmurphyy.bilimcha.data.models.NumbersRange
import com.danmurphyy.bilimcha.data.repository.UserRepository
import com.danmurphyy.bilimcha.uibases.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NumbersDashboardVm @Inject constructor(
    private val userRepository: UserRepository,
) : BaseVM<NumbersDashboardContract.Intent,
        NumbersDashboardContract.State,
        NumbersDashboardContract.Effect>(
    NumbersDashboardContract.State()
) {

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            userRepository.getUser().collectLatest { user ->
                if (user == null) {
                    updateState { it.copy(isUserRegistered = false, isLoading = false) }
                    return@collectLatest
                }

                val stats = user.stats.numbers
                val unlockedIds = stats.filter { it.isUnlocked }.map { it.rangeId }.toSet()

                val totalRanges =
                    NumbersRange.entries.size - 1 // Exclude "All" or similar if needed
                val completedRanges = stats.filter { it.isPassed }.size
                val progress =
                    if (totalRanges > 0) (completedRanges.toFloat() / totalRanges.toFloat()).coerceAtMost(
                        1f
                    ) else 0f

                updateState {
                    it.copy(
                        isLoading = false,
                        isUserRegistered = true,
                        unlockedRanges = unlockedIds,
                        progress = progress
                    )
                }
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

            is NumbersDashboardContract.Intent.SelectRange -> {
                updateState { it.copy(selectedRange = intent.range) }
            }

            NumbersDashboardContract.Intent.ToggleSettingsDialog -> {
                updateState { it.copy(isSettingsDialogOpen = !it.isSettingsDialogOpen) }
            }

            is NumbersDashboardContract.Intent.ToggleRepeat -> {
                updateState { it.copy(isRepeat = intent.enabled) }
            }

            NumbersDashboardContract.Intent.StartPractice -> {
                if (!getState().isUserRegistered) {
                    updateState { it.copy(showRegistrationWarning = true, pendingIntent = intent) }
                } else {
                    navigateToPractice()
                }
            }

            NumbersDashboardContract.Intent.StartTest -> {
                if (!getState().isUserRegistered) {
                    updateState { it.copy(showRegistrationWarning = true, pendingIntent = intent) }
                } else {
                    navigateToTest()
                }
            }

            NumbersDashboardContract.Intent.DismissRegistrationWarning -> {
                val pending = getState().pendingIntent
                updateState { it.copy(showRegistrationWarning = false, pendingIntent = null) }
                if (pending is NumbersDashboardContract.Intent.StartPractice) {
                    navigateToPractice()
                } else if (pending is NumbersDashboardContract.Intent.StartTest) {
                    navigateToTest()
                }
            }

            NumbersDashboardContract.Intent.NavigateToRegistration -> {
                updateState { it.copy(showRegistrationWarning = false, pendingIntent = null) }
                sendEffect { NumbersDashboardContract.Effect.NavigateToRegistration }
            }
        }
    }

    private fun navigateToPractice() {
        val s = getState()
        sendEffect {
            NumbersDashboardContract.Effect.NavigateToPractice(
                range = s.selectedRange,
                language = s.selectedLanguage,
                visualityType = s.visualityType,
                isRepeat = s.isRepeat,
                isAutoMode = s.isAutoMode
            )
        }
    }

    private fun navigateToTest() {
        val s = getState()
        sendEffect {
            NumbersDashboardContract.Effect.NavigateToTest(
                range = s.selectedRange,
                language = s.selectedLanguage,
                visualityType = s.visualityType,
                isRepeat = s.isRepeat
            )
        }
    }
}
