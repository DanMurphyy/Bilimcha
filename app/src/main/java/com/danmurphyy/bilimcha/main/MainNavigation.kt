package com.danmurphyy.bilimcha.main

import com.danmurphyy.bilimcha.navigations.AbcKey
import com.danmurphyy.bilimcha.navigations.AnimalsKey
import com.danmurphyy.bilimcha.navigations.MainHomeKey
import com.danmurphyy.bilimcha.navigations.MainProductKey
import com.danmurphyy.bilimcha.navigations.NavAnimationStyle
import com.danmurphyy.bilimcha.navigations.NavEntryScope
import com.danmurphyy.bilimcha.navigations.PracticeDashboardKey
import com.danmurphyy.bilimcha.navigations.registerScreen

fun NavEntryScope.mainEntryBuilder(){
    registerScreen<MainHomeKey>(NavAnimationStyle.HORIZONTAL) {
        MainHomeScreen(it)
    }

    registerScreen<MainProductKey>() {
        MainProductScreen(it)
    }

    registerScreen<PracticeDashboardKey>() {
        PracticeDashboardScreen(it)
    }


    registerScreen<AbcKey>(NavAnimationStyle.HORIZONTAL) {
        AbcScreen(it)
    }

    registerScreen<AnimalsKey>(NavAnimationStyle.HORIZONTAL) {
        AnimalsScreen(it)
    }
}