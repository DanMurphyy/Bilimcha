package com.danmurphyy.bilimcha.navigations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene

const val NAV_ANIMATION_STYLE_KEY = "nav_animation_style"

enum class NavAnimationStyle {
    HORIZONTAL,
    VERTICAL,
    FADE,
    BOTTOM_SHEET
}

object NavAnimations {
    private const val DEFAULT_DURATION = 400
    private const val MODAL_DURATION = 500

    data class Set(
        val forward: AnimatedContentTransitionScope<*>.() -> ContentTransform,
        val pop: AnimatedContentTransitionScope<*>.() -> ContentTransform,
        val predictivePop: AnimatedContentTransitionScope<Scene<NavKey>>.(Int) -> ContentTransform,
    )

    //Horizontal (default app navigation)
    val Horizontal = Set(
        forward = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(DEFAULT_DURATION)
            ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(DEFAULT_DURATION)
                    )
        },
        pop = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(DEFAULT_DURATION)
            ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(DEFAULT_DURATION)
                    )
        },
        predictivePop = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(DEFAULT_DURATION)
            ) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(DEFAULT_DURATION)
                    )
        }
    )

    //Vertical (modals / flows)
    val Vertical = Set(
        forward = {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(MODAL_DURATION)
            ) togetherWith ExitTransition.None
        },
        pop = {
            EnterTransition.None togetherWith
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(MODAL_DURATION)
                    )
        },
        predictivePop = {
            EnterTransition.None togetherWith
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(MODAL_DURATION)
                    )
        }
    )

    //Fade (settings, lightweight screens)
    val Fade = Set(
        forward = {
            fadeIn(tween(MODAL_DURATION)) togetherWith fadeOut(tween(MODAL_DURATION))
        },
        pop = {
            fadeIn(tween(MODAL_DURATION)) togetherWith fadeOut(tween(MODAL_DURATION))
        },
        predictivePop = {
            fadeIn(tween(MODAL_DURATION)) togetherWith fadeOut(tween(MODAL_DURATION))
        }
    )

    val BottomSheet = Set(
        forward = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(300)
            ) togetherWith fadeOut(
                animationSpec = tween(300)
            )
        },
        pop = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(300)
                    )
        },
        predictivePop = { _ ->
            fadeIn(animationSpec = tween(300)) togetherWith
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(300)
                    )
        }
    )
}
