package com.danmurphyy.bilimcha.navigations

import com.danmurphyy.bilimcha.main.mainEntryBuilder
import com.danmurphyy.bilimcha.numbers.numberEntryBuilder
import com.danmurphyy.bilimcha.profile.profileEntryBuilder

val appNavEntries: List<NavEntryScope.() -> Unit> = listOf(
    NavEntryScope::mainEntryBuilder,
    NavEntryScope::numberEntryBuilder,
    NavEntryScope::profileEntryBuilder,
)
