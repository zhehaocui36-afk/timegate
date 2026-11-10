package com.timegate.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 每日汇总 DAO（R-101 / R-102）。
 */
@Dao
interface DaySummaryDao {

    /** 观察某日的汇总。 */
    @Query("SELECT * FROM day_summary WHERE day_key = :dayKey")
    fun observe(dayKey: String): Flow<DaySummaryEntity?>

    /** 一次性读取某日的汇总。 */
    @Query("SELECT * FROM day_summary WHERE day_key = :dayKey")
    suspend fun get(dayKey: String): DaySummaryEntity?

    /** 最近 N 天汇总（新在前），供 7 天柱图。 */
    @Query("SELECT * FROM day_summary ORDER BY day_key DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<DaySummaryEntity>

    /** 插入或整行覆盖。 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(summary: DaySummaryEntity)
}
