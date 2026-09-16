package com.danmurphyy.bilimcha.features.numbers.practice

import com.danmurphyy.bilimcha.features.numbers.NumbersRepository
import com.danmurphyy.bilimcha.uibases.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NumbersPracticeVm @Inject constructor() : BaseVM<NumbersPracticeContract.Intent,
        NumbersPracticeContract.State,
        NumbersPracticeContract.Effect>(
    NumbersPracticeContract.State()
) {

    override fun handleIntent(intent: NumbersPracticeContract.Intent) {
        when (intent) {
            is NumbersPracticeContract.Intent.Init -> {
                val filteredNumbers = NumbersRepository.getNumbers(
                    from = intent.key.fromValue,
                    to = intent.key.toValue,
                    mode = intent.key.rangeMode
                )
                updateState {
                    it.copy(
                        isLoading = false,
                        numbers = filteredNumbers,
                        key = intent.key,
                        autoAdvance = intent.key.isAutoMode,
                        autoPlayAudio = true
                    )
                }
            }

            NumbersPracticeContract.Intent.NextNumber -> {
                val s = getState()
                if (s.currentIndex < s.numbers.size - 1) {
                    updateState { it.copy(currentIndex = s.currentIndex + 1) }
                } else {
                    if (s.key?.isRepeat == true) {
                        updateState { it.copy(currentIndex = 0) }
                    } else {
                        updateState { it.copy(isFinished = true) }
                    }
                }
            }

            NumbersPracticeContract.Intent.PreviousNumber -> {
                val s = getState()
                if (s.currentIndex > 0) {
                    updateState { it.copy(currentIndex = s.currentIndex - 1) }
                }
            }

            NumbersPracticeContract.Intent.PlayAudio -> {
                val s = getState()
                val number = s.currentNumber ?: return
                val audioUrl = if (s.key?.language == "ru") number.audioRu else number.audioEn
                sendEffect { NumbersPracticeContract.Effect.PlayAudio(audioUrl) }
            }

            NumbersPracticeContract.Intent.RepeatFromStart -> {
                updateState { it.copy(currentIndex = 0, isFinished = false) }
            }

            NumbersPracticeContract.Intent.Finish -> {
                sendEffect { NumbersPracticeContract.Effect.NavigateBack }
            }
        }
    }
}
