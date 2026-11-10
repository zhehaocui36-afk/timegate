package com.timegate.app.ui.theme

import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

/**
 * 闸 · TimeGate — 主题入口（S05）
 *
 * 用法（页面 / 预览统一包一层）：
 * ```
 * TimeGateTheme(darkTheme = true) {
 *     TgButton(text = "开始 25 分钟专注", onClick = { })
 * }
 * ```
 *
 * 三种要「不崩」的情况都在这里收口：
 *  1. 深/浅切换 —— `darkTheme` 决定两套 `TimeGateColors` + 两套 `ColorScheme`；
 *  2. 字号放大 —— 不覆盖 `LocalDensity`，完全交给系统 / @Preview(fontScale)，
 *                 所有字号与行高用 sp、容器用 heightIn，200% 自动撑高而不裁字；
 *  3. 减弱动效 —— `reduceMotion` 默认读系统 ANIMATOR_DURATION_SCALE，
 *                 也可显式传 true（设置页开关、预览走查）。
 *
 * 组件一律基于 Compose Foundation 自绘，只把颜色/字号/圆角/动效规格当 token 消费，
 * 不套用 Material3 组件的默认外观。
 */

/** 组件层统一的 token 访问入口：`TimeGateTheme.colors.tint` 等。 */
object TimeGateTheme {
    val colors: TimeGateColors
        @Composable @ReadOnlyComposable get() = LocalTimeGateColors.current

    val type: TimeGateType
        @Composable @ReadOnlyComposable get() = LocalTimeGateType.current

    val shapes: TimeGateShapeTokens
        @Composable @ReadOnlyComposable get() = LocalTimeGateShapes.current

    val motion: TimeGateMotion
        @Composable @ReadOnlyComposable get() = LocalTimeGateMotion.current
}

/**
 * Android 上 `prefers-reduced-motion` 的等价信号。
 * 系统「动画时长缩放」为 0（关闭动画）时返回 true；读取失败按 false 处理，不崩。
 */
@Composable
fun systemPrefersReducedMotion(): Boolean {
    val context = LocalContext.current
    return runCatching {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) == 0f
    }.getOrDefault(false)
}

@Composable
fun TimeGateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    reduceMotion: Boolean? = null,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkTimeGateColors else LightTimeGateColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val motion = TimeGateMotion(
        reduceMotion = reduceMotion ?: systemPrefersReducedMotion(),
    )

    CompositionLocalProvider(
        LocalTimeGateColors provides colors,
        LocalTimeGateType provides TimeGateType.Default,
        LocalTimeGateShapes provides TimeGateShapeTokens.Default,
        LocalTimeGateMotion provides motion,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = TimeGateTypography,
            shapes = TimeGateShapes,
            content = content,
        )
    }
}
