package com.timegate.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 受管应用（R-100 / R-13）。
 *
 * 表名 `app_policy`。一条记录 = 一个被纳入监控的应用。
 * 日期不涉及本表；无 `Date` 字段。
 */
@Entity(tableName = "app_policy")
data class AppPolicyEntity(
    /** 应用包名，作为主键（检测层拿到的就是包名，天然唯一）。 */
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,

    /** 显示名（可被用户改名）。 */
    @ColumnInfo(name = "display_name")
    val displayName: String,

    /** 图标缩写（1–2 字），用于无图标时的占位展示。 */
    @ColumnInfo(name = "short_label")
    val shortLabel: String,

    /** 单应用上限（分钟）。 */
    @ColumnInfo(name = "quota_minutes")
    val quotaMinutes: Int,

    /** 是否受管（on=true）。 */
    @ColumnInfo(name = "managed")
    val managed: Boolean,
)
