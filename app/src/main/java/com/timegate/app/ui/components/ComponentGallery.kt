package com.timegate.app.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timegate.app.ui.theme.TimeGateTheme

/**
 * S05 组件预览清单（**预览入口，不是第 11 个组件**）。
 *
 * 把 10 个组件竖排摆一页，配合下面 4 组 @Preview 覆盖四种状态：
 * 深色 / 浅色 / 字号 200% / 减弱动效。逐项预览见每个组件文件里各自的深/浅 @Preview。
 * 本文件只用于 IDE 预览与人工走查，页面层（S07/S09/S10）不消费它。
 */
@Composable
private fun TgSectionLabel(text: String) {
    val colors = TimeGateTheme.colors
    val type = TimeGateTheme.type
    Text(
        text = text,
        color = colors.textPrimary,
        fontSize = type.bodySmall,
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
fun TgComponentGallery(modifier: Modifier = Modifier) {
    val colors = TimeGateTheme.colors
    val type = TimeGateTheme.type
    var switchOn by remember { mutableStateOf(true) }
    var chipOn by remember { mutableStateOf(true) }
    var sliderValue by remember { mutableStateOf(60f) }
    var tabIndex by remember { mutableStateOf(0) }
    var sheetVisible by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("组件库 · 10 项", color = colors.textPrimary, fontSize = type.displayMedium)
            Text(
                "深/浅 · 字号 200% · 减弱动效 三种情况都不崩",
                color = colors.textPrimary,
                fontSize = type.bodySmall,
            )

            TgSectionLabel("1 · Button")
            TgButton(text = "开始 25 分钟专注", onClick = { })
            TgButton(
                text = "现在打开（玻璃）",
                onClick = { },
                variant = TgButtonVariant.Glass,
                size = TgButtonSize.Small,
            )
            TgButton(
                text = "清空（危险）",
                onClick = { },
                variant = TgButtonVariant.Danger,
                size = TgButtonSize.Small,
            )

            TgSectionLabel("2 · Card")
            TgCard {
                Text("普通卡片 · --surface", color = colors.textPrimary, fontSize = type.bodyMedium)
            }
            TgCard(glass = true) {
                Text("玻璃卡片 · 半透明 + 顶光", color = colors.textPrimary, fontSize = type.bodyMedium)
            }

            TgSectionLabel("3 · TabBar")
            TgTabBar(
                items = listOf(
                    TgTabItem("今日", TgTabIcon.Today),
                    TgTabItem("应用", TgTabIcon.Apps),
                    TgTabItem("统计", TgTabIcon.Stats),
                    TgTabItem("我的", TgTabIcon.Me),
                ),
                selectedIndex = tabIndex,
                onSelect = { tabIndex = it },
            )

            TgSectionLabel("4 · Sheet（点按钮展开）")
            TgButton(
                text = "打开底部弹窗",
                onClick = { sheetVisible = true },
                variant = TgButtonVariant.Ghost,
            )

            TgSectionLabel("5 · Ring")
            TgRing(
                progress = 0.68f,
                centerText = "77m",
                caption = "今日已用",
                segments = listOf(
                    TgRingSegment(0.41f),
                    TgRingSegment(0.18f),
                    TgRingSegment(0.12f),
                ),
                ringSize = 160.dp,
            )

            TgSectionLabel("6 · Chip")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TgChip(text = "回消息", selected = chipOn, onClick = { chipOn = !chipOn })
                TgChip(text = "查资料", selected = !chipOn, onClick = { chipOn = !chipOn })
            }

            TgSectionLabel("7 · Switch")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("震动反馈", color = colors.textPrimary, fontSize = type.bodyMedium)
                Spacer(modifier = Modifier.width(16.dp))
                TgSwitch(checked = switchOn, onCheckedChange = { switchOn = it })
            }

            TgSectionLabel("8 · Slider")
            Text(
                text = "玻璃通透度  ${sliderValue.toInt()}",
                color = colors.textPrimary,
                fontSize = type.bodyMedium,
            )
            TgSlider(value = sliderValue, onValueChange = { sliderValue = it })

            TgSectionLabel("9 · ListRow")
            TgListRow(
                title = "抖音",
                subtitle = "今日 41 分钟 · 上限 60",
                trailing = "41 / 60",
                progress = 0.68f,
                leadingText = "抖",
                onClick = { },
            )

            TgSectionLabel("10 · EmptyState")
            TgEmptyState(title = "今天还没有记录", message = "点一个受管应用，就会开始记下你花的时间。")

            Spacer(modifier = Modifier.height(24.dp))
        }

        TgSheet(
            visible = sheetVisible,
            onDismiss = { sheetVisible = false },
            title = "这次用多久？",
            kicker = "抖音 · 今天已用 41 分钟",
        ) {
            TgChip(text = "5 分钟", selected = true, onClick = { })
            TgButton(text = "继续", onClick = { sheetVisible = false })
            TgButton(
                text = "直接退出",
                onClick = { sheetVisible = false },
                variant = TgButtonVariant.Ghost,
            )
        }
    }
}

@Preview(name = "Gallery · 深色", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun GalleryDarkPreview() {
    TimeGateTheme(darkTheme = true) { TgComponentGallery() }
}

@Preview(name = "Gallery · 浅色", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun GalleryLightPreview() {
    TimeGateTheme(darkTheme = false) { TgComponentGallery() }
}

@Preview(name = "Gallery · 字号 200%", fontScale = 2f, heightDp = 1600, showBackground = true)
@Composable
private fun GalleryLargeFontPreview() {
    TimeGateTheme(darkTheme = true) { TgComponentGallery() }
}

@Preview(name = "Gallery · 减弱动效", showBackground = true)
@Composable
private fun GalleryReducedMotionPreview() {
    TimeGateTheme(darkTheme = true, reduceMotion = true) { TgComponentGallery() }
}
