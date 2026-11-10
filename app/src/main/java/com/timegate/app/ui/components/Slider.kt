package com.timegate.app.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timegate.app.ui.theme.TimeGateTheme

/**
 * 滑杆：demo `.range`（轨道 4dp、白色 24dp 拇指）。自绘，不套 Material3 Slider。
 *
 * tint 第 4 处「图表 / 数据条」——已完成轨道用 `tint → tintOn` 渐变，
 * 与柱图、排行条、漏斗轨、列表进度条同一套 token。
 * 触控目标 ≥ 48dp（外层 heightIn），点击与水平拖动都能改值。
 * 减弱动效下 `fadeSpec = snap()`，进度瞬时到位，无补间位移。
 */
@Composable
fun TgSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    enabled: Boolean = true,
) {
    val colors = TimeGateTheme.colors
    val motion = TimeGateTheme.motion

    val span = valueRange.endInclusive - valueRange.start
    val fraction = ((value - valueRange.start) / span).coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = fraction,
        animationSpec = motion.fadeSpec,
        label = "TgSliderProgress",
    )

    val applyFraction: (Float) -> Unit = { f ->
        onValueChange(valueRange.start + f.coerceIn(0f, 1f) * span)
    }

    val gestureModifier = if (enabled) {
        Modifier
            .pointerInput(valueRange) {
                detectTapGestures { offset ->
                    applyFraction(offset.x / size.width.toFloat())
                }
            }
            .pointerInput(valueRange) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        applyFraction(offset.x / size.width.toFloat())
                    },
                    onHorizontalDrag = { change, _ ->
                        applyFraction(change.position.x / size.width.toFloat())
                    },
                )
            }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .alpha(if (enabled) 1f else colors.disabledAlpha)
            .then(gestureModifier),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(48.dp)) {
            val trackH = 4.dp.toPx()
            val cy = size.height / 2f
            val thumbR = 12.dp.toPx()
            val left = thumbR
            val trackW = (size.width - 2f * thumbR).coerceAtLeast(0f)
            val corner = CornerRadius(trackH / 2f, trackH / 2f)

            drawRoundRect(
                color = colors.ringTrack,
                topLeft = Offset(left, cy - trackH / 2f),
                size = Size(trackW, trackH),
                cornerRadius = corner,
            )
            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(colors.tint, colors.tintOn)),
                topLeft = Offset(left, cy - trackH / 2f),
                size = Size(trackW * animated, trackH),
                cornerRadius = corner,
            )

            val cx = left + trackW * animated
            drawCircle(
                color = colors.shadow,
                radius = thumbR + 1.dp.toPx(),
                center = Offset(cx, cy + 1.dp.toPx()),
            )
            drawCircle(color = colors.thumb, radius = thumbR, center = Offset(cx, cy))
        }
    }
}

@Composable
private fun TgSliderSample() {
    val colors = TimeGateTheme.colors
    val type = TimeGateTheme.type
    var value by remember { mutableStateOf(60f) }
    Column {
        Text(
            text = "玻璃通透度  ${value.toInt()}",
            color = colors.textPrimary,
            fontSize = type.bodyMedium,
        )
        TgSlider(value = value, onValueChange = { value = it }, valueRange = 0f..100f)
    }
}

@Preview(name = "Slider · 深色", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TgSliderDarkPreview() {
    TgPreviewSurface(darkTheme = true) { TgSliderSample() }
}

@Preview(name = "Slider · 浅色", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun TgSliderLightPreview() {
    TgPreviewSurface(darkTheme = false) { TgSliderSample() }
}
