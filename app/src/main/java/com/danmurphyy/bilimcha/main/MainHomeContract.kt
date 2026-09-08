package com.danmurphyy.bilimcha.main

interface MainHomeContract {

    sealed interface Intent {
        data object OnClick : Intent
        data object NavigateToProfile : Intent
    }

    data class State(
        val message: String = "",
    )

    sealed interface Effect {
        data class ShowMessage(val message: String) : Effect
        data object NavigateToProfile : Effect
    }
}