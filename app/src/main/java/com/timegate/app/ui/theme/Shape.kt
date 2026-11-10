package com.timegate.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * 闸 · TimeGate — 圆角 token（S05）
 *
 * demo `:root` 阶梯：--r-xs 8 / --r-sm 10 / --r-md 12 / --r-lg 16 / --r-xl 22 / --r-full 999。
 * 弹窗顶部 28（.overlay--sheet .panel）。iOS 分组卡片是 10~12，不是 24。
 */

@Immutable
data class TimeGateShapeTokens(
    val xs: Shape = RoundedCornerShape(8.dp),
    val sm: Shape = RoundedCornerShape(10.dp),
    val md: Shape = RoundedCornerShape(12.dp),
    val lg: Shape = RoundedCornerShape(16.dp),
    val xl: Shape = RoundedCornerShape(22.dp),
    /** 胶囊：用 CircleShape 表达 --r-full 999，不需要魔法数。 */
    val full: Shape = CircleShape,
    val sheetTop: Shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    /** 悬浮胶囊导航：圆角 = 高度 / 2，等价于 50% 圆角。 */
    val bar: Shape = RoundedCornerShape(percent = 50),
) {
    companion object {
        val Default = TimeGateShapeTokens()
    }
}

/** 交给 MaterialTheme，槽位与 xs/sm/md/lg/xl 对齐。 */
val TimeGateShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(22.dp),
)

val LocalTimeGateShapes = staticCompositionLocalOf { TimeGateShapeTokens.Default }
