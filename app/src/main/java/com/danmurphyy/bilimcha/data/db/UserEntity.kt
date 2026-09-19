package com.danmurphyy.bilimcha.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val birthDate: Long,
    val email: String = "",
    val country: String = "",
    val stats: UserStats = UserStats(),
)

@Parcelize
data class UserStats(
    val numbers: List<NumbersRangeStatus> = emptyList(),
) : Parcelable

@Parcelize
data class NumbersRangeStatus(
    val rangeId: String,
    val isUnlocked: Boolean = false,
    val isPassed: Boolean = false,
) : Parcelable
