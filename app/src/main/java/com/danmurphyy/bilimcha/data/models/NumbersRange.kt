package com.danmurphyy.bilimcha.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
enum class NumbersRange(val id: String) : Parcelable {
    RANGE_0_10("0_10"),
    RANGE_11_20("11_20"),
    RANGE_21_30("21_30"),
    RANGE_TENS("40_90"),
    RANGE_HUNDREDS("100_1M"),
    RANGE_ALL_AVAILABLE("All_Available");

    companion object {
        fun fromId(id: String): NumbersRange = entries.find { it.id == id } ?: RANGE_0_10
    }
}
