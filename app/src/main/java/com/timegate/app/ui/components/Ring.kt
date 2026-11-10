package com.timegate.app.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.timegate.app.ui.theme.TimeGateTheme

/** 构成环的一段。`color` 为空时走主题 tint→tintOn 渐变（tint 第 3 处）。 */
data class TgRingSegment(val fraction: Float, val color: Color? = null)

/**
 * 构成环：demo `.ring`（212×212、线宽 9、圆头、段间距 16px）。
 * 回答「时间花在哪了」，不是带分母的进度环——删掉每日总预算后环没有分母。
 *
 * tint 第 3 处「环形分段」：各段用 `tint → tintOn` 线性渐变；
 * 调用方（S10 今日页）也可通过 [TgRingSegment.color] 传 RAMP 色，但组件内不硬编码任何颜色。
 * 中心文字用 `wrapContentSize(unbounded = true)`，字号 200% 时允许溢出圆环但不被裁切。
 */
@Composable
fun TgRing(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    centerText: String = "",
    caption: String? = null,
    segments: List<TgRingSegment> = emptyList(),
    ringSize: Dp = 212.dp,
) {
    val colors = TimeGateTheme.colors
    val type = TimeGateTheme.type

    Box(modifier = modifier.size(ringSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 9.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(
                (size.width - stroke).coerceAtLeast(0f),
                (size.height - stroke).coerceAtLeast(0f),
            )
            drawArc(
                color = colors.ringTrack,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )

            val list = if (segments.isEmpty()) {
                listOf(TgRingSegment(progress.coerceIn(0f, 1f)))
            } else {
                segments
            }
            val gap = if (list.size > 1) 8f else 4f
            var start = -90f
            list.forEach { segment ->
                val sweep = 360f * segment.fraction.coerceIn(0f, 1f)
                val visible = (sweep - gap).coerceAtLeast(0f)
                val segmentColor = segment.color
                val brush = if (segmentColor != null) {
                    Brush.linearGradient(listOf(segmentColor, segmentColor))
                } else {
                    Brush.linearGradient(listOf(colors.tint, colors.tintOn))
                }
                if (visible > 0f) {
                    drawArc(
                        brush = brush,
                        startAngle = start + gap / 2f,
                        sweepAngle = visible,
                        useCenter = false,
                        topLeft = Offset(inset, inset),
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }
                start += sweep
            }
        }

        Column(
            modifier = Modifier.wrapContentSize(align = Alignment.Center, unbounded = true),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = centerText,
                color = colors.textPrimary,
                fontSize = type.numRing,
                textAlign = TextAlign.Center,
            )
            if (caption != null) {
                Text(
                    text = caption,
                    color = colors.textPrimary,
                    fontSize = type.caption,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun TgRingSample() {
    TgRing(
        progress = 0.68f,
        centerText = "77m",
        caption = "今日已用",
        segments = listOf(
            TgRingSegment(0.41f),
            TgRingSegment(0.18f),
            TgRingSegment(0.12f),
        ),
    )
}

@Preview(name = "Ring · 深色", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TgRingDarkPreview() {
    TgPreviewSurface(darkTheme = true) { TgRingSample() }
}

@Preview(name = "Ring · 浅色", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun TgRingLightPreview() {
    TgPreviewSurface(darkTheme = false) { TgRingSample() }
}
