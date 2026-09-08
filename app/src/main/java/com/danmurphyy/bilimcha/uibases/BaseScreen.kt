package com.danmurphyy.bilimcha.uibases

import androidx.compose.runtime.Composable
import com.danmurphyy.bilimcha.navigations.NavKey

interface BaseScreen<T : NavKey> {
    val featureKey: T

    // Composable to render the screen
    @Composable
    fun Content()
}
