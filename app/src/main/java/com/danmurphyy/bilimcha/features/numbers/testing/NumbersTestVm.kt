package com.danmurphyy.bilimcha.features.numbers.testing

import androidx.lifecycle.viewModelScope
import com.danmurphyy.bilimcha.features.numbers.Number
import com.danmurphyy.bilimcha.features.numbers.NumbersRepository
import com.danmurphyy.bilimcha.navigations.NumbersTestKey
import com.danmurphyy.bilimcha.uibases.BaseVM
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class NumbersTestVm @Inject constructor() :
    BaseVM<NumbersTestContract.Intent, NumbersTestContract.State, NumbersTestContract.Effect>(
        NumbersTestContract.State()
    ) {

    private var testNumbers = listOf<Number>()
    private var audioJob: Job? = null

    override fun handleIntent(intent: NumbersTestContract.Intent) {
        when (intent) {
            is NumbersTestContract.Intent.Init -> initTest(intent.key)
            is NumbersTestContract.Intent.SelectOption -> selectOption(intent.number)
            NumbersTestContract.Intent.PlayCurrentAudio -> playCurrentAudio()
            NumbersTestContract.Intent.NextQuestion -> nextQuestion()
            NumbersTestContract.Intent.RepeatTest -> repeatTest()
            NumbersTestContract.Intent.FinishTest -> finishTest()
            NumbersTestContract.Intent.ToggleRepeat -> updateState { it.copy(isRepeatMode = !it.isRepeatMode) }
            NumbersTestContract.Intent.RequestBack -> handleRequestBack()
            NumbersTestContract.Intent.ConfirmBack -> handleConfirmBack()
            NumbersTestContract.Intent.DismissExitDialog -> updateState { it.copy(showExitDialog = false) }
            NumbersTestContract.Intent.StartTestSequence -> startTestSequence()
            NumbersTestContract.Intent.OnPause -> stopAudioLoop()
            NumbersTestContract.Intent.OnResume -> handleOnResume()
        }
    }

    private fun initTest(key: NumbersTestKey) {
        testNumbers = NumbersRepository.numbers.filter {
            it.value in key.fromValue..key.toValue
        }.shuffled()

        updateState {
            it.copy(
                isLoading = false,
                totalCount = testNumbers.size,
                language = key.language,
                visualityType = key.visualityType,
                isRepeatMode = key.isRepeat
            )
        }

        if (testNumbers.isNotEmpty()) {
            startTestSequence()
        } else {
            updateState { it.copy(isFinished = true) }
        }
    }

    private fun startTestSequence() {
        viewModelScope.launch {
            updateState { it.copy(isIntroActive = true, countdownValue = 3) }
            for (i in 3 downTo 1) {
                updateState { it.copy(countdownValue = i) }
                delay(1.seconds)
            }
            updateState { it.copy(isIntroActive = false) }
            setupQuestion(0)
        }
    }

    private fun setupQuestion(index: Int) {
        if (index >= testNumbers.size) {
            if (getState().isRepeatMode) {
                // Save current results as historical and restart
                val currentResult = NumbersTestContract.ResultData(
                    correct = getState().correctCount,
                    wrong = getState().wrongCount
                )
                updateState {
                    it.copy(
                        lastCycleResult = currentResult,
                        correctCount = 0,
                        wrongCount = 0,
                        currentIndex = 0
                    )
                }
                testNumbers = testNumbers.shuffled()
                startTestSequence()
                return
            } else {
                finishTest()
                return
            }
        }

        val currentNumber = testNumbers[index]

        // Pick 2 wrong options
        val otherNumbers = NumbersRepository.numbers.filter { it.value != currentNumber.value }
        val wrongOptions = otherNumbers.shuffled().take(2)
        val options = (wrongOptions + currentNumber).shuffled()

        updateState {
            it.copy(
                currentNumber = currentNumber,
                options = options,
                currentIndex = index,
                currentWrongAttempts = 0,
                isSelectionEnabled = true,
                isNextButtonEnabled = false,
                selectedOption = null,
                isCorrectSelected = null
            )
        }

        startAudioLoop()
    }

    private fun startAudioLoop() {
        audioJob?.cancel()
        val state = getState()
        if (state.isFinished || state.currentNumber == null || state.isIntroActive) return

        val audioPath =
            if (state.language == "en") state.currentNumber.audioEn else state.currentNumber.audioRu

        audioJob = viewModelScope.launch {
            while (true) {
                sendEffect { NumbersTestContract.Effect.PlaySound(audioPath) }
                delay(4000.milliseconds)
            }
        }
    }

    private fun stopAudioLoop() {
        audioJob?.cancel()
        audioJob = null
        sendEffect { NumbersTestContract.Effect.StopSound }
    }

    private fun selectOption(number: Number) {
        val currentState = getState()
        if (!currentState.isSelectionEnabled) return

        val isCorrect = number.value == currentState.currentNumber?.value

        updateState {
            it.copy(
                selectedOption = number,
                isCorrectSelected = isCorrect
            )
        }

        if (isCorrect) {
            handleCorrectSelection()
        } else {
            handleWrongSelection()
        }
    }

    private fun handleCorrectSelection() {
        stopAudioLoop()
        val soundPath = "sounds/${getState().language}/correct.mp3"
        sendEffect { NumbersTestContract.Effect.PlaySound(soundPath) }

        updateState {
            it.copy(
                correctCount = it.correctCount + 1,
                isSelectionEnabled = false
            )
        }

        viewModelScope.launch {
            delay(2000.milliseconds)
            nextQuestion()
        }
    }

    private fun handleWrongSelection() {
        val soundPath = "sounds/${getState().language}/wrong.mp3"
        // Ensure repetitive audio stops before feedback plays
        stopAudioLoop()
        sendEffect { NumbersTestContract.Effect.PlaySound(soundPath) }

        val newWrongAttempts = getState().currentWrongAttempts + 1

        if (newWrongAttempts >= 2) {
            updateState {
                it.copy(
                    wrongCount = it.wrongCount + 1,
                    currentWrongAttempts = newWrongAttempts,
                    isSelectionEnabled = false
                )
            }
            viewModelScope.launch {
                delay(2000.milliseconds)
                nextQuestion()
            }
        } else {
            updateState { it.copy(currentWrongAttempts = newWrongAttempts) }
            // Restart audio loop after short delay or just let it restart in next tick?
            // User wants 1 play at a time. After wrong sound, we should probably wait until wrong sound finished
            // before starting number audio loop again.
            viewModelScope.launch {
                delay(2000.milliseconds)
                if (getState().isSelectionEnabled) {
                    startAudioLoop()
                }
            }
        }
    }

    private fun nextQuestion() {
        stopAudioLoop()
        setupQuestion(getState().currentIndex + 1)
    }

    private fun playCurrentAudio() {
        val state = getState()
        val audioPath =
            if (state.language == "en") state.currentNumber?.audioEn else state.currentNumber?.audioRu
        audioPath?.let {
            sendEffect { NumbersTestContract.Effect.PlaySound(it) }
        }
    }

    private fun finishTest() {
        stopAudioLoop()
        updateState { it.copy(isFinished = true) }
    }

    private fun repeatTest() {
        updateState {
            it.copy(
                currentIndex = 0,
                correctCount = 0,
                wrongCount = 0,
                isFinished = false
            )
        }
        testNumbers = testNumbers.shuffled()
        startTestSequence()
    }

    private fun handleRequestBack() {
        if (getState().isFinished || getState().isLoading) {
            sendEffect { NumbersTestContract.Effect.Back }
        } else {
            updateState { it.copy(showExitDialog = true) }
        }
    }

    private fun handleConfirmBack() {
        updateState { it.copy(showExitDialog = false) }
        val lastResult = getState().lastCycleResult
        if (lastResult != null) {
            // Show last historical result before leaving
            updateState {
                it.copy(
                    isFinished = true,
                    correctCount = lastResult.correct,
                    wrongCount = lastResult.wrong
                )
            }
            stopAudioLoop()
        } else {
            stopAudioLoop()
            sendEffect { NumbersTestContract.Effect.Back }
        }
    }

    private fun handleOnResume() {
        val state = getState()
        if (!state.isFinished && !state.isIntroActive && state.isSelectionEnabled && state.currentNumber != null) {
            startAudioLoop()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAudioLoop()
    }
}
