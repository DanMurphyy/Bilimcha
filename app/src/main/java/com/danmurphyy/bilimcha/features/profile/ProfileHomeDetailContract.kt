package com.danmurphyy.bilimcha.features.profile

import androidx.compose.runtime.Immutable
import com.danmurphyy.bilimcha.navigations.UserFeatureData
import com.danmurphyy.bilimcha.uibases.UIState

interface ProfileHomeDetailContract {
    sealed interface Intent {
        data class OnGetDetails(val details: UserFeatureData) : Intent
        data object OnLogout : Intent
        data object OnDeleteAccount : Intent
        data class UpdateName(val name: String) : Intent
        data class UpdateUsername(val username: String) : Intent
        data class UpdateDob(val dob: String) : Intent
        data class UpdateCountry(val country: String) : Intent
        data class SaveProfile(val updatedState: State) : Intent
    }

    @Immutable
    data class State(
        val isLoading: Boolean = false,
        val id: String = "",
        val name: String = "",
        val username: String = "",
        val dob: String = "",
        val country: String = "",
        val extras: String = "",
        val email: String = "",
        val totalCorrect: Int = 0,
        val totalPossible: Int = 0,
        val totalProgress: Float = 0f,
        val categoriesProgress: List<CategoryProgress> = emptyList()
    ) : UIState

    data class CategoryProgress(
        val name: String,
        val progress: Float,
        val correct: Int,
        val total: Int
    )

    sealed interface Effect {
        data class ShowMessage(val message: String) : Effect
        data object Logout : Effect
        data object OnDeleteAccount : Effect
        data object NavigateBack : Effect
    }
}
