package com.timegate.app.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.wrapContentWidth
import com.timegate.app.ui.theme.TimeGateTheme

/**
 * 标签 / 选项：demo `.chip`。
 *
 * tint 第 5 处「可点文字与选中态」——仅 `selected` 时背景用 `tint`、文字用 `tintInk`；
 * 未选中一律中性色。禁用态 alpha .3。触控目标 ≥ 48dp，减弱动效下无按压缩放。
 */
@Composable
fun TgChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = TimeGateTheme.colors
    val shapes = TimeGateTheme.shapes
    val type = TimeGateTheme.type
    val motion = TimeGateTheme.motion

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled && motion.allowDisplacement) motion.pressScale else 1f,
        animationSpec = motion.pressSpec,
        label = "TgChipPress",
    )

    val background = if (selected) colors.tint else colors.surface2
    val foreground = if (selected) colors.tintInk else colors.textPrimary

    Box(
        modifier = modifier
            .wrapContentWidth()
            .heightIn(min = 48.dp)
            .defaultMinSize(minWidth = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .alpha(if (enabled) 1f else colors.disabledAlpha)
            .background(background, shapes.full)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = foreground,
            fontSize = type.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TgChipSample() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TgChip(text = "回消息", selected = true, onClick = { })
        TgChip(text = "查资料", selected = false, onClick = { })
        TgChip(text = "禁用", selected = false, onClick = { }, enabled = false)
    }
}

@Preview(name = "Chip · 深色", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TgChipDarkPreview() {
    TgPreviewSurface(darkTheme = true) { TgChipSample() }
}

@Preview(name = "Chip · 浅色", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun TgChipLightPreview() {
    TgPreviewSurface(darkTheme = false) { TgChipSample() }
}
