package com.danmurphyy.bilimcha.uibases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseVM<I : Any, S : Any, E : Any>(initialState: S) : ViewModel() {

    // --- STATE ---
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    // --- EFFECT ---
    private val _effect = MutableSharedFlow<E>()
    val effect: SharedFlow<E> = _effect.asSharedFlow()

    init {
        onCreate()
    }

    /**
     * Called once when ViewModel is created.
     * Acts like onCreate().
     */
    protected open fun onCreate() {}

    // --- INTENT ENTRY POINT ---
    fun uiEvent(intent: I) {
        handleIntent(intent)
    }

    // --- MUST BE IMPLEMENTED BY FEATURE ---
    protected abstract fun handleIntent(intent: I)

    // --- REDUCER ---
    protected fun updateState(reducer: (S) -> S) {
        _state.update(reducer)
    }

    // --- EMIT EFFECT ---
    protected fun sendEffect(builder: () -> E) {
        viewModelScope.launch {
            _effect.emit(builder())
        }
    }

    // --- SAFE LAUNCH HELPER ---
    protected fun launch(
        block: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            block()
        }
    }

    protected fun <T> launchResult(
        onStart: (() -> Unit)? = null,
        onSuccess: (T) -> Unit,
        onFailure: (Throwable) -> Unit,
        onFinish: (() -> Unit)? = null,
        block: suspend () -> Result<T>,
    ) {
        viewModelScope.launch {

            onStart?.invoke()

            block()
                .onSuccess { onSuccess(it) }
                .onFailure { onFailure(it) }

            onFinish?.invoke()
        }
    }

    protected fun <T> collectResultFlow(
        onStart: (() -> Unit)? = null,
        onEachSuccess: (T) -> Unit,
        onEachFailure: (Throwable) -> Unit,
        onCompletion: (() -> Unit)? = null,
        block: () -> kotlinx.coroutines.flow.Flow<Result<T>>,
    ) {
        viewModelScope.launch {
            block()
                .onStart { onStart?.invoke() }
                .onCompletion { onCompletion?.invoke() }
                .collectLatest { result ->
                    result
                        .onSuccess { onEachSuccess(it) }
                        .onFailure { onEachFailure(it) }
                }
        }
    }

    protected fun getState() = state.value
}