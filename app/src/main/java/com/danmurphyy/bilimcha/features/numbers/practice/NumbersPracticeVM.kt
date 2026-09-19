package com.danmurphyy.bilimcha.features.numbers.practice

import androidx.lifecycle.viewModelScope
import com.danmurphyy.bilimcha.data.repository.UserRepository
import com.danmurphyy.bilimcha.data.repository.NumbersRepository
import com.danmurphyy.bilimcha.uibases.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class NumbersPracticeVm @Inject constructor(
    private val userRepository: UserRepository
) : BaseVM<NumbersPracticeContract.Intent,
        NumbersPracticeContract.State,
        NumbersPracticeContract.Effect>(
    NumbersPracticeContract.State()
) {

    private var advanceJob: Job? = null

    override fun handleIntent(intent: NumbersPracticeContract.Intent) {
        when (intent) {
            is NumbersPracticeContract.Intent.Init -> {
                val filteredNumbers = NumbersRepository.getNumbers(intent.key.range)
                updateState {
                    it.copy(
                        isLoading = false,
                        numbers = filteredNumbers,
                        key = intent.key,
                        autoAdvance = intent.key.isAutoMode,
                        autoPlayAudio = true
                    )
                }
                viewModelScope.launch {
                    delay(200.milliseconds) // Give UI time to start collecting effects
                    onNumberChanged()
                }
            }

            NumbersPracticeContract.Intent.NextNumber -> {
                val s = getState()
                if (s.currentIndex < s.numbers.size - 1) {
                    updateState { it.copy(currentIndex = s.currentIndex + 1) }
                    onNumberChanged()
                } else {
                    if (s.key?.isRepeat == true) {
                        updateState { it.copy(currentIndex = 0) }
                        onNumberChanged()
                    } else {
                        updateState { it.copy(isFinished = true) }
                        advanceJob?.cancel()
                    }
                }
            }

            NumbersPracticeContract.Intent.PreviousNumber -> {
                val s = getState()
                if (s.currentIndex > 0) {
                    updateState { it.copy(currentIndex = s.currentIndex - 1) }
                    onNumberChanged()
                }
            }

            NumbersPracticeContract.Intent.PlayAudio -> {
                playAudio()
            }

            NumbersPracticeContract.Intent.RepeatFromStart -> {
                updateState { it.copy(currentIndex = 0, isFinished = false) }
                onNumberChanged()
            }

            NumbersPracticeContract.Intent.Finish -> {
                advanceJob?.cancel()
                sendEffect { NumbersPracticeContract.Effect.NavigateBack }
            }

            NumbersPracticeContract.Intent.OnPause -> {
                advanceJob?.cancel()
                sendEffect { NumbersPracticeContract.Effect.StopAudio }
            }

            NumbersPracticeContract.Intent.OnResume -> {
                val s = getState()
                if (!s.isFinished && !s.isLoading) {
                    onNumberChanged()
                }
            }
        }
    }

    private fun onNumberChanged() {
        val s = getState()
        if (s.autoPlayAudio) {
            playAudio()
        }
        startAutoAdvance()
    }

    private fun playAudio() {
        val s = getState()
        val number = s.currentNumber ?: return
        val audioUrl = if (s.key?.language == "ru") number.audioRu else number.audioEn
        sendEffect { NumbersPracticeContract.Effect.PlayAudio(audioUrl) }
    }

    private fun startAutoAdvance() {
        advanceJob?.cancel()
        val s = getState()
        if (s.autoAdvance && !s.isFinished && !s.isLoading) {
            advanceJob = viewModelScope.launch {
                delay(3000.milliseconds)
                handleIntent(NumbersPracticeContract.Intent.NextNumber)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        advanceJob?.cancel()
    }
}
