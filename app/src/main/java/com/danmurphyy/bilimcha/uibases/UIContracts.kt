package com.danmurphyy.bilimcha.uibases

import kotlinx.coroutines.flow.SharedFlow

interface UIState

interface UIEvent

/**
 * Marker interface for one-time UI effects.
 */
interface UIEffect

/**
 * UI state with one-time effects.
 *
 * Effects are exposed as a [SharedFlow] to match [BaseVM].
 */
interface UiStateWithSideEffect<E : UIEffect> : UIState {
    val effect: SharedFlow<E>
}