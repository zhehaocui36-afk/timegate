package com.timegate.app.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timegate.app.ui.theme.TimeGateColors
import com.timegate.app.ui.theme.TimeGateTheme

/**
 * 玻璃模拟：「半透明面 + 顶光 + 底暗边 + 细描边」。
 *
 * Android / Compose 没有 CSS `backdrop-filter` 的等价物：`Modifier.blur` 模糊的是
 * 自身内容，不是背后的图层。按 S05 约束，这里用半透明渐变 + 顶部高光线 + 底部暗边
 * 表达「玻璃厚度」，不追背景折射。demo 对应 `.tabbar__capsule` / `.btn--glass`。
 *
 * 仅本包内部复用（TgCard / TgTabBar），**不是第 11 个组件**。
 */
internal fun Modifier.tgGlassBackground(colors: TimeGateColors, shape: Shape): Modifier =
    this
        .background(Brush.verticalGradient(listOf(colors.glassTop, colors.glassBottom)), shape)
        .border(width = 0.5.dp, color = colors.glassRim, shape = shape)
        .drawBehind {
            val w = 1.dp.toPx()
            // 顶光：左强右弱，近似 demo 左上径向高光
            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(colors.glassTopLight, colors.glassTopLight.copy(alpha = 0f)),
                ),
                start = Offset(0f, w),
                end = Offset(size.width, w),
                strokeWidth = w,
            )
            // 底部暗边 = 厚度
            drawLine(
                color = colors.glassBottomShade,
                start = Offset(0f, size.height - w),
                end = Offset(size.width, size.height - w),
                strokeWidth = w,
            )
        }

/**
 * 预览统一外壳：显式指定深/浅与减弱动效，并铺一层主题背景。
 * 组件文件里因此不需要任何十六进制色值（含预览）。
 */
@Composable
internal fun TgPreviewSurface(
    darkTheme: Boolean,
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit,
) {
    TimeGateTheme(darkTheme = darkTheme, reduceMotion = reduceMotion) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
        ) {
            content()
        }
    }
}

/**
 * 卡片：demo `.card`，`--surface` + `--r-md`(12dp) + 15dp 内边距。
 * `glass = true` 时改走玻璃模拟（半透明 + 顶光）。
 * 用 `wrapContentHeight` 而非固定高度，字号 200% 时自动撑高，不裁字。
 */
@Composable
fun TgCard(
    modifier: Modifier = Modifier,
    glass: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = TimeGateTheme.colors
    val shapes = TimeGateTheme.shapes
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .then(
                if (glass) Modifier.tgGlassBackground(colors, shapes.md)
                else Modifier.background(colors.surface, shapes.md)
            )
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        content = content,
    )
}

@Composable
private fun TgCardSample() {
    val colors = TimeGateTheme.colors
    val type = TimeGateTheme.type
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TgCard {
            Text("普通卡片 · --surface", color = colors.textPrimary, fontSize = type.bodyMedium)
            Text("正文走 textPrimary，深/浅两套都 ≥ 17:1", color = colors.textPrimary, fontSize = type.bodySmall)
        }
        TgCard(glass = true) {
            Text("玻璃卡片 · 半透明 + 顶光", color = colors.textPrimary, fontSize = type.bodyMedium)
            Text("无 backdrop-filter，无 Modifier.blur", color = colors.textPrimary, fontSize = type.bodySmall)
        }
    }
}

@Preview(name = "Card · 深色", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TgCardDarkPreview() {
    TgPreviewSurface(darkTheme = true) { TgCardSample() }
}

@Preview(name = "Card · 浅色", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun TgCardLightPreview() {
    TgPreviewSurface(darkTheme = false) { TgCardSample() }
}
