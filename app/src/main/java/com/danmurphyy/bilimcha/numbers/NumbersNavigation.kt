package com.danmurphyy.bilimcha.numbers

import com.danmurphyy.bilimcha.navigations.NavAnimationStyle
import com.danmurphyy.bilimcha.navigations.NavEntryScope
import com.danmurphyy.bilimcha.navigations.NumbersDashboardKey
import com.danmurphyy.bilimcha.navigations.NumbersPracticeKey
import com.danmurphyy.bilimcha.navigations.NumbersTestKey
import com.danmurphyy.bilimcha.navigations.registerScreen
import com.danmurphyy.bilimcha.numbers.dashboard.NumbersDashboardScreen

fun NavEntryScope.numberEntryBuilder(){
    registerScreen<NumbersDashboardKey>(NavAnimationStyle.HORIZONTAL) {
        NumbersDashboardScreen(it)
    }

    registerScreen<NumbersTestKey>(NavAnimationStyle.HORIZONTAL) {
        NumbersTestScreen(it)
    }
    registerScreen<NumbersPracticeKey>(NavAnimationStyle.HORIZONTAL) {
        NumbersPracticeScreen(it)
    }
}