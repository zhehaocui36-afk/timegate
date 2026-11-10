# 架构 · TimeGate（S03）

> **版本** v1.0 · **状态** 已冻结（S03 交付） · **更新** 2026-11-09
> 本文是 P1「工程底座」的架构依据，S04 起各工包按本文建包、按 `docs/OWNERSHIP.md` 改文件。
> 需求口径见 `docs/REQUIREMENTS.md`；决策依据见 `docs/DECISIONS.md`。

---

## 1. 一句话架构

**单模块** Android 工程（Kotlin + Jetpack Compose），包根 `com.timegate.app`；用**包边界**（`ui` / `domain` / `data` / `service` + `permission` / `usage`）约束依赖方向，**不拆多模块**。

拆包不是为了炫技，是为了三件事：依赖方向单向可验证、每个工包只碰自己的目录、新 agent 只看目录树就知道代码往哪放。

---

## 2. 设计原则（写死）

| # | 原则 | 依据 |
|---|---|---|
| 1 | **单模块 + 包边界**，不用多模块 Gradle | ADR-0005；V1 只有四页 + 两个服务，多模块复杂度大于收益 |
| 2 | 依赖方向 **`ui → domain → data`**；`service` 可依赖 `domain` 与 `data`；反向依赖一律禁止 | ADR-0005 |
| 3 | **闸门用透明 `GateActivity`，不用悬浮窗** | ADR-0007；三问要输入文字，悬浮窗在输入法弹起时必崩（R-46） |
| 4 | 前台检测 **UsageStats 2 秒轮询为主 + 无障碍可选加速**，不读屏幕内容 | ADR-0006；R-30/R-31/R-24 |
| 5 | `demo/` 是**只读原型**，不再加功能 | `docs/OWNERSHIP.md`；SOP §6 |
| 6 | 数据**只存时间戳不存剩余秒**，日期统一 `day_key TEXT` | R-52/R-81；`domain/time` 负责口径 |
| 7 | 零 `INTERNET`、无 WebView、无账号、无埋点 | R-02/R-110 |

---

## 3. 包边界与依赖方向

### 3.1 分层

```
com.timegate.app
├─ ui/           表现层（Compose 页面与组件）
├─ domain/       领域层（纯 Kotlin，无 Android 依赖）：闸门状态机、时间口径
├─ data/         数据层（Room + Repository）：本地存储与仓库接口
├─ service/      系统服务（前台服务）：检测、计时
├─ permission/   权限与能力探测（横切，供引导与各层读状态）
└─ usage/        使用统计与前台检测源（横切，供 service 消费）
```

### 3.2 依赖方向（唯一合法方向）

```text
        ┌──────────────────────────────────┐
        │             ui（表现层）           │
        └───────────────┬──────────────────┘
                        ▼
        ┌──────────────────────────────────┐
        │          domain（领域层）          │
        └───────────────┬──────────────────┘
                        ▼
        ┌──────────────────────────────────┐
        │           data（数据层）           │
        └──────────────────────────────────┘

service ──► domain、data          （不得依赖 ui）
permission ──► domain、data        （不得依赖 ui / service）
usage ──► domain、data             （不得依赖 ui / service）
```

**硬规则**：

- `domain` 不 `import android.*`，不依赖任何其他层。
- `data` 不依赖 `ui` / `service`。
- `ui` 不直接碰 DAO，只通过 `data/repo` 的仓库接口；仓库接口可被 Fake 替换。
- `service` / `permission` / `usage` 不依赖 `ui`（事件用 StateFlow / 回调向下传递，不反向 import）。
- 任何反向依赖在代码评审里直接打回。

### 3.3 依赖方向怎么验证

- `domain/**` 源码中 `import android` 命中数为 0。
- `data/**` 源码中 `import com.timegate.app.ui` 命中数为 0。
- `service/**`、`permission/**`、`usage/**` 源码中 `import com.timegate.app.ui` 命中数为 0。

---

## 4. 目录树（完整，含 owner）

仓库根即 Gradle 工程根。下面是 S03 已冻结的骨架（`.` 表示 S03 已建的包，`.gitkeep` 仅为占位，S04 建工程后各包一旦有 `.kt` 即删 `.gitkeep`）。

```text
timegate/
├─ app/                                    # Android 主模块（单模块）
│  ├─ build.gradle.kts                     # [S04]
│  ├─ proguard-rules.pro                   # [S04]
│  └─ src/
│     ├─ main/
│     │  ├─ AndroidManifest.xml            # [S04] 无 INTERNET；S07/08/09 加权限需 S04 复核
│     │  ├─ java/com/timegate/app/
│     │  │  ├─ TimeGateApp.kt              # [S04] Application 占位
│     │  │  ├─ MainActivity.kt             # [S04] launcher Activity 占位
│     │  │  ├─ ui/theme/                   # [S05] Color/Type/Shape/Theme/Motion
│     │  │  ├─ ui/components/              # [S05] 可复用组件，页面只消费不新造
│     │  │  ├─ ui/nav/                     # [S10] 四 Tab 外壳与悬浮胶囊导航
│     │  │  ├─ ui/onboarding/              # [S07] 教程 3 屏 / 权限页 / 选应用页
│     │  │  ├─ ui/gate/                    # [S09] 闸门三问（时长/意图/替代）+ 倒计时页
│     │  │  ├─ ui/timeout/                 # [S09] 时间到页（三出口）
│     │  │  ├─ ui/today/                   # [S10] 今日页（构成环）
│     │  │  ├─ ui/apps/                    # [S10] 应用页（受管列表）
│     │  │  ├─ ui/stats/                   # [S10] 统计页
│     │  │  ├─ ui/focus/                   # [S10] 专注页
│     │  │  ├─ ui/settings/                # [S10] 设置页
│     │  │  ├─ domain/gate/                # [S09] 闸门状态机 + 归约器（纯 Kotlin）
│     │  │  ├─ domain/time/                # [S06] DayKey / Clock 抽象（4:00 换日口径）
│     │  │  ├─ data/local/                 # [S06] Room Entity/DAO/Database/TypeConverter
│     │  │  ├─ data/repo/                  # [S06] 仓库接口 + 本地实现 + Fake
│     │  │  ├─ permission/                 # [S07] PermissionId/Checker/SettingsIntent/归约器
│     │  │  ├─ usage/                      # [S08] UsageStatsSource/ForegroundDetector/AccessibilitySource
│     │  │  ├─ service/detection/          # [S08] 检测服务 + 前台通知
│     │  │  └─ service/timer/              # [S09] 计时前台服务 + 常驻通知
│     │  └─ res/                           # [S04 建基础] values/theme 占位；各 owner 按需追加资源
│     ├─ test/                             # [写测试的工包] 单元测试，S12 统一门禁
│     └─ androidTest/                      # [写测试的工包] 仪器测试，S12 统一门禁
├─ demo/                                   # 冻结（只读原型 + Pages 源）
│  ├─ index.html                           # 单文件原型，双击可开
│  └─ README.md                            # 标注「原型，不是最终 App」
├─ docs/
│  ├─ ARCHITECTURE.md                      # [S03] 本文件
│  ├─ OWNERSHIP.md                         # [S03] 路径→工包→禁止事项
│  ├─ DECISIONS.md                         # [S01+S03] 全部 ADR
│  ├─ REQUIREMENTS.md / TRACEABILITY.md / TEST-PLAN.md   # [S02 冻结]
│  ├─ PROJECT.md / REQUIREMENTS-FREEZE-v1.0.md           # [S01 冻结]
│  ├─ BUILD.md                             # [S04] 待建
│  ├─ DATA.md / DESIGN.md / PERMISSIONS.md / DETECTION.md / GATE.md / UI-SPEC.md ...  # 后续工包
│  ├─ handoff/                             # 每包交接
│  └─ archive/                             # PLAN.md、HANDOFF.md、旧模块化原型
├─ gradle/                                 # [S04] wrapper + libs.versions.toml
├─ .github/workflows/                      # [S04 ci.yml] [S14 release.yml/pages.yml]
├─ settings.gradle.kts / build.gradle.kts / gradle.properties / gradlew*  # [S04]
├─ README.md / LICENSE / CHANGELOG.md / 社区文件                               # [S13]
├─ .gitignore / .editorconfig              # [S03]
```

---

## 5. 核心数据流：检测 → 闸门 → 计时 → 到时

```text
┌──────────────────────────── 前台检测 ────────────────────────────┐
│  UsageStatsSource  (R-30 主源，queryEvents 2 秒轮询)  ──┐        │
│  AccessibilitySource (R-31 可选加速，typeWindowStateChanged) ─┤   │
│                                                            ▼        │
│               ForegroundDetector（去重 R-32 / 忽略自身与桌面 R-34） │
└───────────────────────────────────────────────────────────────────┘
                        │ StateFlow<ForegroundEvent>
                        ▼
            DetectionService（前台服务，常驻通知）
                        │ 命中受管应用 → 触发开闸
                        ▼
┌──────────────────────────── 闸门 ───────────────────────────────┐
│  GateActivity（透明主题，R-46）                                    │
│   GateStateMachine：IDLE → DURATION → INTENT → ALTERNATIVE      │
│     ├─ DURATION  选时长 5/15/自定义 1–120（R-41）                │
│     ├─ INTENT    意图 ≥2 字（R-42）                              │
│     ├─ ALTERNATIVE  我先去做 / 还是想打开（R-43）                │
│     └─ 每步「直接退出」→ IDLE，记 abandoned（R-44）               │
│                  │ 放行（IN_USE）                                 │
└──────────────────────────────────────────────────────────────────┘
                        ▼
            TimerService（前台服务，计时）
              只存开始/离开/结束时间戳（R-52），剩余现算
              剩 1 分钟预警（R-51）
                        │ 归零（TIMEOUT）
                        ▼
            TimeoutActivity（全屏「时间到」页，R-60）
              ├─ 退出    → 有障碍：GLOBAL_ACTION_HOME 回桌面（R-61/R-111）
              │           无障碍：只撤遮挡（R-22/R-61）
              ├─ 再继续  → 回 DURATION 重选时长（R-62）
              └─ 转去专注 → 10 分钟短专注（R-63）
```

**写入口径**：所有闸门/计时结果经 `data/repo` 仓库接口落库（`gate_log` / `usage_session` / `focus_session`），UI 不直接碰 DAO；`usage` 与 `service` 只写事件、不碰 UI。

---

## 6. 服务与 Activity 生命周期

### 6.1 两个前台服务

| 服务 | 包 | 职责 | 生命周期要点 |
|---|---|---|---|
| `DetectionService` | `service/detection` | 持续轮询前台包名、触发开闸 | `START_STICKY`；`foregroundServiceType="specialUse"` + `PROPERTY_SPECIAL_USE_FGS_SUBTYPE`（R-56）；常驻通知 |
| `TimerService` | `service/timer` | 计时、1 分钟预警、到时触发 | 同上；只存时间戳，强停后系统重启（R-55）；息屏按离开时刻补算（R-54） |

**Android 14+ 硬约束**（R-56）：两个服务都必须声明 `specialUse` 类型与 subtype，否则启动抛 `MissingForegroundServiceTypeException`。

**强停自起**（R-55）：`START_STICKY`，被系统/用户强停后由系统尝试重启；开机自启依赖 `RECEIVE_BOOT_COMPLETED`（可选，R-21）。

### 6.2 三个 Activity

| Activity | 主题 | 职责 | 生命周期要点 |
|---|---|---|---|
| `MainActivity` | 正常 | launcher，四 Tab 宿主 | 仅 S04 占位，S10 接导航 |
| `GateActivity` | **透明** | 闸门三问 | 后台启动 Activity 受限（Android 10+），**持有 `SYSTEM_ALERT_WINDOW` 才豁免**——悬浮窗权限是「能弹闸门」的前提，不只是提醒 |
| `TimeoutActivity` | 全屏 | 时间到页 | 由前台服务触发；无 `USE_FULL_SCREEN_INTENT`（Android 14 限制），失败降级为高优通知 |

### 6.3 后台启动 Activity 的关键约束

- Android 10+ 禁止后台应用随意 `startActivity`；**仅有「悬浮窗」权限（`SYSTEM_ALERT_WINDOW`）的应用可获得豁免**。
- 因此 `SYSTEM_ALERT_WINDOW` 在本项目是 P0 必需权限（R-20），不是可选提醒——它直接决定闸门能不能弹出来（HANDOFF G.6 / P6）。
- 即使持有权限，某些 ROM（ColorOS）仍会拦截，需厂商白名单（S11 处理）；失败时降级为高优通知 + 下次前台再弹。

---

## 7. ADR 索引

| 编号 | 主题 | 一句话 |
|---|---|---|
| ADR-0001 | 包名/应用名 | `com.timegate.app` /「闸」 |
| ADR-0002 | minSdk | 26（Android 8.0） |
| ADR-0003 | 无障碍定位 | 可选增强；不给只撤遮挡 |
| ADR-0004 | 专注时长 | 25 分钟 + 10 分钟短专注 |
| **ADR-0005** | **repo-layout** | 单模块 + 包边界，依赖方向写死 |
| **ADR-0006** | **detection-strategy** | UsageStats 为主 + 无障碍可选加速，不读屏 |
| **ADR-0007** | **gate-activity** | 透明 Activity，不用悬浮窗 |

---

## 8. 新 agent 建同构工程的落地清单

> 目标：一个新 agent 只看本文 + `docs/OWNERSHIP.md`，就能建出与本文一致的工程，不需要问人。

1. 包根固定 `com.timegate.app`，目录前缀 `app/src/main/java/com/timegate/app/`。
2. 按 §4 目录树建 19 个叶子包（`ui/` 11 个、`domain/` 2 个、`data/` 2 个、`permission/` 1 个、`usage/` 1 个、`service/` 2 个），空包放 `.gitkeep`。
3. 依赖方向按 §3.2：`ui → domain → data`；`service` 可依赖 `domain`/`data`；反向禁止。
4. `domain/**` 纯 Kotlin、零 `import android`。
5. 数据落库一律走 `data/repo` 仓库接口，UI/服务不直接碰 DAO。
6. 闸门 = 透明 `GateActivity`（不新建悬浮窗）；到时 = 全屏 `TimeoutActivity`。
7. 两个前台服务都声明 `specialUse` + subtype。
8. Manifest 由 S04 统一维护，任何权限/组件变更先写变更请求给 S04。
9. `demo/` 只读，改原型前先看 `docs/OWNERSHIP.md`。

---

## 9. 边界与禁止

- **V1 不做**：每日总预算、21:30 复盘推送、CSV/JSON 导出、多设备同步、好友 PK、45/60 分钟专注档、iOS、WebView、任何联网能力。
- **不拆多模块**：再多人提也不拆；包边界已足够约束。
- **不把三问做成可关闭开关**：可关 = 机制失效（HANDOFF J）。
- **配色不改**：`#5B6CFF` 已三轮否决后拍板，不要改回绿/金（HANDOFF J）。
