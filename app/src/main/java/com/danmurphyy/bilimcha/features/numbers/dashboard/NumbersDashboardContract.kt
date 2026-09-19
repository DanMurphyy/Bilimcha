package com.danmurphyy.bilimcha.features.numbers.dashboard

import androidx.compose.runtime.Immutable
import com.danmurphyy.bilimcha.data.models.NumbersRange
import com.danmurphyy.bilimcha.navigations.VisualityType

interface NumbersDashboardContract {
    sealed interface Intent {
        data class ChangeLanguage(val code: String) : Intent
        data class ChangeVisuality(val type: VisualityType) : Intent
        data class ToggleAutoMode(val enabled: Boolean) : Intent
        data class SelectRange(val range: NumbersRange) : Intent
        data object ToggleSettingsDialog : Intent
        data class ToggleRepeat(val enabled: Boolean) : Intent
        data object StartPractice : Intent
        data object StartTest : Intent
        data object DismissRegistrationWarning : Intent
        data object NavigateToRegistration : Intent
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val languages: List<Pair<String, String>> = listOf("en" to "English", "ru" to "Russian"),
        val visualityTypes: List<VisualityType> = VisualityType.entries,
        val selectedLanguage: String = "en",
        val visualityType: VisualityType = VisualityType.Symbols,
        val isAutoMode: Boolean = true,
        val selectedRange: NumbersRange = NumbersRange.RANGE_0_10,
        val progress: Float = 0f,
        val isSettingsDialogOpen: Boolean = false,
        val isRepeat: Boolean = true,
        val unlockedRanges: Set<String> = setOf("0_10"),
        val isUserRegistered: Boolean = false,
        val showRegistrationWarning: Boolean = false,
        val pendingIntent: Intent? = null
    )

    sealed interface Effect {
        data class NavigateToPractice(
            val range: NumbersRange,
            val language: String,
            val visualityType: VisualityType,
            val isRepeat: Boolean,
            val isAutoMode: Boolean
        ) : Effect

        data class NavigateToTest(
            val range: NumbersRange,
            val language: String,
            val visualityType: VisualityType,
            val isRepeat: Boolean
        ) : Effect

        data object NavigateToRegistration : Effect
    }
}
