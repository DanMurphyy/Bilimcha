package com.danmurphyy.bilimcha.features.numbers.testing

import com.danmurphyy.bilimcha.features.numbers.Number
import com.danmurphyy.bilimcha.navigations.NumbersTestKey
import com.danmurphyy.bilimcha.navigations.VisualityType

interface NumbersTestContract {
    sealed interface Intent {
        data class Init(val key: NumbersTestKey) : Intent
        data class SelectOption(val number: Number) : Intent
        data object PlayCurrentAudio : Intent
        data object NextQuestion : Intent
        data object RepeatTest : Intent
        data object FinishTest : Intent
        data object ToggleRepeat : Intent
        data object RequestBack : Intent
        data object ConfirmBack : Intent
        data object DismissExitDialog : Intent
        data object StartTestSequence : Intent
        data object OnPause : Intent
        data object OnResume : Intent
    }

    data class State(
        val isLoading: Boolean = true,
        val isIntroActive: Boolean = false,
        val countdownValue: Int = 3,
        val currentNumber: Number? = null,
        val options: List<Number> = emptyList(),
        val currentIndex: Int = 0,
        val totalCount: Int = 0,
        val correctCount: Int = 0,
        val wrongCount: Int = 0,
        val currentWrongAttempts: Int = 0,
        val language: String = "en",
        val visualityType: VisualityType = VisualityType.Symbols,
        val isFinished: Boolean = false,
        val isSelectionEnabled: Boolean = true,
        val isNextButtonEnabled: Boolean = false,
        val selectedOption: Number? = null,
        val isCorrectSelected: Boolean? = null,
        val isRepeatMode: Boolean = false,
        val showExitDialog: Boolean = false,
        val lastCycleResult: ResultData? = null
    )

    data class ResultData(
        val correct: Int,
        val wrong: Int
    )

    sealed interface Effect {
        data class PlaySound(val path: String) : Effect
        data object StopSound : Effect
        data object Back : Effect
    }
}
