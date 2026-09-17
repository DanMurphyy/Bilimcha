package com.danmurphyy.bilimcha.features.profile

import com.danmurphyy.bilimcha.navigations.UserFeatureData
import com.danmurphyy.bilimcha.uibases.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileHomeDetailVm @Inject constructor() : BaseVM<ProfileHomeDetailContract.Intent,
        ProfileHomeDetailContract.State,
        ProfileHomeDetailContract.Effect>(
    ProfileHomeDetailContract.State()
) {

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
            
            ProfileHomeDetailContract.Intent.SaveProfile -> {
                sendEffect { ProfileHomeDetailContract.Effect.ShowMessage("Profile Saved! 🌟") }
            }
        }
    }

    private fun getDetails(details: UserFeatureData) {
        updateState {
            it.copy(
                id = details.id,
                name = details.name,
                extras = details.extras
            )
        }
    }
}
