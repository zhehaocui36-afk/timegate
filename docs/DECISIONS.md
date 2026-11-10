# 架构决策记录（ADR）

> 本文件集中存放 TimeGate 项目的**全部**架构决策（ADR）。
> 编号跨工包连续追加，详见文末「怎么改」。
>
> ADR-0001 ~ ADR-0004 已冻结（2026-11-09），值直接来自 `docs/SOP.md` §0.1，不再讨论。

---

## ADR-0001 · 包名与应用名

- **状态**：已接受（2026-11-09）
- **决定**：包名 `com.timegate.app`，应用名「闸」。
- **背景**：项目需要唯一、稳定的应用身份，用于 APK `applicationId`、仓库命名与对外识别。
- **影响**：仓库名固定为 `timegate`；`applicationId` 一经发布不可更改。

---

## ADR-0002 · minSdk

- **状态**：已接受（2026-11-09）
- **决定**：`minSdk` = 26（Android 8.0）。
- **背景**：在「覆盖广度」与「实现成本」之间取平衡。
- **影响**：覆盖 Android 8.0 及以上；全部代码需兼容 API 26（避免使用更高版本独占 API，或做版本分支）。

---

## ADR-0003 · 无障碍定位

- **状态**：已接受（2026-11-09）
- **决定**：V1 纳入无障碍，作为**可选增强**；用户不给无障碍时，只撤遮挡、不假装「回桌面」。
- **背景**：「到时退出回桌面」依赖无障碍权限，但无障碍不是核心能力，不应成为硬门槛。
- **影响**：「退出即回桌面」是可选能力，不是必需能力；无障碍自我限权（只读包名、不读屏幕文字）在权限与安全章节另行落实。

---

## ADR-0004 · 专注时长

- **状态**：已接受（2026-11-09）
- **决定**：V1 专注只做 **25 分钟**一档；到时页保留 **10 分钟**短专注入口。
- **背景**：控制 V1 范围，避免档位膨胀。
- **影响**：不做 45 / 60 分钟档；专注只有「25 分钟主流程 + 10 分钟短专注」两条路径。

---

## ADR-0005 · repo-layout（仓库布局与包边界）

- **状态**：已接受（2026-11-09，S03 追加）
- **决定**：单模块 Gradle 工程，用**包边界**（`ui` / `domain` / `data` / `service` + `permission` / `usage`）替代多模块。依赖方向写死：`ui → domain → data`；`service` 可依赖 `domain` 与 `data`；反向依赖一律禁止。
- **背景**：V1 只有四页 + 两个前台服务，多模块的构建复杂度大于收益；但仍需约束分层，避免 UI 直连数据库、服务反向依赖页面。
- **影响**：S04 建单模块工程；`domain/**` 纯 Kotlin 零 `import android`；`data/**` 不依赖 `ui`/`service`；依赖方向可被 grep 验证（见 `docs/ARCHITECTURE.md` §3.3）。

---

## ADR-0006 · detection-strategy（前台检测策略）

- **状态**：已接受（2026-11-09，S03 追加）
- **决定**：前台检测以 `UsageStatsManager.queryEvents` **2 秒轮询为主源**（授权门槛低），`AccessibilityService` 作**可选加速**（仅提前 0–200ms 拿到前台包名）；两路写同一 Repository 去重；**不读取屏幕内容**。
- **背景**：UsageStats 无需无障碍权限即可工作，是无障碍缺失时的唯一可靠源；无障碍能提前拿包名但不该成为硬门槛。
- **影响**：无障碍服务只订阅 `typeWindowStateChanged`，配置 `canRetrieveWindowContent="false"`，代码只取 `event.getPackageName()`，不调用 `getText()` / `getContentDescription()`（对应 R-30/R-31/R-24）。

---

## ADR-0007 · gate-activity（闸门用透明 Activity）

- **状态**：已接受（2026-11-09，S03 追加）
- **决定**：闸门三问用**透明主题的 `GateActivity`** 实现，**不用悬浮窗**；到用时全屏 `TimeoutActivity` 拦截。
- **背景**：三问需要输入文字，悬浮窗在输入法弹起时必崩；透明 Activity 是唯一能稳定承载文字输入的实现。Android 10+ 后台启动 Activity 受限，**持有 `SYSTEM_ALERT_WINDOW`（悬浮窗权限）时才获豁免**——因此悬浮窗权限是「能弹闸门」的 P0 前提，不只是提醒。
- **影响**：`GateActivity` 主题为透明；`SYSTEM_ALERT_WINDOW` 列为必需权限（R-20）；后台启动被拒时降级为高优通知 + 下次前台再弹（对应 R-46，详见 `docs/ARCHITECTURE.md` §6.3）。

---

## 怎么改

- 本文件由工包按 SOP 追加：S01 建文件并写 ADR-0001 ~ 0004；S03 追加 ADR-0005 repo-layout、ADR-0006 detection-strategy、ADR-0007 gate-activity。
- **只追加，不改已冻结的 ADR**；确需推翻时，新写一条 ADR 并注明「替代 ADR-xxxx」，不删原文。
- 追加 ADR-0005 起由 S03 执行，需 S01 复核（见 `docs/SOP.md` §6 所有权表）。
