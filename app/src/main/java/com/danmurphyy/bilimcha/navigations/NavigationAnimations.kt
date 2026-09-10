package com.danmurphyy.bilimcha.navigations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
                animationSpec = tween(DEFAULT_DURATION, easing = FastOutSlowInEasing)
            ) togetherWith (fadeOut(targetAlpha = 0.7f, animationSpec = tween(DEFAULT_DURATION)) +
                    scaleOut(targetScale = 0.96f, animationSpec = tween(DEFAULT_DURATION)))
        },
        pop = {
            (fadeIn(initialAlpha = 0.7f, animationSpec = tween(DEFAULT_DURATION)) +
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(DEFAULT_DURATION)
                    )) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(DEFAULT_DURATION, easing = FastOutSlowInEasing)
                    )
        },
        predictivePop = {
            (fadeIn(initialAlpha = 0.7f, animationSpec = tween(DEFAULT_DURATION)) +
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(DEFAULT_DURATION)
                    )) togetherWith
                    slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = tween(DEFAULT_DURATION, easing = FastOutSlowInEasing)
                    )
        }
    )

    //Vertical (modals / flows)
    val Vertical = Set(
        forward = {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(MODAL_DURATION, easing = FastOutSlowInEasing)
            ) togetherWith (fadeOut(targetAlpha = 0.7f, animationSpec = tween(MODAL_DURATION)) +
                    scaleOut(targetScale = 0.96f, animationSpec = tween(MODAL_DURATION)))
        },
        pop = {
            (fadeIn(initialAlpha = 0.7f, animationSpec = tween(MODAL_DURATION)) +
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(MODAL_DURATION)
                    )) togetherWith
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(MODAL_DURATION, easing = FastOutSlowInEasing)
                    )
        },
        predictivePop = {
            (fadeIn(initialAlpha = 0.7f, animationSpec = tween(MODAL_DURATION)) +
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(MODAL_DURATION)
                    )) togetherWith
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(MODAL_DURATION, easing = FastOutSlowInEasing)
                    )
        }
    )

    //Fade (settings, lightweight screens)
    val Fade = Set(
        forward = {
            fadeIn(tween(MODAL_DURATION)) togetherWith (fadeOut(
                targetAlpha = 0.7f,
                animationSpec = tween(MODAL_DURATION)
            ) +
                    scaleOut(targetScale = 0.96f, animationSpec = tween(MODAL_DURATION)))
        },
        pop = {
            (fadeIn(initialAlpha = 0.7f, animationSpec = tween(MODAL_DURATION)) +
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(MODAL_DURATION)
                    )) togetherWith
                    fadeOut(tween(MODAL_DURATION))
        },
        predictivePop = {
            (fadeIn(initialAlpha = 0.7f, animationSpec = tween(MODAL_DURATION)) +
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(MODAL_DURATION)
                    )) togetherWith
                    fadeOut(tween(MODAL_DURATION))
        }
    )

    val BottomSheet = Set(
        forward = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(DEFAULT_DURATION)
            ) togetherWith fadeOut(
                animationSpec = tween(DEFAULT_DURATION)
            )
        },
        pop = {
            fadeIn(animationSpec = tween(DEFAULT_DURATION)) togetherWith
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(DEFAULT_DURATION)
                    )
        },
        predictivePop = { _ ->
            fadeIn(animationSpec = tween(DEFAULT_DURATION)) togetherWith
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(DEFAULT_DURATION)
                    )
        }
    )
}
