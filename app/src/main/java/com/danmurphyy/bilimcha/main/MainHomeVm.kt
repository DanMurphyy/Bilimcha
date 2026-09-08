package com.danmurphyy.bilimcha.main

import com.danmurphyy.bilimcha.uibases.BaseVM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainHomeVm @Inject constructor() : BaseVM<
        MainHomeContract.Intent,
        MainHomeContract.State,
        MainHomeContract.Effect>(MainHomeContract.State()) {

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