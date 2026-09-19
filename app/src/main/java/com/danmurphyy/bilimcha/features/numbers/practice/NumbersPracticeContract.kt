package com.danmurphyy.bilimcha.features.numbers.practice

import androidx.compose.runtime.Immutable
import com.danmurphyy.bilimcha.navigations.NumbersPracticeKey
import com.danmurphyy.bilimcha.data.models.Number

interface NumbersPracticeContract {
    sealed interface Intent {
        data class Init(val key: NumbersPracticeKey) : Intent
        data object NextNumber : Intent
        data object PreviousNumber : Intent
        data object PlayAudio : Intent
        data object RepeatFromStart : Intent
        data object Finish : Intent
        data object OnPause : Intent
        data object OnResume : Intent
    }

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val numbers: List<Number> = emptyList(),
        val currentIndex: Int = 0,
        val key: NumbersPracticeKey? = null,
        val isFinished: Boolean = false,
        val autoPlayAudio: Boolean = true,
        val autoAdvance: Boolean = true,
    ) {
        val currentNumber: Number? get() = numbers.getOrNull(currentIndex)
    }

    sealed interface Effect {
        data class PlayAudio(val url: String) : Effect
        data object StopAudio : Effect
        data object NavigateBack : Effect
    }
}
