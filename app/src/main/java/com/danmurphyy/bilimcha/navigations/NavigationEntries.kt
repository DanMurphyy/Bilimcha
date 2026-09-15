package com.danmurphyy.bilimcha.navigations

import com.danmurphyy.bilimcha.main.mainEntryBuilder
import com.danmurphyy.bilimcha.features.numbers.numberEntryBuilder
import com.danmurphyy.bilimcha.features.profile.profileEntryBuilder

val appNavEntries: List<NavEntryScope.() -> Unit> = listOf(
    NavEntryScope::mainEntryBuilder,
    NavEntryScope::numberEntryBuilder,
    NavEntryScope::profileEntryBuilder,
)
