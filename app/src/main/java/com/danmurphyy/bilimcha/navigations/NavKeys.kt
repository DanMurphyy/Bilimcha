package com.danmurphyy.bilimcha.navigations

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey as BaseNavKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Marker interface for all navigation keys.
 */
interface NavKey : BaseNavKey, Parcelable

//Main Feature
@Serializable
@Parcelize
data object MainHomeKey : NavKey

//Numbers Feature
@Serializable
@Parcelize
data object NumbersDashboardKey : NavKey

@Serializable
@Parcelize
data class PracticeDashboardKey(val practiceType: PracticeType) : NavKey

@Serializable
@Parcelize
data class NumbersTestKey(
    val fromValue: Int,
    val toValue: Int,
    val rangeMode: String = "all",
    val language: String = "en",
    val visualityType: VisualityType = VisualityType.Symbols,
    val isRepeat: Boolean = false,
    val isAutoMode: Boolean = true,
) : NavKey

@Serializable
@Parcelize
data class NumbersPracticeKey(
    val fromValue: Int,
    val toValue: Int,
    val rangeMode: String = "all",
    val language: String = "en",
    val visualityType: VisualityType = VisualityType.Symbols,
    val isRepeat: Boolean = false,
    val isAutoMode: Boolean = true,
) : NavKey

@Serializable
enum class VisualityType {
    Symbols,
    Text,
    Both
}

//Abc Feature
@Serializable
@Parcelize
data object AbcKey : NavKey

//Abc Feature
@Serializable
@Parcelize
data object AnimalsKey : NavKey

@Serializable
@Parcelize
data class MainProductKey(val id: String) : NavKey

@Serializable
@Parcelize
data class ProfileHomeDetailKey(val data: UserFeatureData) : NavKey

@Serializable
@Parcelize
data class UserFeatureData(
    val id: String,
    val name: String,
    val extras: String,
) : Parcelable

@Serializable
enum class PracticeType {
    Numbers,
    Alphabet,
    Animals
}
