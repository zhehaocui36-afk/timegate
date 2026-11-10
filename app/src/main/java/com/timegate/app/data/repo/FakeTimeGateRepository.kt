package com.timegate.app.data.repo

import com.timegate.app.data.local.AppPolicyEntity
import com.timegate.app.data.local.DaySummaryEntity
import com.timegate.app.data.local.FocusSessionEntity
import com.timegate.app.data.local.GateLogEntity
import com.timegate.app.data.local.UsageSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * 仓库的内存 Fake 实现（预览 / 单测用，R-80「接口可被 Fake 替换」）。
 *
 * 与 [RoomTimeGateRepository] 实现同一 [TimeGateRepository] 接口，UI / 测试
 * 注入本类即可脱离 Room。数据存内存，进程结束即失，仅用于不持久化场景。
 *
 * 线程模型：面向单线程（预览态 / 单测）。如需多线程，由 S12 决定是否加锁。
 */
class FakeTimeGateRepository : TimeGateRepository {

    private val apps = MutableStateFlow<List<AppPolicyEntity>>(emptyList())
    private val usageSessions = MutableStateFlow<List<UsageSessionEntity>>(emptyList())
    private val gateLogs = MutableStateFlow<List<GateLogEntity>>(emptyList())
    private val focusSessions = MutableStateFlow<List<FocusSessionEntity>>(emptyList())
    private val daySummaries = MutableStateFlow<Map<String, DaySummaryEntity>>(emptyMap())

    private var nextId = 1L

    // ---- 受管应用 ----

    override fun observeManagedApps(): Flow<List<AppPolicyEntity>> =
        apps.map { list -> list.filter { it.managed } }

    override suspend fun getManagedApps(): List<AppPolicyEntity> =
        apps.value.filter { it.managed }

    override suspend fun upsertApp(app: AppPolicyEntity) {
        apps.value = apps.value.filterNot { it.packageName == app.packageName } + app
    }

    override suspend fun deleteApp(packageName: String) {
        apps.value = apps.value.filterNot { it.packageName == packageName }
    }

    override suspend fun renameApp(packageName: String, newName: String) {
        apps.value = apps.value.map {
            if (it.packageName == packageName) it.copy(displayName = newName) else it
        }
    }

    override suspend fun setAppManaged(packageName: String, managed: Boolean) {
        apps.value = apps.value.map {
            if (it.packageName == packageName) it.copy(managed = managed) else it
        }
    }

    // ---- 使用会话 ----

    override fun observeUsageSessions(dayKey: String): Flow<List<UsageSessionEntity>> =
        usageSessions.map { list -> list.filter { it.dayKey == dayKey } }

    override suspend fun getUsageSessions(dayKey: String): List<UsageSessionEntity> =
        usageSessions.value.filter { it.dayKey == dayKey }

    override suspend fun insertUsageSession(session: UsageSessionEntity): Long {
        val id = nextId++
        usageSessions.value = usageSessions.value + session.copy(id = id)
        return id
    }

    override suspend fun updateUsageSession(session: UsageSessionEntity) {
        usageSessions.value = usageSessions.value.map {
            if (it.id == session.id) session else it
        }
    }

    // ---- 闸门日志 ----

    override fun observeGateLogs(dayKey: String): Flow<List<GateLogEntity>> =
        gateLogs.map { list -> list.filter { it.dayKey == dayKey } }

    override suspend fun getGateLogs(dayKey: String): List<GateLogEntity> =
        gateLogs.value.filter { it.dayKey == dayKey }

    override suspend fun insertGateLog(log: GateLogEntity): Long {
        val id = nextId++
        gateLogs.value = gateLogs.value + log.copy(id = id)
        return id
    }

    // ---- 专注会话 ----

    override fun observeFocusSessions(): Flow<List<FocusSessionEntity>> = focusSessions

    override suspend fun insertFocusSession(session: FocusSessionEntity): Long {
        val id = nextId++
        focusSessions.value = focusSessions.value + session.copy(id = id)
        return id
    }

    override suspend fun updateFocusSession(session: FocusSessionEntity) {
        focusSessions.value = focusSessions.value.map {
            if (it.id == session.id) session else it
        }
    }

    // ---- 每日汇总 ----

    override fun observeDaySummary(dayKey: String): Flow<DaySummaryEntity?> =
        daySummaries.map { map -> map[dayKey] }

    override suspend fun getDaySummary(dayKey: String): DaySummaryEntity? =
        daySummaries.value[dayKey]

    override suspend fun upsertDaySummary(summary: DaySummaryEntity) {
        daySummaries.value = daySummaries.value + (summary.dayKey to summary)
    }

    // ---- 清空 ----

    override suspend fun clearAll() {
        apps.value = emptyList()
        usageSessions.value = emptyList()
        gateLogs.value = emptyList()
        focusSessions.value = emptyList()
        daySummaries.value = emptyMap()
        nextId = 1L
    }
}
