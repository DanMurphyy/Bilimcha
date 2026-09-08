package com.danmurphyy.bilimcha.main

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.danmurphyy.bilimcha.navigations.LocalBackStackController
import com.danmurphyy.bilimcha.navigations.LocalSheetController
import com.danmurphyy.bilimcha.navigations.MainHomeKey
import com.danmurphyy.bilimcha.navigations.NumbersDashboardKey
import com.danmurphyy.bilimcha.navigations.PracticeDashboardKey
import com.danmurphyy.bilimcha.navigations.PracticeType
import com.danmurphyy.bilimcha.navigations.ProfileHomeDetailKey
import com.danmurphyy.bilimcha.navigations.UserFeatureData
import com.danmurphyy.bilimcha.ui.components.FloatingHeader
import com.danmurphyy.bilimcha.ui.components.LearningCard
import com.danmurphyy.bilimcha.ui.theme.KidsABC
import com.danmurphyy.bilimcha.ui.theme.KidsAnimals
import com.danmurphyy.bilimcha.ui.theme.KidsNumbers
import com.danmurphyy.bilimcha.uibases.AppHeader
import com.danmurphyy.bilimcha.uibases.BaseScreen
import com.danmurphyy.bilimcha.uibases.BottomFooter
import com.danmurphyy.bilimcha.uibases.FooterTab


class MainHomeScreen(override val featureKey: MainHomeKey) : BaseScreen<MainHomeKey> {
    @Composable
    override fun Content() {
        val navigation = LocalBackStackController.current
        val sheetController = LocalSheetController.current
        val context = LocalContext.current

        val vm: MainHomeVm = hiltViewModel()

        val tabs = remember {
            listOf(
                FooterTab(
                    MainHomeKey,
                    "Home",
                    Icons.Filled.Home
                ) {
                    if (navigation.current() != MainHomeKey) {
                        navigation.popTo(MainHomeKey)
                    }
                },

                FooterTab(
                    ProfileHomeDetailKey::class,
                    "Profile",
                    Icons.Filled.AccountCircle
                ) {
                    navigation.push(
                        ProfileHomeDetailKey(
                            data = UserFeatureData(
                                id = "123",
                                name = "John Doe",
                                extras = "Extra Data"
                            )
                        )
                    )
                }
            )
        }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is MainHomeContract.Effect.ShowMessage -> {
                        showToast(context, effect.message)
                    }

                    MainHomeContract.Effect.NavigateToProfile -> {
                        navigation.push(
                            ProfileHomeDetailKey(
                                data = UserFeatureData(
                                    id = "123",
                                    name = "John Doe",
                                    extras = "Extra Data"
                                )
                            )
                        )
                    }
                }
            }
        }
        Scaffold(
            modifier = Modifier
                .padding(horizontal = 20.dp),
            topBar = {
                AppHeader(
                    title = "Bilimcha",
                    showBackButton = false,
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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                FloatingHeader(greeting = "Hi, Kiddo!")

                LearningCard(
                    title = "Numbers",
                    icon = Icons.Default.FormatListNumbered,
                    containerColor = KidsNumbers,
                    onClick = { navigation.push(NumbersDashboardKey) }
                )

                LearningCard(
                    title = "Alphabet",
                    icon = Icons.Default.Abc,
                    containerColor = KidsABC,
                    onClick = { navigation.push(PracticeDashboardKey(PracticeType.Alphabet)) }
                )

                LearningCard(
                    title = "Animals",
                    icon = Icons.Default.Pets,
                    containerColor = KidsAnimals,
                    onClick = { navigation.push(PracticeDashboardKey(PracticeType.Animals)) }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

fun showToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}