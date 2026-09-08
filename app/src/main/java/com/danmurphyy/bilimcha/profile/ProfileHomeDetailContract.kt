package com.danmurphyy.bilimcha.profile

import com.danmurphyy.bilimcha.navigations.UserFeatureData

interface ProfileHomeDetailContract {

    sealed interface Intent {
        data object OnClick : Intent
        data object OnLogout : Intent
        data object OnDeleteAccount : Intent
        data class OnGetDetails(val details: UserFeatureData) : Intent
    }

    data class State(
        val id: String = "",
        val name: String = "",
        val extras: String = "",
        val email: String = "",
    )

    sealed interface Effect {
        data class ShowMessage(val message: String) : Effect
        data object Logout : Effect
        data object OnDeleteAccount : Effect

    }
}