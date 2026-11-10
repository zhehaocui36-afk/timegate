package com.timegate.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 专注会话（R-90 / R-91）。
 *
 * 表名 `focus_session`。V1 只做 25 分钟专注 + 到时页 10 分钟短专注。
 *
 * 日期统一存 `day_key TEXT`，无 `Date` 字段。
 */
@Entity(
    tableName = "focus_session",
    indices = [Index(value = ["day_key"])],
)
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /** 记账日，`YYYY-MM-DD`。 */
    @ColumnInfo(name = "day_key")
    val dayKey: String,

    /** 开始时间戳（epoch millis）。 */
    @ColumnInfo(name = "started_at")
    val startedAt: Long,

    /** 结束时间戳（epoch millis），可空（进行中为空）。 */
    @ColumnInfo(name = "ended_at")
    val endedAt: Long?,

    /** 承诺专注时长（分钟）：25 或 10。 */
    @ColumnInfo(name = "planned_minutes")
    val plannedMinutes: Int,

    /** 是否完成（放弃专注 = false）。 */
    @ColumnInfo(name = "completed")
    val completed: Boolean,
)
