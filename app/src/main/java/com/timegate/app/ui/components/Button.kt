package com.timegate.app.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timegate.app.ui.theme.TimeGateTheme

/** demo `.btn--primary` / `.btn--glass` / `.btn--ghost` / `.btn--danger`。 */
enum class TgButtonVariant { Primary, Glass, Ghost, Danger }

/** 三档尺寸；最小高度一律 ≥ 48dp（demo 的 36/30 不满足触控目标，已按无障碍上调）。 */
enum class TgButtonSize { Block, Small, Compact }

/**
 * 主按钮 = 深/浅反色实心；玻璃 = 半透明 + 顶光；幽灵 = 描边；危险 = danger 描边。
 *
 * - 触控目标 ≥ 48dp：`heightIn(min)` + `defaultMinSize(minWidth)`；
 * - 字号 200% 不截断：不用固定 `height`、不设 `maxLines`/Ellipsis，文字可换行撑高；
 * - 减弱动效：`motion.allowDisplacement == false` 时按压 scale 恒为 1f（无位移/缩放）；
 * - 正文对比度：文字一律 `btnSolidInk` / `textPrimary`（17–21:1）；
 *   危险语义由 `danger` 描边传达，不用低对比的红字。
 */
@Composable
fun TgButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: TgButtonVariant = TgButtonVariant.Primary,
    size: TgButtonSize = TgButtonSize.Block,
    enabled: Boolean = true,
) {
    val colors = TimeGateTheme.colors
    val shapes = TimeGateTheme.shapes
    val type = TimeGateTheme.type
    val motion = TimeGateTheme.motion

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val focused by interactionSource.collectIsFocusedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled && motion.allowDisplacement) motion.pressScale else 1f,
        animationSpec = motion.pressSpec,
        label = "TgButtonPress",
    )

    val minHeight = when (size) {
        TgButtonSize.Block -> 50.dp
        TgButtonSize.Small, TgButtonSize.Compact -> 48.dp
    }
    val horizontalPadding = when (size) {
        TgButtonSize.Block -> 22.dp
        TgButtonSize.Small -> 16.dp
        TgButtonSize.Compact -> 13.dp
    }
    val fontSize = when (size) {
        TgButtonSize.Block -> type.bodyLarge
        TgButtonSize.Small -> type.bodyMedium
        TgButtonSize.Compact -> type.bodySmall
    }

    val backgroundModifier = when (variant) {
        TgButtonVariant.Primary -> Modifier.background(
            Brush.verticalGradient(
                listOf(colors.btnSolidGradientTop, colors.btnSolidGradientEnd),
            ),
            shapes.full,
        )
        TgButtonVariant.Glass -> Modifier.tgGlassBackground(colors, shapes.full)
        TgButtonVariant.Ghost -> Modifier.background(colors.surface2, shapes.full)
        TgButtonVariant.Danger -> Modifier.background(colors.surface, shapes.full)
    }

    val borderColor = when {
        focused && enabled -> colors.tintSoft          // :focus-visible 的 3px tint-soft 外环
        variant == TgButtonVariant.Danger -> colors.danger
        variant == TgButtonVariant.Ghost -> colors.lineStrong
        else -> colors.glassRim
    }
    val borderWidth = if (focused && enabled) 3.dp else 0.5.dp
    val textColor = if (variant == TgButtonVariant.Primary) colors.btnSolidInk
    else colors.textPrimary

    Row(
        modifier = modifier
            .then(if (size == TgButtonSize.Block) Modifier.fillMaxWidth() else Modifier)
            .heightIn(min = minHeight)
            .defaultMinSize(minWidth = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .alpha(if (enabled) 1f else colors.disabledAlpha)
            .then(backgroundModifier)
            .border(width = borderWidth, color = borderColor, shape = shapes.full)
            .clickable(
                interactionSource = interactionSource,
                indication = null,               // demo 无 ripple，按压由自绘 scale 表达
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = horizontalPadding, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TgButtonSample() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        TgButton(text = "开始 25 分钟专注", onClick = { })
        TgButton(text = "现在打开", onClick = { }, variant = TgButtonVariant.Glass, size = TgButtonSize.Small)
        TgButton(text = "挑一个应用，试试闸门", onClick = { }, variant = TgButtonVariant.Ghost, size = TgButtonSize.Small)
        TgButton(text = "清空", onClick = { }, variant = TgButtonVariant.Danger, size = TgButtonSize.Compact)
        TgButton(text = "禁用态", onClick = { }, enabled = false, size = TgButtonSize.Small)
    }
}

@Preview(name = "Button · 深色", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TgButtonDarkPreview() {
    TgPreviewSurface(darkTheme = true) { TgButtonSample() }
}

@Preview(name = "Button · 浅色", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun TgButtonLightPreview() {
    TgPreviewSurface(darkTheme = false) { TgButtonSample() }
}
