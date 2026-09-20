package com.danmurphyy.bilimcha.features.profile

import androidx.lifecycle.viewModelScope
import com.danmurphyy.bilimcha.data.models.NumbersRange
import com.danmurphyy.bilimcha.data.repository.UserRepository
import com.danmurphyy.bilimcha.navigations.UserFeatureData
import com.danmurphyy.bilimcha.uibases.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileHomeDetailVm @Inject constructor(
    private val userRepository: UserRepository
) : BaseVM<ProfileHomeDetailContract.Intent,
        ProfileHomeDetailContract.State,
        ProfileHomeDetailContract.Effect>(
    ProfileHomeDetailContract.State()
) {

    init {
        observeUser()
    }

    private fun observeUser() {
        viewModelScope.launch {
            userRepository.getUser().collectLatest { user ->
                if (user == null) return@collectLatest

                val dobStr = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    .format(Date(user.birthDate))

                val stats = user.stats.numbers
                val completedCount = stats.count { it.isPassed }
                val totalRanges = NumbersRange.entries.size - 1
                val progress = if (totalRanges > 0) completedCount.toFloat() / totalRanges else 0f

                val categoriesProgress = listOf(
                    ProfileHomeDetailContract.CategoryProgress(
                        name = "Numbers Learning",
                        progress = progress,
                        correct = completedCount,
                        total = totalRanges
                    )
                )

                updateState {
                    it.copy(
                        name = user.name,
                        dob = dobStr,
                        country = user.country,
                        totalCorrect = completedCount,
                        totalPossible = totalRanges,
                        totalProgress = progress,
                        categoriesProgress = categoriesProgress
                    )
                }
            }
        }
    }

    override fun handleIntent(intent: ProfileHomeDetailContract.Intent) {
        when (intent) {
            is ProfileHomeDetailContract.Intent.OnGetDetails -> getDetails(intent.details)
            
            ProfileHomeDetailContract.Intent.OnLogout -> {
                sendEffect { ProfileHomeDetailContract.Effect.Logout }
            }
            
            ProfileHomeDetailContract.Intent.OnDeleteAccount -> {
                sendEffect { ProfileHomeDetailContract.Effect.ShowMessage("Account deleted") }
                sendEffect { ProfileHomeDetailContract.Effect.OnDeleteAccount }
            }
            
            is ProfileHomeDetailContract.Intent.UpdateName -> {
                updateState { it.copy(name = intent.name) }
            }
            
            is ProfileHomeDetailContract.Intent.UpdateUsername -> {
                updateState { it.copy(username = intent.username) }
            }
            
            is ProfileHomeDetailContract.Intent.UpdateDob -> {
                updateState { it.copy(dob = intent.dob) }
            }
            
            is ProfileHomeDetailContract.Intent.UpdateCountry -> {
                updateState { it.copy(country = intent.country) }
            }
            
            is ProfileHomeDetailContract.Intent.SaveProfile -> {
                saveProfile(intent.updatedState)
            }
        }
    }

    private fun saveProfile(updatedState: ProfileHomeDetailContract.State) {
        viewModelScope.launch {
            val dobDate = try {
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(updatedState.dob)
            } catch (e: Exception) {
                null
            }

            userRepository.updateProfile(
                name = updatedState.name,
                birthDate = dobDate?.time ?: 0L,
                country = updatedState.country
            )
            sendEffect { ProfileHomeDetailContract.Effect.ShowMessage("Profile Saved! 🌟") }
        }
    }

    private fun getDetails(details: UserFeatureData) {
        updateState {
            it.copy(
                id = details.id.orEmpty(),
                name = details.name.orEmpty(),
                extras = details.extras.orEmpty()
            )
        }
    }
}
