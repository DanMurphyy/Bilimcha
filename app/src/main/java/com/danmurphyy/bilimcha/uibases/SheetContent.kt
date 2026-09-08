package com.danmurphyy.bilimcha.uibases

import androidx.compose.runtime.Composable

interface SheetContent {
    val initialFullExpand: Boolean
        get() = false

    val isDialog: Boolean
        get() = false

    val canDismiss: Boolean
        get() = true

    @Composable
    fun Content()
}