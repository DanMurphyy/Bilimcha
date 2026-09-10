package com.danmurphyy.bilimcha.numbers.dashboard

import androidx.compose.runtime.Immutable
import com.danmurphyy.bilimcha.navigations.VisualityType

interface NumbersDashboardContracts {
    sealed interface Intent {
        data class ChangeLanguage(val code: String) : Intent
        data class ChangeVisuality(val type: VisualityType) : Intent
        data class ToggleAutoMode(val enabled: Boolean) : Intent
        data class SelectFrom(val value: Int) : Intent
        data class SelectTo(val value: Int) : Intent
        data object ToggleExpandedFrom : Intent
        data object ToggleExpandedTo : Intent
        data object ToggleAdditionalVisibility : Intent
        data class ToggleRepeat(val enabled: Boolean) : Intent
        data object StartPractice : Intent
        data object StartTest : Intent
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val availableNumbers: List<Int> = emptyList(),
        val languages: List<Pair<String, String>> = listOf("en" to "English", "ru" to "Russian"),
        val visualityTypes: List<VisualityType> = VisualityType.entries,
        val selectedLanguage: String = "en",
        val visualityType: VisualityType = VisualityType.Symbols,
        val isAutoMode: Boolean = true,
        val selectedFrom: Int = 1,
        val selectedTo: Int = 20,
        val expandedFrom: Boolean = false,
        val expandedTo: Boolean = false,
        val progress: Float = 0f,
        val fromOptions: List<Int> = emptyList(),
        val toOptions: List<Int> = emptyList(),
        val isAdditionalVisible: Boolean = true,
        val isRepeat: Boolean = true
    )

    sealed interface Effect {
        data class NavigateToPractice(
            val from: Int,
            val to: Int,
            val language: String,
            val visualityType: VisualityType,
            val isRepeat: Boolean
        ) : Effect

        data class NavigateToTest(
            val from: Int,
            val to: Int,
            val language: String,
            val visualityType: VisualityType,
            val isRepeat: Boolean
        ) : Effect
    }
}
