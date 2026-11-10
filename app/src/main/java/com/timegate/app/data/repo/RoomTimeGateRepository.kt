package com.timegate.app.data.repo

import android.content.Context
import com.timegate.app.data.local.AppPolicyEntity
import com.timegate.app.data.local.DaySummaryEntity
import com.timegate.app.data.local.FocusSessionEntity
import com.timegate.app.data.local.GateLogEntity
import com.timegate.app.data.local.TimeGateDatabase
import com.timegate.app.data.local.UsageSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 仓库的 Room 本地实现（生产）。
 *
 * 每次访问 DAO 时经 [TimeGateDatabase.getInstance] 拿当前实例，因此 [clearAll]
 * 删库后下次访问自动拿到重建的空库，无需缓存 DAO 引用。
 */
class RoomTimeGateRepository(
    private val context: Context,
) : TimeGateRepository {

    private val db: TimeGateDatabase
        get() = TimeGateDatabase.getInstance(context)

    // ---- 受管应用 ----

    override fun observeManagedApps(): Flow<List<AppPolicyEntity>> =
        db.appPolicyDao().observeManagedApps()

    override suspend fun getManagedApps(): List<AppPolicyEntity> =
        db.appPolicyDao().getManagedApps()

    override suspend fun upsertApp(app: AppPolicyEntity) =
        db.appPolicyDao().upsert(app)

    override suspend fun deleteApp(packageName: String) {
        db.appPolicyDao().getApp(packageName)?.let { db.appPolicyDao().delete(it) }
    }

    override suspend fun renameApp(packageName: String, newName: String) =
        db.appPolicyDao().rename(packageName, newName)

    override suspend fun setAppManaged(packageName: String, managed: Boolean) =
        db.appPolicyDao().setManaged(packageName, managed)

    // ---- 使用会话 ----

    override fun observeUsageSessions(dayKey: String): Flow<List<UsageSessionEntity>> =
        db.usageSessionDao().observeForDay(dayKey)

    override suspend fun getUsageSessions(dayKey: String): List<UsageSessionEntity> =
        db.usageSessionDao().getForDay(dayKey)

    override suspend fun insertUsageSession(session: UsageSessionEntity): Long =
        db.usageSessionDao().insert(session)

    override suspend fun updateUsageSession(session: UsageSessionEntity) =
        db.usageSessionDao().update(session)

    // ---- 闸门日志 ----

    override fun observeGateLogs(dayKey: String): Flow<List<GateLogEntity>> =
        db.gateLogDao().observeForDay(dayKey)

    override suspend fun getGateLogs(dayKey: String): List<GateLogEntity> =
        db.gateLogDao().getForDay(dayKey)

    override suspend fun insertGateLog(log: GateLogEntity): Long =
        db.gateLogDao().insert(log)

    // ---- 专注会话 ----

    override fun observeFocusSessions(): Flow<List<FocusSessionEntity>> =
        db.focusSessionDao().observeAll()

    override suspend fun insertFocusSession(session: FocusSessionEntity): Long =
        db.focusSessionDao().insert(session)

    override suspend fun updateFocusSession(session: FocusSessionEntity) =
        db.focusSessionDao().update(session)

    // ---- 每日汇总 ----

    override fun observeDaySummary(dayKey: String): Flow<DaySummaryEntity?> =
        db.daySummaryDao().observe(dayKey)

    override suspend fun getDaySummary(dayKey: String): DaySummaryEntity? =
        db.daySummaryDao().get(dayKey)

    override suspend fun upsertDaySummary(summary: DaySummaryEntity) =
        db.daySummaryDao().upsert(summary)

    // ---- 清空 ----

    override suspend fun clearAll() {
        TimeGateDatabase.destroy(context)
    }
}
