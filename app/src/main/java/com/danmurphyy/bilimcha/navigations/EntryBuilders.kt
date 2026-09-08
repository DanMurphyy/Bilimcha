package com.danmurphyy.bilimcha.navigations

import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderScope
import com.danmurphyy.bilimcha.uibases.BaseScreen

import androidx.navigation3.runtime.NavKey as BaseNavKey

typealias NavEntryScope = EntryProviderScope<BaseNavKey>

inline fun <reified T : NavKey> NavEntryScope.registerScreen(
    animationStyle: NavAnimationStyle = NavAnimationStyle.HORIZONTAL,
    crossinline screenFactory: (T) -> BaseScreen<T>,
) {
    entry<T>(metadata = mapOf(NAV_ANIMATION_STYLE_KEY to animationStyle)) { key ->
        // Automatically create the screen instance
        val screen = remember(key) { screenFactory(key) }
        screen.Content()
    }
}
