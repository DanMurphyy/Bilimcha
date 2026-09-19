package com.danmurphyy.bilimcha.main

import androidx.lifecycle.viewModelScope
import com.danmurphyy.bilimcha.data.repository.UserRepository
import com.danmurphyy.bilimcha.uibases.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainHomeVm @Inject constructor(
    private val userRepository: UserRepository
) : BaseVM<
        MainHomeContract.Intent,
        MainHomeContract.State,
        MainHomeContract.Effect>(MainHomeContract.State()) {

    init {
        checkUserRegistration()
    }

    private fun checkUserRegistration() {
        viewModelScope.launch {
            val user = userRepository.getUser().first()
            if (user == null) {
                sendEffect { MainHomeContract.Effect.ShowRegistrationWall }
            }
        }
    }

    override fun handleIntent(intent: MainHomeContract.Intent) {
        when (intent) {

            MainHomeContract.Intent.OnClick -> {
                sendEffect {
                    MainHomeContract.Effect.ShowMessage("Clicked")
                }
            }

            MainHomeContract.Intent.NavigateToProfile -> {
                sendEffect {
                    MainHomeContract.Effect.NavigateToProfile
                }
            }
        }
    }
}