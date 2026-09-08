package com.danmurphyy.bilimcha.profile

import com.danmurphyy.bilimcha.navigations.NavAnimationStyle
import com.danmurphyy.bilimcha.navigations.NavEntryScope
import com.danmurphyy.bilimcha.navigations.ProfileHomeDetailKey

import com.danmurphyy.bilimcha.navigations.registerScreen

fun NavEntryScope.profileEntryBuilder() {

    registerScreen<ProfileHomeDetailKey>(NavAnimationStyle.HORIZONTAL) {
        ProfileHomeDetailScreen(it)
    }
}