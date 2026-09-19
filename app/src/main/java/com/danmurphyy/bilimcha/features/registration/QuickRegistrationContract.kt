package com.danmurphyy.bilimcha.features.registration

import com.danmurphyy.bilimcha.uibases.UIEffect
import com.danmurphyy.bilimcha.uibases.UIEvent
import com.danmurphyy.bilimcha.uibases.UIState

class QuickRegistrationContract {
    data class State(
        val name: String = "",
        val birthDate: Long? = null,
        val country: String = "",
        val isSaving: Boolean = false
    ) : UIState

    sealed class Intent : UIEvent {
        data class ChangeName(val name: String) : Intent()
        data class ChangeBirthDate(val date: Long) : Intent()
        data class ChangeCountry(val country: String) : Intent()
        data object Save : Intent()
        data object Dismiss : Intent()
    }

    sealed class Effect : UIEffect {
        data object NavigateToHome : Effect()
    }
}
