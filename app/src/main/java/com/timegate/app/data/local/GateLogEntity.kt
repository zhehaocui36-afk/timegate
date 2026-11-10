package com.timegate.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 闸门日志（R-40 / R-43 / R-44）。
 *
 * 表名 `gate_log`。每次「打开受管应用触发闸门」记一条。
 * 用于闸门漏斗统计：`total`（总次数）、`passed`（放行）、`abandoned`（闸门前离开）。
 *
 * 日期统一存 `day_key TEXT`，无 `Date` 字段。
 */
@Entity(
    tableName = "gate_log",
    indices = [Index(value = ["day_key"]), Index(value = ["package_name"])],
)
data class GateLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    /** 应用包名（逻辑关联 app_policy.package_name）。 */
    @ColumnInfo(name = "package_name")
    val packageName: String,

    /** 记账日，`YYYY-MM-DD`。 */
    @ColumnInfo(name = "day_key")
    val dayKey: String,

    /** 开闸时刻（epoch millis）。 */
    @ColumnInfo(name = "opened_at")
    val openedAt: Long,

    /** 关闸时刻（epoch millis），可空。 */
    @ColumnInfo(name = "closed_at")
    val closedAt: Long?,

    /** 所选时长（分钟）；在时长步就退出则为空。 */
    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int?,

    /** 意图文本；未走到意图步则为空。 */
    @ColumnInfo(name = "intent")
    val intent: String?,

    /** 替代动作；未走到替代步则为空。 */
    @ColumnInfo(name = "alternative")
    val alternative: String?,

    /** 结果（枚举，经 TypeConverter 存为 String）。 */
    @ColumnInfo(name = "outcome")
    val outcome: GateOutcome,
)
