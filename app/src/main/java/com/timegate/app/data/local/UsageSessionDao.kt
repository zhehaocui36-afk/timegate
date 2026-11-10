package com.timegate.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * 使用会话 DAO（R-52 / R-54）。
 */
@Dao
interface UsageSessionDao {

    /** 观察某记账日的使用会话。 */
    @Query("SELECT * FROM usage_session WHERE day_key = :dayKey ORDER BY started_at")
    fun observeForDay(dayKey: String): Flow<List<UsageSessionEntity>>

    /** 一次性读取某记账日的使用会话。 */
    @Query("SELECT * FROM usage_session WHERE day_key = :dayKey ORDER BY started_at")
    suspend fun getForDay(dayKey: String): List<UsageSessionEntity>

    /** 插入，返回自增主键。 */
    @Insert
    suspend fun insert(session: UsageSessionEntity): Long

    /** 更新（如写入 leftAt / endedAt / actualMinutes）。 */
    @Update
    suspend fun update(session: UsageSessionEntity)
}
