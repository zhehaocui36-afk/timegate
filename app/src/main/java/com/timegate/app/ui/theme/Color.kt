package com.timegate.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * 闸 · TimeGate — 颜色 token（S05）
 *
 * 唯一权威来源：`demo/index.html` 内嵌 CSS 的 `:root` /
 * `html[data-theme="dark"]` / `html[data-theme="light"]` 两套变量。
 * 主 tint `#5B6CFF` 经三轮否决后拍板（ARCHITECTURE §9），不得改回绿/金。
 *
 * ⚠️ 本文件是 ui/ 层【唯一】允许出现十六进制色值的地方。
 *    ui/components 包一律通过 `TimeGateTheme.colors` 或 `MaterialTheme.colorScheme` 取色。
 *
 * 见 docs/DESIGN.md「CSS 变量 → Compose token 对照表」与「对比度审计」。
 */

/* ---------- 中性基色（仅用于合成半透明语义色，不直接暴露给组件） ---------- */
private val Black = Color(0xFF000000)
private val White = Color(0xFFFFFFFF)
private val DarkLineBase = Color(0xFF545458)      // rgba(84,84,88,α) 的基色
private val DarkTextBase = Color(0xFFEBEBF5)      // rgba(235,235,245,α) 的基色
private val LightLineBase = Color(0xFF3C3C43)     // rgba(60,60,67,α) 的基色
private val TrackBase = Color(0xFF787880)         // rgba(120,120,128,α) 的基色

/**
 * 与 demo CSS 变量一一对应的语义色集合。
 * 组件只应读取这里或 `MaterialTheme.colorScheme`，禁止自行拼 Color。
 */
@Immutable
data class TimeGateColors(
    val background: Color,
    val surface: Color,
    val surface2: Color,
    val surface3: Color,
    val line: Color,
    val lineStrong: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val tint: Color,
    val tintOn: Color,
    val tintSoft: Color,
    val tintInk: Color,
    val danger: Color,
    val warn: Color,
    val ringTrack: Color,
    val btnSolid: Color,
    val btnSolidInk: Color,
    val btnSolidHi: Color,
    val btnSolidGradientTop: Color,
    val btnSolidGradientEnd: Color,
    val tabIdle: Color,
    val glassTop: Color,
    val glassBottom: Color,
    val glassRim: Color,
    val glassTopLight: Color,
    val glassBottomShade: Color,
    val indicatorFill: Color,
    val scrim: Color,
    val thumb: Color,
    val shadow: Color,
    val disabledAlpha: Float = 0.30f,
)

/** 深色：demo `html[data-theme="dark"]`。 */
val DarkTimeGateColors = TimeGateColors(
    background = Black,                                  // --bg #000000
    surface = Color(0xFF1C1C1E),                         // --surface
    surface2 = Color(0xFF2C2C2E),                        // --surface-2
    surface3 = Color(0xFF3A3A3C),                        // --surface-3
    line = DarkLineBase.copy(alpha = 0.62f),             // --line
    lineStrong = DarkLineBase.copy(alpha = 0.95f),       // --line-strong
    textPrimary = White,                                 // --text-1 #FFFFFF
    textSecondary = DarkTextBase.copy(alpha = 0.60f),    // --text-2
    textTertiary = DarkTextBase.copy(alpha = 0.34f),     // --text-3
    tint = Color(0xFF4E5CEC),                            // --tint（填充/基础）
    tintOn = Color(0xFF5B6CFF),                          // --tint-on（强调/前景，主 tint）
    tintSoft = Color(0xFF5B6CFF).copy(alpha = 0.24f),    // --tint-soft
    tintInk = White,                                     // --tint-ink
    danger = Color(0xFFFF453A),                          // --danger
    warn = Color(0xFFFF9F0A),                            // --warn
    ringTrack = TrackBase.copy(alpha = 0.32f),           // --ring-track
    btnSolid = White,                                    // --btn-solid
    btnSolidInk = Black,                                 // --btn-solid-ink
    btnSolidHi = White.copy(alpha = 0.55f),              // --btn-solid-hi
    btnSolidGradientTop = White,                         // .btn--primary 渐变顶
    btnSolidGradientEnd = Color(0xFFE9E9EE),             // .btn--primary 渐变底
    tabIdle = DarkTextBase.copy(alpha = 0.50f),          // --tab-idle
    // 玻璃：半透明面 + 顶光 + 底暗边（Compose 无 backdrop-filter 等价物，见 DESIGN.md）
    glassTop = Color(0xFF3A3A3C).copy(alpha = 0.62f),
    glassBottom = Color(0xFF1C1C1E).copy(alpha = 0.78f),
    glassRim = White.copy(alpha = 0.13f),
    glassTopLight = White.copy(alpha = 0.26f),
    glassBottomShade = Black.copy(alpha = 0.55f),
    indicatorFill = White.copy(alpha = 0.13f),           // 内层选中胶囊
    scrim = Black.copy(alpha = 0.46f),                   // .overlay--sheet 遮罩
    thumb = White,                                       // 开关 / 滑杆拇指
    shadow = Black.copy(alpha = 0.45f),
)

/** 浅色：demo `html[data-theme="light"]`。 */
val LightTimeGateColors = TimeGateColors(
    background = Color(0xFFF2F2F7),                      // --bg
    surface = White,                                     // --surface
    surface2 = Color(0xFFE9E9EE),                        // --surface-2
    surface3 = Color(0xFFD6D6DC),                        // --surface-3
    line = LightLineBase.copy(alpha = 0.26f),            // --line
    lineStrong = LightLineBase.copy(alpha = 0.42f),      // --line-strong
    textPrimary = Black,                                 // --text-1 #000000
    textSecondary = LightLineBase.copy(alpha = 0.60f),   // --text-2
    textTertiary = LightLineBase.copy(alpha = 0.34f),    // --text-3
    tint = Color(0xFF4655DB),                            // --tint
    tintOn = Color(0xFF3541C9),                          // --tint-on
    tintSoft = Color(0xFF4655DB).copy(alpha = 0.14f),    // --tint-soft
    tintInk = White,                                     // --tint-ink
    danger = Color(0xFFFF3B30),                          // --danger
    warn = Color(0xFFC77700),                            // --warn
    ringTrack = TrackBase.copy(alpha = 0.20f),           // --ring-track
    btnSolid = Black,                                    // --btn-solid
    btnSolidInk = White,                                 // --btn-solid-ink
    btnSolidHi = White.copy(alpha = 0.20f),              // --btn-solid-hi
    btnSolidGradientTop = Color(0xFF2C2C2E),             // .btn--primary 渐变顶
    btnSolidGradientEnd = Black,                         // .btn--primary 渐变底
    tabIdle = LightLineBase.copy(alpha = 0.52f),         // --tab-idle
    glassTop = White.copy(alpha = 0.94f),
    glassBottom = Color(0xFFF4F4F8).copy(alpha = 0.94f),
    glassRim = LightLineBase.copy(alpha = 0.10f),
    glassTopLight = White.copy(alpha = 0.95f),
    glassBottomShade = Black.copy(alpha = 0.06f),
    indicatorFill = White,
    scrim = Black.copy(alpha = 0.46f),
    thumb = White,
    shadow = Black.copy(alpha = 0.30f),
)

/**
 * Material3 ColorScheme 映射：让组件可以「走 MaterialTheme 的 token」取色。
 * 注意 `onSurfaceVariant` 刻意用 `textPrimary` 而非 `textSecondary`——
 * 次级色在浅色主题下仅 3.44:1，不能作为正文（见 DESIGN.md 对比度审计）。
 */
val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = DarkTimeGateColors.tintOn,
    onPrimary = DarkTimeGateColors.tintInk,
    primaryContainer = DarkTimeGateColors.tint,
    onPrimaryContainer = DarkTimeGateColors.tintInk,
    secondary = DarkTimeGateColors.surface3,
    onSecondary = DarkTimeGateColors.textPrimary,
    tertiary = DarkTimeGateColors.warn,
    onTertiary = DarkTimeGateColors.textPrimary,
    background = DarkTimeGateColors.background,
    onBackground = DarkTimeGateColors.textPrimary,
    surface = DarkTimeGateColors.surface,
    onSurface = DarkTimeGateColors.textPrimary,
    surfaceVariant = DarkTimeGateColors.surface2,
    onSurfaceVariant = DarkTimeGateColors.textPrimary,
    outline = DarkTimeGateColors.lineStrong,
    outlineVariant = DarkTimeGateColors.line,
    error = DarkTimeGateColors.danger,
    onError = DarkTimeGateColors.textPrimary,
    scrim = DarkTimeGateColors.scrim,
)

val LightColorScheme: ColorScheme = lightColorScheme(
    primary = LightTimeGateColors.tintOn,
    onPrimary = LightTimeGateColors.tintInk,
    primaryContainer = LightTimeGateColors.tint,
    onPrimaryContainer = LightTimeGateColors.tintInk,
    secondary = LightTimeGateColors.surface3,
    onSecondary = LightTimeGateColors.textPrimary,
    tertiary = LightTimeGateColors.warn,
    onTertiary = LightTimeGateColors.textPrimary,
    background = LightTimeGateColors.background,
    onBackground = LightTimeGateColors.textPrimary,
    surface = LightTimeGateColors.surface,
    onSurface = LightTimeGateColors.textPrimary,
    surfaceVariant = LightTimeGateColors.surface2,
    onSurfaceVariant = LightTimeGateColors.textPrimary,
    outline = LightTimeGateColors.lineStrong,
    outlineVariant = LightTimeGateColors.line,
    error = LightTimeGateColors.danger,
    onError = LightTimeGateColors.textPrimary,
    scrim = LightTimeGateColors.scrim,
)

/** 未提供 TimeGateTheme 时（例如单组件裸预览）的兜底，避免 CompositionLocal 读不到而崩溃。 */
val LocalTimeGateColors = staticCompositionLocalOf { DarkTimeGateColors }
