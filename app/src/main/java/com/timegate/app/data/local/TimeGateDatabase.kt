package com.timegate.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room 数据库（R-80）。
 *
 * 五表：`app_policy` / `usage_session` / `gate_log` / `focus_session` / `day_summary`。
 * 版本 1，`exportSchema = true`（schema JSON 导出到 `app/schemas`，勿手改）。
 */
@Database(
    entities = [
        AppPolicyEntity::class,
        UsageSessionEntity::class,
        GateLogEntity::class,
        FocusSessionEntity::class,
        DaySummaryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class TimeGateDatabase : RoomDatabase() {

    abstract fun appPolicyDao(): AppPolicyDao
    abstract fun usageSessionDao(): UsageSessionDao
    abstract fun gateLogDao(): GateLogDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun daySummaryDao(): DaySummaryDao

    companion object {
        const val DATABASE_NAME = "timegate.db"

        @Volatile
        private var INSTANCE: TimeGateDatabase? = null

        /**
         * 获取数据库单例。首次访问时创建（onCreate 建五表）。
         * 不在 Application 里初始化（`TimeGateApp` 属 S04），由仓库层按需惰性创建。
         */
        fun getInstance(context: Context): TimeGateDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TimeGateDatabase::class.java,
                    DATABASE_NAME,
                ).build().also { INSTANCE = it }
            }

        /**
         * 清空数据 = 删库重建（R-82）。
         *
         * 关闭当前连接、删除数据库文件（含 -wal / -shm），下次 [getInstance] 重建空库。
         * 不做软删除、不做云备份。
         */
        fun destroy(context: Context) {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
                context.applicationContext.deleteDatabase(DATABASE_NAME)
            }
        }
    }
}
