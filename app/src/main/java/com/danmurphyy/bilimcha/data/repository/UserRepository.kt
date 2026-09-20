package com.danmurphyy.bilimcha.data.repository

import com.danmurphyy.bilimcha.data.db.NumbersRangeStatus
import com.danmurphyy.bilimcha.data.db.UserDao
import com.danmurphyy.bilimcha.data.db.UserEntity
import com.danmurphyy.bilimcha.data.db.UserStats
import com.danmurphyy.bilimcha.data.models.NumbersRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao,
) {
    fun getUser(): Flow<UserEntity?> = userDao.getUser()

    suspend fun saveUser(name: String, birthDate: Long, country: String = "") {
        val initialStats = UserStats(
            numbers = listOf(
                NumbersRangeStatus(
                    rangeId = NumbersRange.RANGE_0_10.id,
                    isUnlocked = true,
                )
            )
        )

        userDao.insertUser(
            UserEntity(
                name = name,
                birthDate = birthDate,
                country = country,
                stats = initialStats,
            )
        )
    }

    suspend fun updateNumbersStats(range: NumbersRange) {
        val user = userDao.getUser().first() ?: return

        val nextRange = NumbersRange.entries
            .getOrNull(NumbersRange.entries.indexOf(range) + 1)

        val updatedNumbers = user.stats.numbers
            .map { status ->
                when (status.rangeId) {
                    range.id -> status.copy(
                        isUnlocked = true,
                        isPassed = true,
                    )

                    nextRange?.id -> status.copy(
                        isUnlocked = true,
                    )

                    else -> status
                }
            }
            .let { numbers ->
                if (nextRange != null && numbers.none { it.rangeId == nextRange.id }) {
                    numbers + NumbersRangeStatus(
                        rangeId = nextRange.id,
                        isUnlocked = true,
                    )
                } else {
                    numbers
                }
            }

        userDao.updateUser(
            user.copy(
                stats = user.stats.copy(
                    numbers = updatedNumbers,
                ),
            ),
        )
    }

    suspend fun clearUser() {
        userDao.clearUser()
    }

    suspend fun updateProfile(name: String, birthDate: Long, country: String) {
        val user = userDao.getUser().first() ?: return
        userDao.updateUser(
            user.copy(
                name = name,
                birthDate = birthDate,
                country = country
            )
        )
    }
}
