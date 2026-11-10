# 文件所有权 · TimeGate（S03）

> **版本** v1.0 · **状态** 已冻结（S03 交付） · **更新** 2026-11-09
> 一个路径只有一个主人。改别人文件前先写变更请求（`docs/handoff/`），由主人改，禁止「顺手重构」。
> 依据 `docs/SOP.md` §6 与 §4 工包所有权，与 `docs/ARCHITECTURE.md` §4 目录树一一对应。

---

## 1. Integrator（共享文件统一修改人）= S04

以下文件**只有 S04 能改**，其他工包确需改动时，先在 `docs/handoff/SXX.md` 写变更请求，交给 S04 改：

| 共享文件 | 备注 |
|---|---|
| `settings.gradle.kts` | 模块声明 |
| 根 `build.gradle.kts` | 插件/仓库 |
| `gradle.properties` | 全局属性 |
| `gradlew` / `gradlew.bat` | wrapper |
| `gradle/**`（含 `gradle/libs.versions.toml`、`wrapper/`） | 版本目录 |
| `app/build.gradle.kts` | S14 只加签名块，需 S04 复核 |
| `app/proguard-rules.pro` | 混淆规则 |
| `app/src/main/AndroidManifest.xml` | S07/S08/S09 加权限/组件需 S04 复核 |
| `app/src/main/java/com/timegate/app/TimeGateApp.kt` | Application 占位 |
| `app/src/main/java/com/timegate/app/MainActivity.kt` | launcher 占位 |
| `app/src/main/res/` 基础结构 | values/theme 占位；各 owner 按需追加资源需与 S04 对齐 |
| `.github/workflows/ci.yml` | S12 加门禁需 S04 复核 |
| `docs/BUILD.md` | 构建文档 |

---

## 2. 路径 → 工包 → 禁止事项

| 路径 glob | 主人 | 禁止事项 |
|---|---|---|
| `app/src/main/java/com/timegate/app/ui/theme/**` | S05 | 页面只消费主题，不改 token |
| `app/src/main/java/com/timegate/app/ui/components/**` | S05 | 页面只消费组件，不新造按钮 |
| `docs/DESIGN.md` | S05 | — |
| `app/src/main/java/com/timegate/app/data/**` | S06 | UI 不直接碰 DAO |
| `app/src/main/java/com/timegate/app/domain/time/**` | S06 | — |
| `app/schemas/**` | S06 | Room schema 导出，勿手改 |
| `docs/DATA.md` | S06 | — |
| `app/src/main/java/com/timegate/app/permission/**` | S07 | 检测层只读权限状态，不写 |
| `app/src/main/java/com/timegate/app/ui/onboarding/**` | S07 | — |
| `docs/PERMISSIONS.md` | S07 | — |
| `app/src/main/java/com/timegate/app/usage/**` | S08 | 闸门只订阅事件，不反向依赖 |
| `app/src/main/java/com/timegate/app/service/detection/**` | S08 | — |
| `docs/DETECTION.md` | S08 | — |
| `app/src/main/java/com/timegate/app/domain/gate/**` | S09 | 页面层不改状态机 |
| `app/src/main/java/com/timegate/app/ui/gate/**` | S09 | — |
| `app/src/main/java/com/timegate/app/ui/timeout/**` | S09 | — |
| `app/src/main/java/com/timegate/app/service/timer/**` | S09 | — |
| `docs/GATE.md` | S09 | — |
| `app/src/main/java/com/timegate/app/ui/nav/**` | S10 | — |
| `app/src/main/java/com/timegate/app/ui/today/**` | S10 | 只消费 S05/S06/S09 |
| `app/src/main/java/com/timegate/app/ui/apps/**` | S10 | 只消费 S05/S06/S09 |
| `app/src/main/java/com/timegate/app/ui/stats/**` | S10 | 只消费 S05/S06/S09 |
| `app/src/main/java/com/timegate/app/ui/focus/**` | S10 | 只消费 S05/S06/S09 |
| `app/src/main/java/com/timegate/app/ui/settings/**` | S10 | 只消费 S05/S06/S09 |
| `docs/UI-SPEC.md` | S10 | — |
| `docs/DEVICE-COMPAT.md`、`docs/QA-REPORT.md` | S11 | 定向修复需 owner 同意 |
| `app/src/test/**`、`app/src/androidTest/**` | 写测试的工包 | S12 负责测试计划、覆盖率、CI 门禁 |
| `docs/QA.md`、`docs/SECURITY-AUDIT.md` | S12 | — |
| `README.md`、`LICENSE`、`CHANGELOG.md`、`CONTRIBUTING.md`、`CODE_OF_CONDUCT.md`、`SECURITY.md`、`THIRD-PARTY.md` | S13 | S14 不改事实性内容 |
| `docs/PRIVACY.md`、`.github/ISSUE_TEMPLATE/**`、`.github/PULL_REQUEST_TEMPLATE.md`、`docs/assets/**` | S13 | — |
| `.github/workflows/release.yml`、`.github/workflows/pages.yml` | S14 | — |
| `docs/RELEASE.md` | S14 | — |
| `docs/DECISIONS.md` | S01 | S03 只追加 ADR-0005~0007，需 S01 复核；推翻旧 ADR 只新增不删除 |
| `docs/PROJECT.md`、`docs/REQUIREMENTS-FREEZE-v1.0.md` | S01（冻结） | 只读 |
| `docs/REQUIREMENTS.md`、`docs/TRACEABILITY.md`、`docs/TEST-PLAN.md` | S02（冻结） | 只读 |
| `docs/ARCHITECTURE.md`、`docs/OWNERSHIP.md` | S03 | 架构/所有权变更需重开 S03 级复核 |
| `.gitignore`、`.editorconfig` | S03 | 后续工包新增忽略规则提变更请求 |

---

## 3. demo/ 只读（冻结）

| 路径 | 状态 | 规则 |
|---|---|---|
| `demo/index.html` | 冻结 | 单文件原型，Pages 入口，**不再加功能** |
| `demo/README.md` | 冻结 | 标注「原型，不是最终 App」 |

- 原型只是 Android 版的设计参照物，**不参与构建**，不随 App 功能迭代。
- 改动 `demo/` 属于跨冻结边界，需项目负责人拍板；V1 内默认不改。

---

## 4. docs/archive/ 归档区（只读）

| 路径 | 内容 |
|---|---|
| `docs/archive/PLAN.md` | 过程稿（设计细节 + Android 要点），已归档 |
| `docs/archive/HANDOFF.md` | 交付包原始权威文档，已归档 |
| `docs/archive/prototype/` | 旧模块化 Web 原型（index.html + demo.html + css/ + js/），已归档 |

- 归档区**不参与构建、不参与 Pages、不进 CI**。
- 只作历史参考；内容已被 `docs/` 下的正式文档取代（需求 → `REQUIREMENTS.md`，Android 要点 → `ARCHITECTURE.md`/各 feature 文档）。
- 如需追溯旧决策，优先看 `docs/DECISIONS.md`，归档区仅作原始快照。

---

## 5. 变更请求模板（跨 ownership 必走）

在 `docs/handoff/SXX.md` 里加一段：

```markdown
## 变更请求（给 S04 / owner）

- 文件：app/src/main/AndroidManifest.xml
- 原因：闸门需要注册 GateActivity
- 拟改动：<activity android:name=".ui.gate.GateActivity" .../>
- 影响：无 INTERNET，无新增权限
```

由 owner 评审后改；未获批前不动手。
