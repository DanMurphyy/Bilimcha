package com.danmurphyy.bilimcha.profile

import com.danmurphyy.bilimcha.navigations.UserFeatureData
import com.danmurphyy.bilimcha.uibases.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileHomeDetailVm @Inject constructor() :
    BaseVM<
            ProfileHomeDetailContract.Intent,
            ProfileHomeDetailContract.State,
            ProfileHomeDetailContract.Effect
            >(ProfileHomeDetailContract.State()) {

    override fun handleIntent(
        intent: ProfileHomeDetailContract.Intent,
    ) {
        when (intent) {
            ProfileHomeDetailContract.Intent.OnClick -> {
                sendEffect {
                    ProfileHomeDetailContract.Effect.ShowMessage(
                        "Profile clicked"
                    )
                }
            }

            ProfileHomeDetailContract.Intent.OnLogout -> {
                sendEffect {
                    ProfileHomeDetailContract.Effect.Logout
                }
            }

            ProfileHomeDetailContract.Intent.OnDeleteAccount -> {
                // Call delete account API here

                sendEffect {
                    ProfileHomeDetailContract.Effect.ShowMessage(
                        "Account deleted"
                    )
                }
                sendEffect {
                    ProfileHomeDetailContract.Effect.OnDeleteAccount
                }
            }

            is ProfileHomeDetailContract.Intent.OnGetDetails -> getDetails(intent.details)
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