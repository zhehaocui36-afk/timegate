package com.timegate.app.data.repo

import com.timegate.app.data.local.AppPolicyEntity
import com.timegate.app.data.local.DaySummaryEntity
import com.timegate.app.data.local.FocusSessionEntity
import com.timegate.app.data.local.GateLogEntity
import com.timegate.app.data.local.UsageSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 数据仓库接口（S06）。
 *
 * 上层（ui / service / domain）**只通过本接口访问数据，绝不直接碰 DAO**（R-80）。
 * 本接口可被 [RoomTimeGateRepository]（生产）与 [FakeTimeGateRepository]（预览 / 测试）替换。
 *
 * 口径约定（详见 `docs/DATA.md`）：
 * - 日期一律 `day_key`（`YYYY-MM-DD`，本地 04:00 换日），Entity 无 `Date`。
 * - 计时只存时间戳（Long），剩余由 `domain/time` 现算。
 * - 清空 = 删库重建（[clearAll]）。
 */
interface TimeGateRepository {

    // ---- 受管应用（app_policy）----

    /** 观察受管应用列表。 */
    fun observeManagedApps(): Flow<List<AppPolicyEntity>>

    /** 一次性读取受管应用列表。 */
    suspend fun getManagedApps(): List<AppPolicyEntity>

    /** 插入或覆盖受管应用。 */
    suspend fun upsertApp(app: AppPolicyEntity)

    /** 移除受管应用（删除记录）。 */
    suspend fun deleteApp(packageName: String)

    /** 改显示名。 */
    suspend fun renameApp(packageName: String, newName: String)

    /** 开关受管状态。 */
    suspend fun setAppManaged(packageName: String, managed: Boolean)

    // ---- 使用会话（usage_session）----

    /** 观察某记账日的使用会话。 */
    fun observeUsageSessions(dayKey: String): Flow<List<UsageSessionEntity>>

    /** 一次性读取某记账日的使用会话。 */
    suspend fun getUsageSessions(dayKey: String): List<UsageSessionEntity>

    /** 插入会话，返回自增主键。 */
    suspend fun insertUsageSession(session: UsageSessionEntity): Long

    /** 更新会话（写 leftAt / endedAt / actualMinutes）。 */
    suspend fun updateUsageSession(session: UsageSessionEntity)

    // ---- 闸门日志（gate_log）----

    /** 观察某记账日的闸门日志。 */
    fun observeGateLogs(dayKey: String): Flow<List<GateLogEntity>>

    /** 一次性读取某记账日的闸门日志。 */
    suspend fun getGateLogs(dayKey: String): List<GateLogEntity>

    /** 插入闸门日志，返回自增主键。 */
    suspend fun insertGateLog(log: GateLogEntity): Long

    // ---- 专注会话（focus_session）----

    /** 观察全部专注会话。 */
    fun observeFocusSessions(): Flow<List<FocusSessionEntity>>

    /** 插入专注会话，返回自增主键。 */
    suspend fun insertFocusSession(session: FocusSessionEntity): Long

    /** 更新专注会话（写 endedAt / completed）。 */
    suspend fun updateFocusSession(session: FocusSessionEntity)

    // ---- 每日汇总（day_summary）----

    /** 观察某日汇总。 */
    fun observeDaySummary(dayKey: String): Flow<DaySummaryEntity?>

    /** 一次性读取某日汇总。 */
    suspend fun getDaySummary(dayKey: String): DaySummaryEntity?

    /** 插入或覆盖某日汇总。 */
    suspend fun upsertDaySummary(summary: DaySummaryEntity)

    // ---- 清空 ----

    /** 清空数据 = 删库重建（R-82）。 */
    suspend fun clearAll()
}
