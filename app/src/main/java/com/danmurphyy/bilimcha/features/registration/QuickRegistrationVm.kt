package com.danmurphyy.bilimcha.features.registration

import androidx.lifecycle.viewModelScope
import com.danmurphyy.bilimcha.data.repository.UserRepository
import com.danmurphyy.bilimcha.uibases.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuickRegistrationVm @Inject constructor(
    private val userRepository: UserRepository
) : BaseVM<QuickRegistrationContract.Intent, QuickRegistrationContract.State, QuickRegistrationContract.Effect>(
    QuickRegistrationContract.State()
) {

    override fun handleIntent(intent: QuickRegistrationContract.Intent) {
        when (intent) {
            is QuickRegistrationContract.Intent.ChangeName -> {
                updateState { it.copy(name = intent.name) }
            }
            is QuickRegistrationContract.Intent.ChangeBirthDate -> {
                updateState { it.copy(birthDate = intent.date) }
            }
            is QuickRegistrationContract.Intent.ChangeCountry -> {
                updateState { it.copy(country = intent.country) }
            }
            is QuickRegistrationContract.Intent.Save -> {
                saveUser()
            }
            is QuickRegistrationContract.Intent.Dismiss -> {
                sendEffect { QuickRegistrationContract.Effect.NavigateToHome }
            }
        }
    }

    private fun saveUser() {
        val state = getState()
        val name = state.name
        val birthDate = state.birthDate ?: return
        val country = state.country

        if (name.isBlank()) return

        viewModelScope.launch {
            updateState { it.copy(isSaving = true) }
            userRepository.saveUser(
                name = name,
                birthDate = birthDate,
                country = country
            )
            updateState { it.copy(isSaving = false) }
            sendEffect { QuickRegistrationContract.Effect.NavigateToHome }
        }
    }
}
