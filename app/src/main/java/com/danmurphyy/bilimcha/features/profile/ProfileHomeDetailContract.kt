package com.danmurphyy.bilimcha.features.profile

import androidx.compose.runtime.Immutable
import com.danmurphyy.bilimcha.navigations.UserFeatureData

interface ProfileHomeDetailContract {
    sealed interface Intent {
        data class OnGetDetails(val details: UserFeatureData) : Intent
        data object OnLogout : Intent
        data object OnDeleteAccount : Intent
        data class UpdateName(val name: String) : Intent
        data class UpdateUsername(val username: String) : Intent
        data class UpdateDob(val dob: String) : Intent
        data class UpdateCountry(val country: String) : Intent
        data object SaveProfile : Intent
    }

    @Immutable
    data class State(
        val isLoading: Boolean = false,
        val id: String = "",
        val name: String = "Little Explorer",
        val username: String = "explorer123",
        val dob: String = "01.01.2020",
        val country: String = "Uzbekistan",
        val extras: String = "",
        val email: String = "explorer@bilimcha.com",
        val totalCorrect: Int = 245,
        val totalPossible: Int = 300,
        val totalProgress: Float = 0.81f,
        val categoriesProgress: List<CategoryProgress> = listOf(
            CategoryProgress("Numbers (EN)", 0.9f, 45, 50),
            CategoryProgress("Numbers (RU)", 0.7f, 35, 50),
            CategoryProgress("ABC English", 0.6f, 60, 100),
            CategoryProgress("ABC Russian", 0.4f, 40, 100),
            CategoryProgress("ABC Arabic", 0.2f, 20, 100),
            CategoryProgress("Animals (EN)", 0.95f, 45, 50)
        )
    )

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
