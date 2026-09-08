package com.danmurphyy.bilimcha.navigations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey as BaseNavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    innerPadding: PaddingValues,
    navEntries: List<NavEntryScope.() -> Unit>,
    backStackController: BackStackController,
    sheetController: SheetController,
) {
    val backStack = rememberNavBackStack(MainHomeKey as BaseNavKey)

    SideEffect {
        backStackController.setBackStack(backStack)
    }

    CompositionLocalProvider(
        LocalSheetController provides sheetController,
        LocalBackStackController provides backStackController
    ) {
        val entryProvider = remember(navEntries) {
            entryProvider {
                navEntries.forEach { it() }
            }
        }

        val decorator1 = rememberSaveableStateHolderNavEntryDecorator<BaseNavKey>()
        val decorator2 = rememberViewModelStoreNavEntryDecorator<BaseNavKey>()
        val entryDecorators = remember(decorator1, decorator2) {
            listOf(decorator1, decorator2)
        }

        NavDisplay(
            backStack = backStack,
            modifier = Modifier
                .padding(innerPadding)
                .statusBarsPadding(),
            entryDecorators = entryDecorators,
            entryProvider = entryProvider,
            onBack = {
                // Direct backstack modification for zero-latency
                backStack.removeLastOrNull()
            },
            transitionSpec = {
                resolveAnimations(this, isPop = false).forward(this)
            },
            popTransitionSpec = {
                resolveAnimations(this, isPop = true).pop(this)
            },
            predictivePopTransitionSpec = { count ->
                resolveAnimations(this, isPop = true).predictivePop(this, count)
            })

        BottomSheetHost(innerPadding)
    }
}

fun resolveAnimations(
    scope: AnimatedContentTransitionScope<Scene<BaseNavKey>>,
    isPop: Boolean,
): NavAnimations.Set {
    val scene = if (isPop) scope.initialState else scope.targetState
    val style = scene
        .entries
        .lastOrNull()
        ?.metadata
        ?.get(NAV_ANIMATION_STYLE_KEY) as? NavAnimationStyle

    return when (style) {
        NavAnimationStyle.VERTICAL -> NavAnimations.Vertical
        NavAnimationStyle.FADE -> NavAnimations.Fade
        NavAnimationStyle.HORIZONTAL -> NavAnimations.Horizontal
        NavAnimationStyle.BOTTOM_SHEET -> NavAnimations.BottomSheet
        null -> NavAnimations.Horizontal
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetHost(innerPadding: PaddingValues) {
    val sheetController = LocalSheetController.current
    val sheet = sheetController.sheet.value ?: return
    val scope = rememberCoroutineScope()

    if (sheet.isDialog) {
        Dialog(
            onDismissRequest = {
                if (sheet.canDismiss) {
                    scope.launch(Dispatchers.Main.immediate) {
                        sheetController.hide()
                        sheetController.clear()
                    }
                }
            }
        ) {
            sheet.Content()
        }
    } else {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = sheet.initialFullExpand
        )

        LaunchedEffect(sheet) {
            launch(start = CoroutineStart.UNDISPATCHED) {
                sheetState.show()
                if (sheet.initialFullExpand) {
                    sheetState.expand()
                }
            }
        }

        LaunchedEffect(sheetController.shouldDismiss.value) {
            if (sheetController.shouldDismiss.value) {
                launch(start = CoroutineStart.UNDISPATCHED) {
                    sheetState.hide()
                    sheetController.clear()
                }
            }
        }

        ModalBottomSheet(
            modifier = Modifier
                .padding(innerPadding)
                .statusBarsPadding(),
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch(Dispatchers.Main.immediate) {
                    sheetController.clear()
                }
            }
        ) {
            sheet.Content()
        }
    }
}
