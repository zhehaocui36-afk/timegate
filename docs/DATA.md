# 数据层与时间口径 · TimeGate（S06）

> **版本** v1.0 · **状态** 已交付 · **更新** 2026-11-10
> 本文冻结 S06 的两条口径：**凌晨 04:00 换日** 与 **只存时间戳不存剩余秒**，并给出 Room 五表的表结构、索引、迁移与清空策略。
> 依据 `docs/REQUIREMENTS.md` R-52/53/54/70/71/80/81/82/83、`docs/ARCHITECTURE.md` §2/#6、`docs/archive/HANDOFF.md` E 节。

---

## 1. 一句话

Room 本地库五表（`app_policy` / `usage_session` / `gate_log` / `focus_session` / `day_summary`）；日期一律 `day_key TEXT`，计时只存时间戳；清空 = 删库重建。上层只经 `data/repo` 仓库接口访问，不碰 DAO。

---

## 2. 时间口径（写死）

### 2.1 凌晨 04:00 换日（R-70）

- `dayKey = 本地日期(ts − 4h)`，即对 epoch millis 直接减 4 小时**绝对时长**，再转本地日期取年月日。
- 本地时间 04:00 为界：**04:00 前归前一日，04:00 起归当日**。
  - 东八区 03:50 → 前一日；04:10 → 当日。
- 时区取**系统默认时区**（`ZoneId.systemDefault()`），**不硬编码东八区**。
- 实现：`domain/time/DayKey.kt`，纯 Kotlin、零 `import android.*`。

### 2.2 day_key 本地拼日期（R-71）

- 格式固定 `YYYY-MM-DD`（`uuuu-MM-dd`），用本地年月日拼装。
- **禁止** `toISOString().slice(0, 10)` 一类 UTC 截断——那会让东八区整体偏 8 小时。
- ⚠️ **口径裁决**：`REQUIREMENTS.md` R-71 字面写「东八区 00:30 归当天」，与 R-70「04:00 前归前一日」冲突（00:30 < 04:00）。本实现以 `HANDOFF.md` E.2（唯一权威）+ `demo/index.html` 的 `dayKey` 函数 + 任务约束「dayKey = 本地日期(ts − 4h)」三方一致为准：**东八区 00:30 归前一日**。R-71 的「00:30 归当天」是未叠加 4:00 换日时「本地日期 vs UTC 截断」的举例措辞，已在本包测试与 `handoff/S06.md` 中标注，建议由项目负责人确认是否需要回改 R-71 措辞（不影响代码）。

### 2.3 只存时间戳，不存剩余秒（R-52 / R-54）

- 计时只记录三个时间戳（毫秒 `Long`）：`startedAt`（开始）、`leftAt`（离开）、`endedAt`（结束）。
- 数据层**不存在**「剩余秒数」字段；剩余每次现算：`剩余 = 承诺 − 实际经过`，向下取整到秒，误差 < 1 秒。
- 息屏 / 切后台 / 进程被回收期间，倒计时**照常流逝**：回前台按时间戳补算。
- 实现：`domain/time/RemainingTime.kt`（纯 Kotlin）。

### 2.4 提前结束按实际分钟计（R-53）

- 提前结束：按实际已用分钟计入，**向上取整且最少 1 分钟**（30 秒 → 1 分钟；3 分 10 秒 → 4 分钟）。
- 该取整逻辑由 S09 状态机在写 `usage_session.actual_minutes` 时执行，数据层只存结果。

---

## 3. 五表结构（R-80）

数据库：`timegate.db`，`version = 1`，`exportSchema = true`（schema JSON 导出到 `app/schemas`，勿手改）。

> 列类型约定：`TEXT` = 字符串；`INTEGER` = Long / Int / Boolean；主键自增用 `INTEGER PRIMARY KEY AUTOINCREMENT`。

### 3.1 app_policy（受管应用）

| 列 | 类型 | 约束 | 说明 |
|---|---|---|---|
| package_name | TEXT | 主键 | 应用包名 |
| display_name | TEXT | 非空 | 显示名（可改名） |
| short_label | TEXT | 非空 | 图标缩写（1–2 字） |
| quota_minutes | INTEGER | 非空 | 单应用上限（分钟） |
| managed | INTEGER | 非空 | 是否受管（0/1） |

索引：主键 `package_name`（隐含唯一索引）。

### 3.2 usage_session（使用会话）

| 列 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | INTEGER | 主键自增 | — |
| package_name | TEXT | 非空 | 包名（逻辑关联，无外键约束） |
| day_key | TEXT | 非空 | 记账日 `YYYY-MM-DD` |
| started_at | INTEGER | 非空 | 开始时间戳 |
| left_at | INTEGER | 可空 | 离开时刻 |
| ended_at | INTEGER | 可空 | 结束时刻 |
| planned_minutes | INTEGER | 非空 | 承诺时长（分钟） |
| actual_minutes | INTEGER | 可空 | 实际计入分钟（R-53 取整） |

索引：`day_key`、`package_name`。

### 3.3 gate_log（闸门日志）

| 列 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | INTEGER | 主键自增 | — |
| package_name | TEXT | 非空 | 包名 |
| day_key | TEXT | 非空 | 记账日 `YYYY-MM-DD` |
| opened_at | INTEGER | 非空 | 开闸时刻 |
| closed_at | INTEGER | 可空 | 关闸时刻 |
| duration_minutes | INTEGER | 可空 | 所选时长 |
| intent | TEXT | 可空 | 意图文本 |
| alternative | TEXT | 可空 | 替代动作 |
| outcome | TEXT | 非空 | 结果枚举（见下） |

索引：`day_key`、`package_name`。

`outcome` 枚举（经 `Converters` 存为 String）：
- `PASSED`：放行进入计时。
- `ABANDONED_DURATION` / `ABANDONED_INTENT` / `ABANDONED_ALTERNATIVE`：闸门前离开（abandoned）。

漏斗统计：`total = COUNT(*)`；`abandoned = COUNT(outcome ∈ ABANDONED_*)`；`passed = COUNT(outcome = 'PASSED')`。

### 3.4 focus_session（专注会话）

| 列 | 类型 | 约束 | 说明 |
|---|---|---|---|
| id | INTEGER | 主键自增 | — |
| day_key | TEXT | 非空 | 记账日 `YYYY-MM-DD` |
| started_at | INTEGER | 非空 | 开始时间戳 |
| ended_at | INTEGER | 可空 | 结束时间戳 |
| planned_minutes | INTEGER | 非空 | 承诺时长（25 / 10） |
| completed | INTEGER | 非空 | 是否完成（放弃 = 0） |

索引：`day_key`。

### 3.5 day_summary（每日汇总）

| 列 | 类型 | 约束 | 说明 |
|---|---|---|---|
| day_key | TEXT | 主键 | 记账日 `YYYY-MM-DD` |
| total_minutes | INTEGER | 非空 | 当日总使用分钟 |

索引：主键 `day_key`。`total_minutes` 由 S09/S10 在会话结束后聚合写入，供「今日环 / 7 天柱图」直接读。

---

## 4. 关键设计决策

1. **无 SQLite 外键约束**：`usage_session` / `gate_log` 的 `package_name` 与 `app_policy` 仅**逻辑关联**，不设 `@ForeignKey`。理由：移除受管应用后，历史会话/闸门日志应保留（审计/统计），外键约束会触发级联删除或阻塞删除。
2. **无 `Date` 字段**（R-81 / ST-04）：所有日期字段均为 `day_key TEXT`；时间戳一律 `Long`。`Converters` 仅处理 `GateOutcome` 枚举，不涉日期。
3. **day_summary 是聚合快照**：避免首页/统计页每次 `SUM(usage_session)`；其正确性由写入方（S09/S10）保证，读取方只读。

---

## 5. 索引汇总

| 表 | 索引 |
|---|---|
| app_policy | 主键 `package_name` |
| usage_session | `day_key`、`package_name` |
| gate_log | `day_key`、`package_name` |
| focus_session | `day_key` |
| day_summary | 主键 `day_key` |

---

## 6. 迁移规则

- 当前 `version = 1`，无历史迁移。
- 未来 schema 变更规则：
  1. 递增 `version`，新增 `Migration(from, to)`，在 `Room.databaseBuilder(...).addMigrations(...)` 注册。
  2. `exportSchema = true`，每次变更后 `app/schemas/<db>/<version>.json` 自动导出，**提交进仓库、勿手改**，供迁移校验与 `MigrationTestHelper` 使用。
  3. **禁止** destructive migration（`fallbackToDestructiveMigration`）——「删库重建」只作为「清空」语义（见 §7），不作为迁移手段。

---

## 7. 清空策略（R-82）

- 清空数据 = **删库重建**：`TimeGateDatabase.destroy(context)` 关闭连接 → `deleteDatabase("timegate.db")`（含 `-wal` / `-shm`）→ 下次访问重建空库。
- **不做**软删除、不做云备份、不做 CSV/JSON 导出（导出进 V1.1，见 R-83）。
- 仓库层统一经 `TimeGateRepository.clearAll()`；实现见 `RoomTimeGateRepository`。
- ⚠️ 注意：五表不含 UI 偏好（`theme` / `glass` / `haptic` / `onboarded` / 演示 `speed`）。这些由 **S07 / S10** 用 SharedPreferences 存储（简单 KV、非关系型），「清空回到引导页」需由 S10 在清空时同步重置 `onboarded` 等偏好。

---

## 8. 依赖方向与访问边界（R-80）

- `ui → domain → data`；`data` 不依赖 `ui` / `service`。
- UI 与服务**不直接碰 DAO**，一律经 `TimeGateRepository` 接口；接口可被 `FakeTimeGateRepository` 替换。
- `domain/time` 纯 Kotlin、零 `import android.*`；`data` 零 `import com.timegate.app.ui`。
- 零联网：Room 是纯本地库，不声明 `INTERNET`、不引入分析/崩溃/网络 SDK（R-02 / R-83）。

---

## 9. 与需求 / 测试映射

| 需求 | 测试用例 | 落在哪 |
|---|---|---|
| R-70 / R-71 | UT-01 | `DayKeyTest` |
| R-52 / R-54 | UT-02 | `RemainingTimeTest` |
| R-80 / R-82 | IT-01（S12） | 五表 + DAO + `clearAll` |
| R-81 | ST-04（S12） | Entity 无 `Date` 字段审计 |
| R-83 | ST-01（S12） | 无联网调用审计 |
