package com.danmurphyy.bilimcha.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.danmurphyy.bilimcha.navigations.AbcKey
import com.danmurphyy.bilimcha.navigations.AnimalsKey
import com.danmurphyy.bilimcha.uibases.AppHeader
import com.danmurphyy.bilimcha.uibases.BaseScreen

class AbcScreen(override val featureKey: AbcKey) : BaseScreen<AbcKey> {
    @Composable
    override fun Content() {
        Scaffold(
            topBar = { AppHeader(title = "ABC", showBackButton = true) }
        ) { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center) {
                Text("A, B, C... Coming Soon!")
            }
        }
    }
}

class AnimalsScreen(override val featureKey: AnimalsKey) : BaseScreen<AnimalsKey> {
    @Composable
    override fun Content() {
        Scaffold(
            topBar = { AppHeader(title = "Animals", showBackButton = true) }
        ) { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center) {
                Text("🦁, 🐘, 🦒... Coming Soon!")
            }
        }
    }
}
