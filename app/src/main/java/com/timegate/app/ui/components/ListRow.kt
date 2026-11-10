package com.timegate.app.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timegate.app.ui.theme.TimeGateTheme

/**
 * 列表行：demo `.app-row` / `.row`（surface 底、12dp 圆角、图标 + 名称 + 已用 + 进度条）。
 *
 * tint 第 4 处「图表 / 数据条」——`progress` 不为空时，细进度条用 `tint → tintOn` 渐变。
 * 左侧图标方块刻意用中性 `surface3`（demo `.app-ico` 的 tint 渐变收敛掉，
 * 因为 tint 只允许 5 处，见 docs/DESIGN.md）；需要强调色时由调用方传入。
 * 触控目标 ≥ 64dp；文字容器 wrapContentHeight，字号 200% 不截断。
 */
@Composable
fun TgListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: String? = null,
    progress: Float? = null,
    leadingText: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val colors = TimeGateTheme.colors
    val shapes = TimeGateTheme.shapes
    val type = TimeGateTheme.type
    val motion = TimeGateTheme.motion

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && onClick != null && motion.allowDisplacement) {
            motion.pressScale
        } else {
            1f
        },
        animationSpec = motion.pressSpec,
        label = "TgListRowPress",
    )

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = { onClick.invoke() },
        )
    } else {
        Modifier
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .background(colors.surface, shapes.md)
            .then(clickModifier)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingText != null) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colors.surface3, shapes.sm),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = leadingText, color = colors.textPrimary, fontSize = type.bodyMedium)
            }
            Spacer(modifier = Modifier.width(13.dp))
        }

        Column(modifier = Modifier.weight(1f).wrapContentHeight()) {
            Text(text = title, color = colors.textPrimary, fontSize = type.bodyLarge)
            if (subtitle != null) {
                Text(text = subtitle, color = colors.textPrimary, fontSize = type.caption)
            }
            if (progress != null) {
                Spacer(modifier = Modifier.height(7.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(colors.ringTrack, shapes.full),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .height(4.dp)
                            .background(
                                Brush.horizontalGradient(listOf(colors.tint, colors.tintOn)),
                                shapes.full,
                            ),
                    )
                }
            }
        }

        if (trailing != null) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = trailing,
                color = colors.textPrimary,
                fontSize = type.bodySmall,
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun TgListRowSample() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TgListRow(
            title = "抖音",
            subtitle = "今日 41 分钟 · 上限 60",
            trailing = "41 / 60",
            progress = 0.68f,
            leadingText = "抖",
            onClick = { },
        )
        TgListRow(
            title = "小红书",
            trailing = "18 / 30",
            progress = 0.60f,
            leadingText = "红",
        )
        TgListRow(title = "主题", trailing = "深色")
    }
}

@Preview(name = "ListRow · 深色", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TgListRowDarkPreview() {
    TgPreviewSurface(darkTheme = true) { TgListRowSample() }
}

@Preview(name = "ListRow · 浅色", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun TgListRowLightPreview() {
    TgPreviewSurface(darkTheme = false) { TgListRowSample() }
}
