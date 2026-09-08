package com.danmurphyy.bilimcha.navigations

import androidx.compose.runtime.compositionLocalOf

val LocalSheetController = compositionLocalOf<SheetController> {
    error("SheetController not provided")
}

val LocalBackStackController = compositionLocalOf<BackStackController> {
    error("BackStackController not provided")
}