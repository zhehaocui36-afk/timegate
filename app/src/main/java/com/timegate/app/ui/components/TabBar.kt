package com.timegate.app.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timegate.app.ui.theme.TimeGateTheme

/** demo 四个 Tab 的极简线稿图标（自绘，避免引入 material-icons 依赖）。 */
enum class TgTabIcon { Today, Apps, Stats, Me }

data class TgTabItem(val label: String, val icon: TgTabIcon)

/**
 * 悬浮胶囊导航：demo `.tabbar__capsule`（高 64dp、compact 56dp、50% 圆角、双层玻璃）。
 *
 * tint 第 1 处「Tab 选中」——仅选中项图标/文字用 `tintOn`，未选中用 `tabIdle`。
 * 玻璃走 `tgGlassBackground`（半透明 + 顶光），不使用 backdrop-filter。
 * 选中指示器用 `motion.moveSpec`：正常弹簧滑动，减弱动效下 `snap()` 瞬时到位，无滑动动画。
 * 每个 Tab `heightIn(min = 48.dp)`；胶囊本身也用 heightIn，200% 字号时标签换行可撑高不裁字。
 */
@Composable
fun TgTabBar(
    items: List<TgTabItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val colors = TimeGateTheme.colors
    val shapes = TimeGateTheme.shapes
    val type = TimeGateTheme.type
    val motion = TimeGateTheme.motion
    val barHeight = if (compact) 56.dp else 64.dp

    val fraction by animateFloatAsState(
        targetValue = selectedIndex.coerceAtLeast(0).toFloat(),
        animationSpec = motion.moveSpec,
        label = "TgTabIndicator",
    )

    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = barHeight)
                .tgGlassBackground(colors, shapes.bar),
        ) {
            val count = items.size.coerceAtLeast(1)
            val itemWidth = maxWidth / count.toFloat()

            Box(
                modifier = Modifier
                    .width(itemWidth)
                    .heightIn(min = 46.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = itemWidth * fraction, y = 0.dp)
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .background(colors.indicatorFill, shapes.full),
            )

            Row(modifier = Modifier.fillMaxWidth().heightIn(min = barHeight)) {
                items.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    val foreground = if (selected) colors.tintOn else colors.tabIdle
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .clickable(interactionSource = null, indication = null) {
                                onSelect(index)
                            }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        TgTabIconGlyph(icon = item.icon, color = foreground)
                        Text(
                            text = item.label,
                            color = foreground,
                            fontSize = type.label,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TgTabIconGlyph(
    icon: TgTabIcon,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(23.dp)) {
        val w = size.width
        val h = size.height
        val sw = 1.7.dp.toPx()
        val mid = Offset(w / 2f, h / 2f)
        when (icon) {
            TgTabIcon.Today -> {
                drawCircle(color = color, radius = w * 0.35f, center = mid, style = Stroke(sw))
                drawCircle(color = color, radius = w * 0.10f, center = mid)
            }
            TgTabIcon.Apps -> {
                val s = w * 0.36f
                val p = 2.dp.toPx()
                val r = 2.dp.toPx()
                listOf(
                    Offset(p, p),
                    Offset(w - p - s, p),
                    Offset(p, h - p - s),
                    Offset(w - p - s, h - p - s),
                ).forEach { origin ->
                    drawRoundRect(
                        color = color,
                        topLeft = origin,
                        size = Size(s, s),
                        cornerRadius = CornerRadius(r, r),
                        style = Stroke(sw),
                    )
                }
            }
            TgTabIcon.Stats -> {
                drawLine(color, Offset(w * 0.25f, h * 0.74f), Offset(w * 0.25f, h * 0.42f), sw, StrokeCap.Round)
                drawLine(color, Offset(w * 0.50f, h * 0.74f), Offset(w * 0.50f, h * 0.24f), sw, StrokeCap.Round)
                drawLine(color, Offset(w * 0.75f, h * 0.74f), Offset(w * 0.75f, h * 0.50f), sw, StrokeCap.Round)
            }
            TgTabIcon.Me -> {
                drawCircle(color = color, radius = w * 0.16f, center = Offset(w / 2f, h * 0.34f), style = Stroke(sw))
                drawArc(
                    color = color,
                    startAngle = 200f,
                    sweepAngle = 140f,
                    useCenter = false,
                    topLeft = Offset(w * 0.14f, h * 0.52f),
                    size = Size(w * 0.72f, h * 0.60f),
                    style = Stroke(sw),
                )
            }
        }
    }
}

@Composable
private fun TgTabBarSample() {
    var selected by remember { mutableStateOf(0) }
    TgTabBar(
        items = listOf(
            TgTabItem("今日", TgTabIcon.Today),
            TgTabItem("应用", TgTabIcon.Apps),
            TgTabItem("统计", TgTabIcon.Stats),
            TgTabItem("我的", TgTabIcon.Me),
        ),
        selectedIndex = selected,
        onSelect = { selected = it },
    )
}

@Preview(name = "TabBar · 深色", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TgTabBarDarkPreview() {
    TgPreviewSurface(darkTheme = true) { TgTabBarSample() }
}

@Preview(name = "TabBar · 浅色", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun TgTabBarLightPreview() {
    TgPreviewSurface(darkTheme = false) { TgTabBarSample() }
}
