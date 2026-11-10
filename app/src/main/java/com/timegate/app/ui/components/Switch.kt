package com.timegate.app.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timegate.app.ui.theme.TimeGateTheme

/**
 * 开关：demo `.switch`（轨道 51×31、拇指 27、行程 20）。自绘，不套 Material3 Switch。
 *
 * tint 第 2 处「开关」——仅 checked 轨道用 `tint`；未选中用中性 `ringTrack`。
 * 外层 `sizeIn(minHeight = 48.dp)` 保证触控目标 ≥ 48dp。
 * 减弱动效：`moveSpec` 退化为 `snap()`，拇指瞬时到位，**没有中间位移动画**；
 * 开关仍必须落到开/关位置以表达状态，这与 demo「prefers-reduced-motion 下不位移」等价。
 */
@Composable
fun TgSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = TimeGateTheme.colors
    val motion = TimeGateTheme.motion
    val fraction by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = motion.moveSpec,
        label = "TgSwitchThumb",
    )

    Box(
        modifier = modifier
            .sizeIn(minWidth = 51.dp, minHeight = 48.dp)
            .alpha(if (enabled) 1f else colors.disabledAlpha)
            .toggleable(
                value = checked,
                interactionSource = null,
                indication = null,
                enabled = enabled,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(width = 51.dp, height = 31.dp)) {
            val r = size.height / 2f
            drawRoundRect(
                color = if (checked) colors.tint else colors.ringTrack,
                cornerRadius = CornerRadius(r, r),
            )
            val pad = 2.dp.toPx()
            val thumbR = 13.5.dp.toPx()
            val travel = size.width - 2f * pad - 2f * thumbR
            val cx = pad + thumbR + travel * fraction
            val cy = size.height / 2f
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
private fun TgSwitchSample() {
    val colors = TimeGateTheme.colors
    val type = TimeGateTheme.type
    var on by remember { mutableStateOf(true) }
    var off by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("震动反馈", color = colors.textPrimary, fontSize = type.bodyMedium)
        TgSwitch(checked = on, onCheckedChange = { on = it })
        TgSwitch(checked = off, onCheckedChange = { off = it })
    }
}

@Preview(name = "Switch · 深色", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TgSwitchDarkPreview() {
    TgPreviewSurface(darkTheme = true) { TgSwitchSample() }
}

@Preview(name = "Switch · 浅色", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun TgSwitchLightPreview() {
    TgPreviewSurface(darkTheme = false) { TgSwitchSample() }
}
