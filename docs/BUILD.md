# 构建指南 · TimeGate（S04）

> 本文档让一个**陌生人**能只靠仓库里的文件，在一台干净机器上把「闸」构建出来。
> 版本 v1.0 · 2026-11-09 · 由 S04 建立，后续 Gradle / Manifest / CI 变更由 S04 维护。

---

## 1. 一句话

这是单模块 Android 工程（Kotlin + Jetpack Compose）。装上 JDK 17，`./gradlew assembleDebug` 就能出 Debug APK。**不需要预装 Gradle**（wrapper 自带），**不需要预装 Android Studio**（命令行即可）。

---

## 2. 前置要求

| 项 | 要求 | 备注 |
|---|---|---|
| JDK | **17**（Temurin/Amazon Corretto 均可） | 本地与 CI 一致，写死 |
| Android SDK | 需要 `platforms;android-37` + `build-tools;36.0.0` | AGP 会在缺失时**自动下载**，但首次需接受 license |
| Gradle | **不需要预装** | `gradle/wrapper/` 自带 9.6.0 |
| 网络 | 首次构建要下载依赖 | Google Maven + Maven Central |

### 2.1 装 JDK 17（Windows）

```bash
# 任选其一（以 Temurin 为例，winget）
winget install EclipseAdoptium.Temurin.17.JDK
```

装完确认：

```bash
java -version
# 期望输出类似：openjdk version "17.0.x" ...
```

### 2.2 Android SDK

- 若装了 Android Studio：SDK 默认在 `%LOCALAPPDATA%\Android\Sdk`，命令行构建时用环境变量 `ANDROID_HOME` 或项目根 `local.properties` 指向它。
- 若只装命令行工具：用 `sdkmanager` 装 `platforms;android-37`、`build-tools;36.0.0`，并 `sdkmanager --licenses` 接受全部 license。

> `local.properties` 只放本机 SDK 路径，**已被 `.gitignore` 忽略**，不要提交。示例：
> ```
> sdk.dir=C\:\\Users\\Feynman\\AppData\\Local\\Android\\Sdk
> ```

---

## 3. 工具链版本（写死，改版本走 S04）

| 项 | 值 |
|---|---|
| Gradle | 9.6.0（wrapper） |
| Android Gradle Plugin（AGP） | 9.5.0 |
| Kotlin | 2.4.21（AGP 9 内置 Kotlin，此值只喂 Compose 编译器插件） |
| Compose BOM | 2026.09.00 |
| compileSdk / targetSdk | 37（Android 16） |
| minSdk | 26（Android 8.0） |
| JDK | 17 |

版本目录统一在 `gradle/libs.versions.toml`。

---

## 4. 构建命令

```bash
# Windows（Git Bash / 命令行）：用 gradlew.bat
gradlew.bat clean assembleDebug

# Linux / macOS / WSL / Git Bash：用 ./gradlew
./gradlew clean assembleDebug
```

产出 Debug APK：

```
app/build/outputs/apk/debug/app-debug.apk
```

### 4.1 常用命令一览

| 命令 | 作用 |
|---|---|
| `./gradlew assembleDebug` | 打 Debug APK |
| `./gradlew assembleRelease` | 打 Release APK（S14 前无签名，不参与发布） |
| `./gradlew lintDebug` | 静态检查（Lint） |
| `./gradlew testDebugUnitTest` | 跑单元测试 |
| `./gradlew clean` | 清空构建产物 |

### 4.2 S04 验收命令（一次跑全）

```bash
# Windows
gradlew.bat clean assembleDebug lintDebug testDebugUnitTest

# Git Bash / Linux / macOS
./gradlew clean assembleDebug lintDebug testDebugUnitTest
```

---

## 5. 权限审计（零联网承诺）

本项目**不声明 `INTERNET`**（R-02 / R-110）。每次构建后可用 SDK 的 `aapt2` 检查合并后的 APK：

```bash
# 找到 aapt2（build-tools 目录下）
aapt2 dump permissions app/build/outputs/apk/debug/app-debug.apk
# 或老命令
aapt dump permissions app/build/outputs/apk/debug/app-debug.apk
```

**输出里不得出现 `android.permission.INTERNET`。** 这一步 CI 会在每次 PR 上自动执行（见 `.github/workflows/ci.yml`）。

---

## 6. CI 说明

两个工作流：

- `.github/workflows/ci.yml`：push 到 `main` / 任意 PR → JDK 17 → `assembleDebug lintDebug testDebugUnitTest` → `aapt2 dump permissions` 校验无 INTERNET → 上传 APK / Lint / 单测报告。
- `.github/workflows/gradle-wrapper-validation.yml`：校验 `gradle-wrapper.jar` 完整性（防供应链投毒）。

CI 与本地用**同一个 JDK 17**，避免「本地过、CI 挂」。

---

## 7. 常见报错

### 7.1 `SDK location not found`
AGP 找不到 Android SDK。解决：设 `ANDROID_HOME`，或建 `local.properties` 写 `sdk.dir`（见 §2.2）。

### 7.2 `Failed to apply plugin 'org.jetbrains.kotlin.android'` / `Cannot add extension with name 'kotlin'`
AGP 9 已经**内置 Kotlin**，不能再加 `org.jetbrains.kotlin.android` 插件。本项目只用 `com.android.application` + `org.jetbrains.kotlin.plugin.compose`。**不要**往回加 kotlin-android 插件。

### 7.3 `Inconsistent JVM-target compatibility (1.8 and 17)`
Java 与 Kotlin 的 target 不一致。本项目已在 `app/build.gradle.kts` 的 `compileOptions` 设为 17，且 AGP 9 内置 Kotlin 会自动把 `jvmTarget` 对齐到 17。若手改后复现，检查是否误删了 `compileOptions`。

### 7.4 Gradle 首次运行很慢 / 卡在下载
wrapper 首次要下载 Gradle 9.6.0（约 130MB）+ 全部依赖。走 `services.gradle.org` 与 Google Maven，属正常现象。国内网络慢可给 Gradle 配镜像（改 `init.d` 脚本，属本机行为，不进仓库）。

### 7.5 `license not accepted` / `Failed to install the following Android SDK packages`
AGP 自动下载 SDK 组件前必须接受 license。命令行装 SDK 时执行一次 `sdkmanager --licenses` 全选 `y`。

### 7.6 新版 cmdline-tools：包名从 `;` 改成 `/`，platform 带小版本
2026 年的 cmdline-tools（如 `16111833`）已把 `sdkmanager` 的包名格式从分号改成斜杠，且 platform 带小版本号：旧格式 `platforms;android-37` 会报 `Package platforms not found`，要用 `platforms/android-37.2`、`build-tools/36.0.0`。platform 37 只有 `android-37.0` / `37.1` / `37.2`，没有纯 `android-37`。

---

## 8. 目录结构（构建相关）

```
timegate/
├─ settings.gradle.kts            # 模块声明（单模块 :app）
├─ build.gradle.kts               # 顶层：只声明插件
├─ gradle.properties              # JVM 参数 + AndroidX 开关
├─ gradle/
│  ├─ wrapper/                    # wrapper 三件套 + properties
│  └─ libs.versions.toml          # 版本目录（唯一改版本处）
├─ app/
│  ├─ build.gradle.kts            # 模块配置（SDK、依赖、buildTypes）
│  ├─ proguard-rules.pro          # 混淆规则（V1 未开）
│  └─ src/main/
│     ├─ AndroidManifest.xml      # 无 INTERNET、单 launcher、allowBackup=false
│     └─ java/com/timegate/app/   # 包根（占位：TimeGateApp / MainActivity）
└─ .github/workflows/             # ci.yml + wrapper validation
```

---

## 9. 下一步（S05/S06 起）

新工包加依赖时：**先到 `gradle/libs.versions.toml` 加版本，再到 `app/build.gradle.kts` 引用**，不要硬编码版本号。改 Manifest（加权限/组件）一律先向 S04 写变更请求（见 `docs/OWNERSHIP.md` §5）。

---

## 10. 本机开发环境（S04 已配好，2026-11-10）

本机已装好可直接构建的环境（均放 D 盘，不占 C 盘），后续工包直接复用：

| 项 | 路径 |
|---|---|
| JDK 17 | `D:\WorkBuddyData\jdk17`（Temurin 17.0.21） |
| Android SDK | `D:\WorkBuddyData\android-sdk`（`platforms/android-37.2` + `build-tools/36.0.0`） |
| Gradle 缓存 | `D:\WorkBuddyData\gradle-home`（`GRADLE_USER_HOME`，避免 C 盘 `.gradle` 膨胀） |

本地构建（Git Bash）一次带齐环境变量：

```bash
cd timegate
export JAVA_HOME="D:/WorkBuddyData/jdk17"
export GRADLE_USER_HOME="D:/WorkBuddyData/gradle-home"
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew clean assembleDebug lintDebug testDebugUnitTest
```

`local.properties`（`sdk.dir=D:/WorkBuddyData/android-sdk`）已写好，被 `.gitignore` 忽略、不提交；CI 上用 ubuntu runner 预装 SDK + AGP 自动补装，与本地路径无关。
