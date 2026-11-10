package com.timegate.app.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.timegate.app.ui.theme.TimeGateTheme

/**
 * 空状态：demo「今天还没有记录」「还没有受管应用」等零数据场景。
 * 纯中性色，不引入 tint（遵守 tint 5 处清单）。
 * 文字与操作区都随内容撑高，字号 200% 不截断。
 */
@Composable
fun TgEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    action: (@Composable () -> Unit)? = null,
) {
    val colors = TimeGateTheme.colors
    val type = TimeGateTheme.type

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Canvas(modifier = Modifier.size(56.dp)) {
            val r = size.minDimension / 2f
            drawCircle(color = colors.surface3, radius = r - 2.dp.toPx())
            drawCircle(
                color = colors.ringTrack,
                radius = r - 8.dp.toPx(),
                style = Stroke(width = 2.dp.toPx()),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            color = colors.textPrimary,
            fontSize = type.bodyLarge,
            textAlign = TextAlign.Center,
        )
        if (message != null) {
            Text(
                text = message,
                color = colors.textPrimary,
                fontSize = type.bodySmall,
                textAlign = TextAlign.Center,
            )
        }
        if (action != null) {
            Spacer(modifier = Modifier.height(8.dp))
            action()
        }
    }
}

@Preview(name = "EmptyState · 深色", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun TgEmptyStateDarkPreview() {
    TgPreviewSurface(darkTheme = true) {
        TgEmptyState(
            title = "今天还没有记录",
            message = "点一个受管应用，就会开始记下你花的时间。",
            action = { TgButton(text = "挑一个应用", onClick = { }, size = TgButtonSize.Small) },
        )
    }
}

@Preview(name = "EmptyState · 浅色", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
private fun TgEmptyStateLightPreview() {
    TgPreviewSurface(darkTheme = false) {
        TgEmptyState(
            title = "还没有受管应用",
            message = "去「应用」里加一个，先管住最耗时的那个。",
            action = { TgButton(text = "去添加", onClick = { }, size = TgButtonSize.Small) },
        )
    }
}
