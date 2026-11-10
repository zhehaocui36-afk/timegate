package com.timegate.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 闸门日志 DAO（R-43 / R-44，供漏斗统计）。
 */
@Dao
interface GateLogDao {

    /** 观察某记账日的闸门日志。 */
    @Query("SELECT * FROM gate_log WHERE day_key = :dayKey ORDER BY opened_at")
    fun observeForDay(dayKey: String): Flow<List<GateLogEntity>>

    /** 一次性读取某记账日的闸门日志。 */
    @Query("SELECT * FROM gate_log WHERE day_key = :dayKey ORDER BY opened_at")
    suspend fun getForDay(dayKey: String): List<GateLogEntity>

    /** 插入，返回自增主键。 */
    @Insert
    suspend fun insert(log: GateLogEntity): Long

    /** 某日开闸总次数。 */
    @Query("SELECT COUNT(*) FROM gate_log WHERE day_key = :dayKey")
    suspend fun countForDay(dayKey: String): Int

    /** 某日在闸门前离开（abandoned）的次数。 */
    @Query(
        "SELECT COUNT(*) FROM gate_log WHERE day_key = :dayKey " +
            "AND outcome IN ('ABANDONED_DURATION', 'ABANDONED_INTENT', 'ABANDONED_ALTERNATIVE')",
    )
    suspend fun countAbandonedForDay(dayKey: String): Int
}
