package com.timegate.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * 闸 · TimeGate — 字号 / 字重 token（S05）
 *
 * 字号阶梯来自 HANDOFF F.2 与 demo CSS：30 / 24 / 22 / 17 / 16 / 15 / 13 / 12 / 11 / 10。
 * 演示页的数字专用字号（78 / 66 / 40 / 32 / 21）单独列出，供 Ring / 倒计时 / 专注页消费。
 *
 * ⚠️ 全部用 `sp`，行高也用 `sp`：系统字号放大到 200% 时会等比放大。
 *    组件层因此【禁止】用固定 `height()` 或 `maxLines=1 + Ellipsis` 包文字，
 *    统一用 `heightIn(min = …)` / `wrapContentHeight`，保证 200% 不截断。
 */

@Immutable
data class TimeGateType(
    val displayLarge: TextUnit = 30.sp,
    val displayMedium: TextUnit = 24.sp,
    val titleLarge: TextUnit = 22.sp,
    val titleMedium: TextUnit = 17.sp,
    val bodyLarge: TextUnit = 16.sp,
    val bodyMedium: TextUnit = 15.sp,
    val bodySmall: TextUnit = 13.sp,
    val caption: TextUnit = 12.sp,
    val overline: TextUnit = 11.sp,
    val label: TextUnit = 10.sp,
    val numHero: TextUnit = 78.sp,
    val numFocus: TextUnit = 66.sp,
    val numRing: TextUnit = 40.sp,
    val numBig: TextUnit = 32.sp,
    val numDuration: TextUnit = 21.sp,
) {
    companion object {
        val Default = TimeGateType()
    }
}

/** 交给 MaterialTheme；组件也可用 `MaterialTheme.typography.*`。 */
val TimeGateTypography = Typography(
    displayLarge = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Medium, lineHeight = 38.sp),
    displayMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium, lineHeight = 32.sp),
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Medium, lineHeight = 30.sp),
    titleMedium = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, lineHeight = 22.sp),
    bodySmall = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, lineHeight = 19.sp),
    labelLarge = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, lineHeight = 22.sp),
    labelMedium = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, lineHeight = 18.sp),
    labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal, lineHeight = 16.sp),
)

val LocalTimeGateType = staticCompositionLocalOf { TimeGateType.Default }
