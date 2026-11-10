package com.timegate.app.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timegate.app.ui.theme.TimeGateTheme

/**
 * 底部弹窗：demo `.overlay--sheet .panel`（顶部圆角 28、抓手、kicker、标题），
 * 闸门三问与引导都由 S07/S09 消费这个组件。
 *
 * - 遮罩用半透明 scrim，**不用 blur**（Compose 无 backdrop-filter 等价物）；
 * - 进入动效：淡入 + 120px 上滑；减弱动效时 `allowDisplacement == false`
 *   → `translationY` 恒为 0，且 `fadeSpec = snap()`，只有状态切换、**没有任何位移**；
 * - 内容 `verticalScroll` + `heightIn(max)`：字号 200% 时可滚动，不截断；
 * - 遮罩整层可点关闭，触控目标远大于 48dp。
 */
@Composable
fun TgSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    kicker: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = TimeGateTheme.colors
    val shapes = TimeGateTheme.shapes
    val type = TimeGateTheme.type
    val motion = TimeGateTheme.motion

    var target by remember { mutableStateOf(if (visible) 1f else 0f) }
    LaunchedEffect(visible) { target = if (visible) 1f else 0f }
    val progress by animateFloatAsState(
        targetValue = target,
        animationSpec = motion.fadeSpec,
        label = "TgSheetProgress",
    )

    if (visible || progress > 0f) {
        Box(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(progress)
                    .background(colors.scrim)
                    .clickable(interactionSource = null, indication = null) { onDismiss() },
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        alpha = progress
                        translationY = if (motion.allowDisplacement) {
                            (1f - progress) * 120f
                        } else {
                            0f
                        }
                    }
                    .background(colors.background, shapes.sheetTop)
                    .heightIn(max = 640.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 36.dp, height = 5.dp)
                        .background(colors.surface3, shapes.full),
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (kicker != null) {
                    Text(
                        text = kicker,
                        color = colors.textPrimary,
                        fontSize = type.caption,
                        textAlign = TextAlign.Center,
                    )
                }
                Text(
                    text = title,
                    color = colors.textPrimary,
                    fontSize = type.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}

@Composable
private fun TgSheetSample(reduceMotion: Boolean = false) {
    TgPreviewSurface(darkTheme = true, reduceMotion = reduceMotion) {
        Box(modifier = Modifier.height(360.dp)) {
            TgSheet(
                visible = true,
                onDismiss = { },
                title = "这次用多久？",
                kicker = "抖音 · 今天已用 41 分钟",
            ) {
                RowChips()
                TgButton(text = "继续", onClick = { })
                TgButton(text = "直接退出", onClick = { }, variant = TgButtonVariant.Ghost)
            }
        }
    }
}

@Composable
private fun RowChips() {
    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TgChip(text = "5 分钟", selected = true, onClick = { })
        TgChip(text = "15 分钟", selected = false, onClick = { })
    }
}

@Preview(name = "Sheet · 深色", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TgSheetDarkPreview() {
    TgSheetSample()
}

@Preview(name = "Sheet · 浅色", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun TgSheetLightPreview() {
    TgPreviewSurface(darkTheme = false) {
        Box(modifier = Modifier.height(360.dp)) {
            TgSheet(
                visible = true,
                onDismiss = { },
                title = "这次用多久？",
                kicker = "抖音 · 今天已用 41 分钟",
            ) {
                TgButton(text = "继续", onClick = { })
                TgButton(text = "直接退出", onClick = { }, variant = TgButtonVariant.Ghost)
            }
        }
    }
}

@Preview(name = "Sheet · 减弱动效（无位移）", showBackground = true)
@Composable
private fun TgSheetReducedMotionPreview() {
    TgSheetSample(reduceMotion = true)
}
