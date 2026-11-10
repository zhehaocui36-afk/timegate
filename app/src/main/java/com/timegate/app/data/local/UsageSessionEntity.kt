package com.timegate.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 使用会话（计时记录，R-52 / R-54）。
 *
 * 表名 `usage_session`。**只存时间戳（Long），不存「剩余秒数」**；
 * 剩余时长每次由 `domain/time` 的 [com.timegate.app.domain.time.RemainingTime] 现算。
 *
 * 三个时间戳语义：
 * - `startedAt`：开始计时（放行进入 IN_USE）。
 * - `leftAt`：离开时刻（切后台 / 息屏 / 进程被回收），可空。
 * - `endedAt`：结束时刻（提前结束 / 到时），可空。
 *
 * 日期统一存 `day_key TEXT`（开始时刻所属的记账日），无 `Date` 字段。
 */
@Entity(
    tableName = "usage_session",
    indices = [Index(value = ["day_key"]), Index(value = ["package_name"])],
)
data class UsageSessionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /** 应用包名（逻辑关联 app_policy.package_name，不设外键约束以保留历史）。 */
    @ColumnInfo(name = "package_name")
    val packageName: String,

    /** 记账日，`YYYY-MM-DD`（本地 04:00 换日）。 */
    @ColumnInfo(name = "day_key")
    val dayKey: String,

    /** 开始时间戳（epoch millis）。 */
    @ColumnInfo(name = "started_at")
    val startedAt: Long,

    /** 离开时刻（epoch millis），可空。 */
    @ColumnInfo(name = "left_at")
    val leftAt: Long?,

    /** 结束时刻（epoch millis），可空。 */
    @ColumnInfo(name = "ended_at")
    val endedAt: Long?,

    /** 承诺时长（分钟）。 */
    @ColumnInfo(name = "planned_minutes")
    val plannedMinutes: Int,

    /** 实际计入分钟（R-53：向上取整、最少 1 分钟），可空（未结束时为空）。 */
    @ColumnInfo(name = "actual_minutes")
    val actualMinutes: Int?,
)
