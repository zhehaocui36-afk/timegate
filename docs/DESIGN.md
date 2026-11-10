# 设计系统 · 闸 / TimeGate（S05）

> **版本** v1.0 · **状态** 完成 · **更新** 2026-11-10 · **owner** S05
> **唯一视觉权威**：`demo/index.html` 内嵌 CSS（冻结只读，不参与构建）
> **本文用途**：S07 / S09 / S10 消费组件库的唯一入口。页面层**只消费，不新造按钮**，也不改 token。

---

## 0. 一句话

把 demo 深/浅两套 CSS 变量翻译成一套 Compose 主题（5 个文件）+ 10 个自绘组件，保证**深/浅色切换、字号放大 200%、减弱动效**三种情况下都不崩。

---

## 1. 硬规则（写死）

| # | 规则 |
|---|---|
| 1 | 主 tint = **`#5B6CFF`**，三轮否决后拍板（`ARCHITECTURE.md` §9、`HANDOFF.md` J），**不得改回绿/金** |
| 2 | 除 `ui/theme/**` 外，任何组件**不得硬编码颜色**；一律走 `MaterialTheme.colorScheme` 或 `TimeGateTheme.colors` |
| 3 | **tint 只允许出现在 5 处**（§4 写死），第 6 处一律不允许 |
| 4 | 组件基于 **Compose Foundation 自绘**，只把 token 当数据消费，**不套用 Material3 组件的默认外观** |
| 5 | 触控目标 **≥ 48dp**；正文对比度 **≥ 4.5:1**；字号 **200% 不截断** |
| 6 | 减弱动效下**不得有位移/缩放动画** |
| 7 | 玻璃用**半透明 + 顶光**模拟，Android / Compose 无 `backdrop-filter` 等价物，不追 `Modifier.blur` |
| 8 | 每个组件带深 / 浅两组 `@Preview`，另有一份汇总预览清单 |

---

## 2. 交付文件

| 路径 | 内容 |
|---|---|
| `ui/theme/Color.kt` | 两套 `TimeGateColors` + 两套 Material3 `ColorScheme` + `LocalTimeGateColors` |
| `ui/theme/Type.kt` | `TimeGateType` 字号阶梯 + `TimeGateTypography` |
| `ui/theme/Shape.kt` | `TimeGateShapeTokens` + `TimeGateShapes` |
| `ui/theme/Theme.kt` | `TimeGateTheme` 入口 + `TimeGateTheme.*` 访问器 + 系统减弱动效探测 |
| `ui/theme/Motion.kt` | `TimeGateMotion`（时长、spring、pressScale、`allowDisplacement`） |
| `ui/components/*.kt` | 10 个组件，各带深/浅 `@Preview` |
| `ui/components/ComponentGallery.kt` | **预览入口，不是组件**：汇总 10 项 + 4 组预览（深/浅/200%/减弱动效） |
| `docs/handoff/S05.md` | 交接（G0 门禁） |

`ui/theme/.gitkeep`、`ui/components/.gitkeep` 已删除。

---

## 3. CSS 变量 → Compose token 对照表

读法：`TimeGateTheme.colors.xxx` 简写为 `c.xxx`；`MaterialTheme.colorScheme.xxx` 简写为 `cs.xxx`。
**所有十六进制色值只出现在 `Color.kt`**，其余文件通过下表取用。

### 3.1 基础面与线条

| CSS 变量 | 深色 | 浅色 | Compose token | MaterialTheme 槽位 |
|---|---|---|---|---|
| `--bg` | `#000000` | `#F2F2F7` | `c.background` | `cs.background` |
| `--surface` | `#1C1C1E` | `#FFFFFF` | `c.surface` | `cs.surface` |
| `--surface-2` | `#2C2C2E` | `#E9E9EE` | `c.surface2` | `cs.surfaceVariant` |
| `--surface-3` | `#3A3A3C` | `#D6D6DC` | `c.surface3` | `cs.secondary` |
| `--line` | `rgba(84,84,88,.62)` | `rgba(60,60,67,.26)` | `c.line` | `cs.outlineVariant` |
| `--line-strong` | `rgba(84,84,88,.95)` | `rgba(60,60,67,.42)` | `c.lineStrong` | `cs.outline` |

### 3.2 文字

| CSS 变量 | 深色 | 浅色 | Compose token | MaterialTheme 槽位 |
|---|---|---|---|---|
| `--text-1` | `#FFFFFF` | `#000000` | `c.textPrimary` | `cs.onSurface` / `cs.onBackground` |
| `--text-2` | `rgba(235,235,245,.60)` | `rgba(60,60,67,.60)` | `c.textSecondary` | — |
| `--text-3` | `rgba(235,235,245,.34)` | `rgba(60,60,67,.34)` | `c.textTertiary` | — |
| `--tab-idle` | `rgba(235,235,245,.50)` | `rgba(60,60,67,.52)` | `c.tabIdle` | — |

> ⚠️ `cs.onSurfaceVariant` 刻意映射到 **`c.textPrimary` 而不是 `textSecondary`**：
> 浅色 `textSecondary` 仅 3.44:1，不能作为正文（见 §5）。

### 3.3 强调色与功能色

| CSS 变量 | 深色 | 浅色 | Compose token | MaterialTheme 槽位 |
|---|---|---|---|---|
| `--tint`（填充/基础） | `#4E5CEC` | `#4655DB` | `c.tint` | `cs.primaryContainer` |
| `--tint-on`（强调/前景，**主 tint**） | **`#5B6CFF`** | `#3541C9` | `c.tintOn` | `cs.primary` |
| `--tint-soft` | `rgba(91,108,255,.24)` | `rgba(70,85,219,.14)` | `c.tintSoft` | — |
| `--tint-ink` | `#FFFFFF` | `#FFFFFF` | `c.tintInk` | `cs.onPrimary` |
| `--danger` | `#FF453A` | `#FF3B30` | `c.danger` | `cs.error` |
| `--warn` | `#FF9F0A` | `#C77700` | `c.warn` | `cs.tertiary` |
| `--ring-track` | `rgba(120,120,128,.32)` | `rgba(120,120,128,.20)` | `c.ringTrack` | — |

### 3.4 按钮

| CSS 变量 / 规则 | 深色 | 浅色 | Compose token |
|---|---|---|---|
| `--btn-solid` | `#FFFFFF` | `#000000` | `c.btnSolid` |
| `--btn-solid-ink` | `#000000` | `#FFFFFF` | `c.btnSolidInk` |
| `--btn-solid-hi` | `rgba(255,255,255,.55)` | `rgba(255,255,255,.20)` | `c.btnSolidHi` |
| `.btn--primary` 渐变顶 / 底 | `#FFFFFF → #E9E9EE` | `#2C2C2E → #000000` | `c.btnSolidGradientTop` / `c.btnSolidGradientEnd` |
| `.btn:disabled { opacity:.3 }` | — | — | `c.disabledAlpha = 0.30f` |

### 3.5 玻璃 / 遮罩 / 拇指 / 阴影

| demo 来源 | Compose token | 说明 |
|---|---|---|
| `color-mix(var(--surface), --glass-alpha)` | `c.glassTop` / `c.glassBottom` | 半透明渐变面（顶亮底暗） |
| `inset 0 1px 0 rgba(255,255,255,.26)` | `c.glassTopLight` | 顶光 1dp 线（左强右弱） |
| `inset 0 -1px 0 rgba(0,0,0,.55)` | `c.glassBottomShade` | 底部暗边＝厚度 |
| `0 0 0 .5px rgba(255,255,255,.13)` | `c.glassRim` | 细描边 |
| `.tabbar__ind::before` | `c.indicatorFill` | 内层选中胶囊（深：白 13%，浅：白实色） |
| `.overlay--sheet rgba(0,0,0,.46)` | `c.scrim` | 弹窗遮罩（深/浅同值） |
| `.switch i::after` / `.range::-webkit-slider-thumb` | `c.thumb` | 拇指白色 |
| `box-shadow` 落影 | `c.shadow` | 半透明黑 |

> **未复制的两个 CSS 变量**：`--glass-blur: 30px`、`backdrop-filter: blur(...)`。
> Compose 没有背景模糊的等价物，按 S05 约束用半透明 + 顶光代替（§8）。
> `--glass-mix` / `--glass-alpha` 由 S10 设置页的「玻璃通透度」动态控制，S05 只提供静态基线。

### 3.6 圆角 / 字号 / 动效 / 间距

| 类别 | demo | Compose token |
|---|---|---|
| 圆角 | `--r-xs 8 / --r-sm 10 / --r-md 12 / --r-lg 16 / --r-xl 22 / --r-full 999` | `shapes.xs/sm/md/lg/xl/full`；`full` 用 `CircleShape` 表达 |
| 弹窗顶部 | `border-radius: 28px 28px 0 0` | `shapes.sheetTop` |
| 导航胶囊 | `calc(var(--bar-h)/2)` = 50% | `shapes.bar` |
| 字号 | `30 / 24 / 22 / 17 / 16 / 15 / 13 / 12 / 11 / 10` | `type.displayLarge … type.label`（全部 `sp`） |
| 数字专用 | `78 / 66 / 40 / 32 / 21` | `type.numHero/numFocus/numRing/numBig/numDuration` |
| 动效时长 | `--d1 .12s / --d2 .2s / --d3 .38s / --d4 .6s` | `motion.durationShort/Medium/Long/Extra` = `120/200/380/600` ms |
| 缓动 | `--ease-spring cubic-bezier(.34,1.4,.64,1)` | `motion.moveSpec`（`spring(0.85, 300)`） |
| 按压 | `.btn:active { scale(.972) }` | `motion.pressScale = 0.972f` |
| 导航高度 | `--bar-h 64px`（compact 56） | `TgTabBar(compact = …)` |

**间距规范**（组件内已写死，页面层沿用）：

| 场景 | 值 |
|---|---|
| 页面水平边距 | 20dp |
| 卡片内边距 | 15dp |
| 卡片间距 | 10dp |
| 列表行内边距 | 14dp 水平 / 13dp 垂直 |
| 组件间距 | 8–10dp（`Arrangement.spacedBy`） |
| TabBar 外边距 | 16dp 水平 / 10dp 垂直 |
| Sheet 内边距 | 20dp 水平 / 8dp 顶 / 24dp 底 |
| 抓手 | 36×5dp |
| 最小触控目标 | **48dp** |

---

## 4. tint 只允许出现的 5 处（写死）

依据 `docs/archive/HANDOFF.md` F.1「tint 只出现在 5 处」+ `demo/index.html` 里 `var(--tint*)` 的实际用法收敛。**这 5 处之外，一律黑白灰。**

| # | 语义位置 | demo CSS 依据 | 组件库落点 | token |
|---|---|---|---|---|
| 1 | **Tab 选中** | `.tab.is-active { color: var(--tint-on) }`；`.ob__dots i.on` / `.ov-dots i.on` | `TgTabBar` 选中项图标 + 文字 | `c.tintOn` |
| 2 | **开关打开** | `.switch input:checked + i { background: var(--tint) }` | `TgSwitch` checked 轨道 | `c.tint` |
| 3 | **环形分段** | JS `RAMP`（自 `#5B6CFF` 起）绘制 `#donut` 构成环 | `TgRing` 各段弧 | `c.tint → c.tintOn` 渐变；S10 可经 `TgRingSegment.color` 传 RAMP |
| 4 | **图表 / 数据条** | `.chart__bar`、`.rank-bar>i`、`.funnel__track>i`、`.app-bar>i`、`.funnel__big` | `TgSlider` 已完成轨道、`TgListRow` 进度条；柱图 / 排行 / 漏斗由 S10 消费同一 token | `c.tint → c.tintOn` 渐变 |
| 5 | **可点文字与选中态** | `.chip.is-on`、`.dur__item.is-on`、`.pick.on .pick__ck`、`.q-title em`、`.reclaim__t b`、`.textlink`；`:focus-visible` 的 `--tint-soft`、`field:focus-within` 的 `--tint` | `TgChip` 选中态、`TgButton` 焦点外环 | `c.tint` / `c.tintOn` / `c.tintSoft` |

**附属于第 5 处、不另计第 6 处**：`--tint-soft` 只用于 `:focus-visible` 焦点外环；`--tint-ink` 只作为 `c.tint` 上的前景字。

**明确禁止（容易被顺手加上的）**：

- 主按钮背景 / 文字（走 `btnSolid` / `btnSolidInk`，白黑反色）
- 卡片、列表行、Sheet、玻璃、遮罩、抓手、描边、图标方块
- 引导教程页、空状态、Toast、页脚
- `TgListRow` 的 `leadingText` 图标方块：**demo `.app-ico` 用了 tint 渐变，S05 收敛为中性 `surface3`**（见 §12 取舍记录）
- 任何「为了让页面看起来更活泼」的额外 tint

---

## 5. 对比度审计（WCAG 2.1 相对亮度公式，逐项实测）

半透明色先按其 alpha 合成到对应底色再计算。

| 前景 / 背景 | 深色 | 浅色 | 可否作正文 |
|---|---|---|---|
| `textPrimary` / `background` | **21.00:1** | **18.82:1** | ✅ |
| `textPrimary` / `surface` | **17.01:1** | **21.00:1** | ✅ |
| `btnSolidInk` / `btnSolid` | **21.00:1** | **21.00:1** | ✅ |
| `tintInk`(白) / `tint` | **5.16:1** | **5.86:1** | ✅（选中态 Chip） |
| `tintOn` / `background` | 5.03:1 | 6.84:1 | ✅（Tab 选中、全屏页强调文字） |
| `textSecondary` / `surface` | 5.94:1 | **3.44:1** | ⚠️ 深色可、浅色不可 |
| `tintOn` / `surface` | **4.08:1** | 7.63:1 | ⚠️ 深色不可在卡片内用 tintOn 作文字 |
| `danger` / `surface` | 4.99:1 | **3.55:1** | ❌ 仅描边/图标/短状态标签 |
| `warn` / `surface` | 8.28:1 | **3.46:1** | ❌ 仅描边/图标/短状态标签 |
| `textTertiary` / `surface` | 2.81:1 | 1.88:1 | ❌ 仅装饰 / 禁用态 |

### S05 的执行规则（保证「正文 ≥ 4.5:1」）

1. **正文定义**：承载句子、标题、数值、按钮文案的文本。
2. 组件库里**所有 `Text`** 只用 `c.textPrimary`、`c.btnSolidInk`、`c.tintInk` 三种前景色 —— 最低 5.16:1，**没有任何一处正文低于 4.5:1**。
3. `c.textSecondary` / `c.textTertiary` / `c.danger` / `c.warn` 只用于描边、图标、分隔线与非必要装饰；`danger` 语义由**描边颜色**传达，危险按钮的文字仍走 `textPrimary`。
4. `c.tintOn` 作文字只允许在 `background` 上（TabBar 悬浮胶囊、全屏页）；卡片内强调一律 `textPrimary`。
5. 页面层（S07/S09/S10）若确需用低对比色，仅可用于 ≤12sp 的装饰标签，**不得承载信息**。

---

## 6. 字号放大 200% 不截断的实现规则

系统字号由 `LocalDensity.fontScale` 提供，`TimeGateTheme` **不覆盖它**（覆盖会让 App 与系统设置脱节）。

| 做法 | 说明 |
|---|---|
| 全部字号与行高用 `sp` | 随 `fontScale` 等比放大 |
| 文字容器用 `heightIn(min)` / `wrapContentHeight` | **禁止固定 `height()`**；按钮最小 48/50dp，内容可撑高 |
| 不设 `maxLines = 1`、不用 `TextOverflow.Ellipsis` | 长文案自动换行，不出现省略号截断 |
| `Column(Modifier.weight(1f).wrapContentHeight())` | 列表行主区随内容增高 |
| `TgSheet` 内容 `verticalScroll` + `heightIn(max = 640.dp)` | 大字号时面板可滚动 |
| `TgTabBar` 胶囊与每个 Tab 都用 `heightIn` | 标签换行后撑高，不裁切 |
| `TgRing` 中心文字 `wrapContentSize(unbounded = true)` | 允许溢出圆环，但**不被裁切**（无 `clip`、无 `Ellipsis`） |
| Preview `@Preview(fontScale = 2f)` | `Gallery · 字号 200%` 可直接走查 |

---

## 7. 减弱动效（Android 上的 `prefers-reduced-motion` 等价）

Android 无 CSS 媒体查询，等价信号取 **`Settings.Global.ANIMATOR_DURATION_SCALE == 0`**
（开发者选项「动画时长缩放 → 关闭动画」，部分 ROM 的「移除动画」无障碍开关也写这个值）。
`TimeGateTheme` 默认读取；设置页 / 预览可用 `TimeGateTheme(reduceMotion = true)` 显式覆盖。

| 动效 | 正常 | `reduceMotion = true` |
|---|---|---|
| 按钮 / Chip / ListRow 按压 | `scale(0.972)` + 120ms | `allowDisplacement = false` → scale 恒 **1f**，`snap()` → **无缩放** |
| Sheet 上滑 | 淡入 + 120px 上滑（`fadeSpec` 200ms） | `translationY` 恒 **0f**，`snap()` → **无位移** |
| TabBar 选中指示器 | spring(0.85, 300) 滑动 | `snap()` 瞬时到位 → **无滑动中间帧** |
| Switch 拇指 | spring 滑动 | `snap()` 瞬时到位 → **无滑动中间帧**（仍必须落到开/关位置表达状态） |
| Slider 进度补间 | 200ms 补间 | `snap()` 瞬时 |

> 代码层面是**显式清零**（`motion.allowDisplacement` / `translationY = 0f` / `snap()`），
> 不是靠「把动画调得很快」糊弄，可逐行审计。

---

## 8. 玻璃：半透明 + 顶光

`internal fun Modifier.tgGlassBackground(colors, shape)`（`Card.kt`，供 `TgCard` / `TgTabBar` 复用）：

1. `Brush.verticalGradient(glassTop → glassBottom)` 半透明面；
2. `border(0.5dp, glassRim, shape)` 细描边；
3. `drawBehind` 画顶部 1dp 高光线（横向渐隐，近似 demo 左上径向高光）；
4. `drawBehind` 画底部 1dp 暗边＝厚度。

**为什么不用 `backdrop-filter`**：Compose 的 `Modifier.blur` 模糊的是自身内容，不是背后的图层；Android 上没有与 CSS `backdrop-filter` 等价的公开能力。demo 也只在两处真用 blur（悬浮胶囊、弹窗遮罩），S05 统一降级为「半透明 + 顶光」，遮罩用 `c.scrim` 纯半透明，不做模糊。S10 的「玻璃通透度」设置可调 `glassTop/glassBottom` 的 alpha，不改这个结构。

---

## 9. 触控目标 ≥ 48dp

| 组件 | 可点区域 |
|---|---|
| `TgButton` | `heightIn(min = 50/48dp)` + `defaultMinSize(minWidth = 48dp)` |
| `TgChip` | `heightIn(min = 48dp)` + `defaultMinSize(minWidth = 48dp)` |
| `TgSwitch` | `sizeIn(minWidth = 51dp, minHeight = 48dp)`（轨道视觉仍 51×31） |
| `TgSlider` | 外层 `heightIn(min = 48dp)`，整行可点/可拖 |
| `TgTabBar` | 每个 Tab `heightIn(min = 48dp)` 且 `weight(1f)` |
| `TgListRow` | `heightIn(min = 64dp)` |
| `TgSheet` | 整屏遮罩可点关闭；内部按钮见 `TgButton` |
| `TgCard` / `TgRing` / `TgEmptyState` | 无自身点击区；内部操作交给上述组件 |

demo 的 `.btn--sm 36px` / `.btn--xs 30px` / `.tab__label 10px` 低于 48dp，S05 已按无障碍上调最小高度，**视觉密度靠 padding 而非缩小高度**保留。

---

## 10. 组件用法速查（S07 / S09 / S10 只读这一节就够）

统一先包一层主题：

```kotlin
TimeGateTheme(darkTheme = isSystemInDarkTheme()) {
    // 页面内容
}
```

| 组件 | 主签名 | 用途 / 注意 |
|---|---|---|
| `TgButton` | `(text, onClick, modifier, variant, size, enabled)` | `variant`: Primary / Glass / Ghost / Danger；`size`: Block / Small / Compact。**不要自己写 Button** |
| `TgCard` | `(modifier, glass, content: @Composable ColumnScope.() -> Unit)` | `glass = true` 走半透明 + 顶光 |
| `TgTabBar` | `(items: List<TgTabItem>, selectedIndex, onSelect, modifier, compact)` | 图标用 `TgTabIcon` 枚举自绘；指示器位移由 `motion` 控制 |
| `TgSheet` | `(visible, onDismiss, title, modifier, kicker, content)` | 底部弹窗；闸门三问、时长选择都用它；内容可滚动 |
| `TgRing` | `(modifier, progress, centerText, caption, segments, ringSize)` | 构成环；传 `segments` 画分段，`progress` 是单段简写 |
| `TgChip` | `(text, selected, onClick, modifier, enabled)` | 意图标签 / 替代动作 / 时长档位 |
| `TgSwitch` | `(checked, onCheckedChange, modifier, enabled)` | 设置开关；自绘，带 `Role.Switch` 语义 |
| `TgSlider` | `(value, onValueChange, modifier, valueRange, enabled)` | 玻璃通透度、自定义时长 1–120 |
| `TgListRow` | `(title, modifier, subtitle, trailing, progress, leadingText, onClick)` | 受管应用行 / 设置行；`progress` 非空才画数据条 |
| `TgEmptyState` | `(title, modifier, message, action)` | 零数据页；纯中性色，不用 tint |

**通用约定**

- 颜色：`TimeGateTheme.colors.*` 或 `MaterialTheme.colorScheme.*`，**禁止** `Color(0x…)`。
- 字号：`TimeGateTheme.type.*`，全部 `sp`。
- 圆角：`TimeGateTheme.shapes.*`。
- 动效：`TimeGateTheme.motion.*`，任何位移动画前先判 `allowDisplacement`。
- 页面层不得新增 `Button` / `Card` / `Switch` 等同类实现；确需新形态，走跨 ownership 变更请求给 S05。

---

## 11. 预览与自检

### 11.1 预览清单

- 10 个组件各 2 组 `@Preview`（深色 `UI_MODE_NIGHT_YES` / 浅色 `UI_MODE_NIGHT_NO`），共 20+ 组；
- `TgSheet` 额外 1 组「减弱动效（无位移）」；
- `ComponentGallery.kt` 汇总 4 组：`Gallery · 深色` / `· 浅色` / `· 字号 200%`(`fontScale = 2f`) / `· 减弱动效`。

在 Android Studio 的 Preview 面板里逐项展开即可核对；`Gallery · 字号 200%` 与 `Gallery · 减弱动效` 用于人工走查两种情况。

### 11.2 硬编码颜色自检

```bash
# ui/components 下不应出现任何十六进制色值（预期：无输出）
grep -rniE '0x[0-9a-f]{6,8}|#[0-9a-f]{6,8}' \
  app/src/main/java/com/timegate/app/ui/components/
```

### 11.3 主题文件自检

```bash
ls app/src/main/java/com/timegate/app/ui/theme/     # 应有且仅有 5 个 .kt
ls app/src/main/java/com/timegate/app/ui/components/ # 10 个组件 + ComponentGallery.kt
```

---

## 12. 与 demo 的有意差异（取舍记录，避免后续被当成 bug 修回去）

| # | demo | S05 | 理由 |
|---|---|---|---|
| 1 | 主按钮白色文字用 `--text-1` / 次级用 `--text-2` | 所有按钮文字走 `btnSolidInk` / `textPrimary` | 浅色 `text-2` 仅 3.44:1，正文必须 ≥4.5:1 |
| 2 | `.btn--danger` 红色文字 | 红色**描边** + `textPrimary` 文字 | 浅色 `danger` 3.55:1；危险语义由描边保留 |
| 3 | `.btn--sm 36px` / `.btn--xs 30px` | 最小 48dp | 触控目标 ≥48dp |
| 4 | `.app-ico` tint 渐变图标方块 | `TgListRow.leadingText` 用中性 `surface3` | tint 只允许 5 处；应用图标不在清单内 |
| 5 | `backdrop-filter: blur(30px)` | 半透明渐变 + 顶光 + 底暗边 | Compose 无背景模糊等价物 |
| 6 | `--glass-blur` / `--glass-alpha` 动态可调 | S05 提供静态基线；动态由 S10 设置页接 | 玻璃通透度属 S10 ownership |
| 7 | `.tab__label 10px` 固定高度胶囊 | `heightIn` 允许撑高 | 200% 字号不截断 |
| 8 | `@media (prefers-reduced-motion)` | `ANIMATOR_DURATION_SCALE == 0` + 显式 `allowDisplacement` | Android 无 CSS 媒体查询 |

以上**只改用法与降级方式，不改任何 CSS 变量的值**；`Color.kt` 的深/浅两套色值与 `demo/index.html` 逐项一致。
