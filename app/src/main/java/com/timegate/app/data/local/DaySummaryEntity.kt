package com.timegate.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 每日汇总（R-101 / R-102）。
 *
 * 表名 `day_summary`。一行 = 一天的聚合快照，供「今日环」与「7 天柱图」直接读，
 * 避免每次现算 SUM。
 *
 * 日期统一存 `day_key TEXT`（主键），无 `Date` 字段。
 */
@Entity(tableName = "day_summary")
data class DaySummaryEntity(
    /** 记账日，`YYYY-MM-DD`（主键）。 */
    @PrimaryKey
    @ColumnInfo(name = "day_key")
    val dayKey: String,

    /** 当日总使用分钟。 */
    @ColumnInfo(name = "total_minutes")
    val totalMinutes: Int,
)
