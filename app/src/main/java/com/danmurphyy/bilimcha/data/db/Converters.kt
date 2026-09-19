package com.danmurphyy.bilimcha.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromUserStats(stats: UserStats): String {
        return Json.encodeToString(stats)
    }

    @TypeConverter
    fun toUserStats(json: String): UserStats {
        return Json.decodeFromString(json)
    }
}
