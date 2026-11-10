package com.timegate.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * 专注会话 DAO（R-90 / R-91）。
 */
@Dao
interface FocusSessionDao {

    /** 观察全部专注会话（新在前）。 */
    @Query("SELECT * FROM focus_session ORDER BY started_at DESC")
    fun observeAll(): Flow<List<FocusSessionEntity>>

    /** 一次性读取全部专注会话。 */
    @Query("SELECT * FROM focus_session ORDER BY started_at DESC")
    suspend fun getAll(): List<FocusSessionEntity>

    /** 插入，返回自增主键。 */
    @Insert
    suspend fun insert(session: FocusSessionEntity): Long

    /** 更新（如写入 endedAt / completed）。 */
    @Update
    suspend fun update(session: FocusSessionEntity)
}
