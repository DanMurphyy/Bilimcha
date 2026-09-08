package com.danmurphyy.bilimcha.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.danmurphyy.bilimcha.main.showToast
import com.danmurphyy.bilimcha.navigations.LocalBackStackController
import com.danmurphyy.bilimcha.navigations.LocalSheetController
import com.danmurphyy.bilimcha.navigations.MainHomeKey
import com.danmurphyy.bilimcha.navigations.ProfileHomeDetailKey
import com.danmurphyy.bilimcha.uibases.AppHeader
import com.danmurphyy.bilimcha.uibases.BaseScreen
import com.danmurphyy.bilimcha.uibases.BottomFooter
import com.danmurphyy.bilimcha.uibases.DialogBottomSheet
import com.danmurphyy.bilimcha.uibases.DialogSheetData
import com.danmurphyy.bilimcha.uibases.FooterTab

class ProfileHomeDetailScreen(
    override val featureKey: ProfileHomeDetailKey,
) : BaseScreen<ProfileHomeDetailKey> {

    @Composable
    override fun Content() {

        val navigation = LocalBackStackController.current
        val sheetController = LocalSheetController.current
        val context = LocalContext.current

        val vm: ProfileHomeDetailVm = hiltViewModel()

        val uiState by vm.state.collectAsState()

        val profileDetails = featureKey.data

        val tabs = remember {
            listOf(
                FooterTab(
                    MainHomeKey,
                    "Home",
                    Icons.Filled.Home
                ) {
                    navigation.popTo(MainHomeKey)
                },

                FooterTab(
                    ProfileHomeDetailKey::class,
                    "Profile",
                    Icons.Filled.AccountCircle
                ) {
                    // Already on Profile
                }
            )
        }

        LaunchedEffect(profileDetails) {
            vm.uiEvent(
                intent = ProfileHomeDetailContract.Intent.OnGetDetails(
                    details = profileDetails
                )
            )
        }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {

                    is ProfileHomeDetailContract.Effect.ShowMessage -> {
                        showToast(context, effect.message)
                    }

                    ProfileHomeDetailContract.Effect.Logout -> {
                        navigation.clearAndPush(MainHomeKey)
                    }

                    ProfileHomeDetailContract.Effect.OnDeleteAccount -> {
                        navigation.clearAndPush(MainHomeKey)
                    }
                }
            }
        }

        Scaffold(
            modifier = Modifier
                .statusBarsPadding(),
            topBar = {
                AppHeader(
                    title = "Profile",
                    showBackButton = false,
                    rightContent = {
                        IconButton(
                            onClick = {
                                sheetController.show(
                                    DialogBottomSheet(
                                        data = DialogSheetData(
                                            title = "Delete Account",
                                            isDialog = false,
                                            subtitle = "Are you sure you want to delete your account?",

                                            onDismiss = {
                                                sheetController.clear()
                                            },

                                            onConfirm = {
                                                sheetController.clear()

                                                vm.uiEvent(
                                                    ProfileHomeDetailContract.Intent.OnDeleteAccount
                                                )
                                            }
                                        )
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete Account",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
            },

            bottomBar = {
                BottomFooter(
                    navigation = navigation,
                    tabs = tabs
                )
            },

            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                Text(
                    text = "Name: ${uiState.name}"
                )

                Text(
                    text = "Email: ${uiState.email}"
                )

                Button(
                    onClick = {
                        vm.uiEvent(
                            ProfileHomeDetailContract.Intent.OnClick
                        )
                    }
                ) {
                    Text("Show Message")
                }

                Button(
                    onClick = {
                        vm.uiEvent(
                            ProfileHomeDetailContract.Intent.OnLogout
                        )
                    }
                ) {
                    Text("Logout")
                }
            }
        }
    }
}