package com.timegate.app.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 闸 · TimeGate — 动效 token（S05）
 *
 * 时长来自 demo `:root`：--d1 .12s / --d2 .2s / --d3 .38s / --d4 .6s。
 * 按压 scale .972 来自 `.btn:active`；选中胶囊与弹窗上滑用 spring。
 *
 * 【prefers-reduced-motion 等价规则】
 * Android 无 CSS 的 `prefers-reduced-motion`，等价信号是系统
 * `Settings.Global.ANIMATOR_DURATION_SCALE == 0`（开发者选项「动画时长缩放 = 关闭动画」）。
 * Theme.kt 默认读该值并填入 `reduceMotion`。
 *
 * reduceMotion = true 时：
 *  - 所有 AnimationSpec 退化为 `snap()`（瞬时到位，没有中间帧 → 没有位移动画）；
 *  - `allowDisplacement = false`，组件里所有位移/缩放分量被显式乘 0；
 *  - 只保留必要的状态变化（开关仍要跳到开/关位置，但瞬间完成，不滑动）。
 * 这就是 demo `@media (prefers-reduced-motion: reduce) { .btn:active { transform:none } }` 的等价实现。
 */
@Immutable
data class TimeGateMotion(
    val reduceMotion: Boolean = false,
    val durationShort: Int = 120,
    val durationMedium: Int = 200,
    val durationLong: Int = 380,
    val durationExtra: Int = 600,
    val pressScale: Float = 0.972f,
    val sheetSlide: Dp = 120.dp,
) {
    /** 位移动画是否允许发生。组件用它把位移分量清零，而不是靠「动画很快」糊弄。 */
    val allowDisplacement: Boolean get() = !reduceMotion

    /** 按压：reduce 下瞬时且目标恒为 1f（调用方据此关掉 scale）。 */
    val pressSpec: AnimationSpec<Float>
        get() = if (reduceMotion) snap() else tween(durationShort)

    /** 位置变化（指示器、开关拇指、Sheet 上滑）：reduce 下瞬时到位。 */
    val moveSpec: AnimationSpec<Float>
        get() = if (reduceMotion) snap()
        else spring(dampingRatio = 0.85f, stiffness = 300f)

    /** 淡入淡出：reduce 下瞬时。 */
    val fadeSpec: AnimationSpec<Float>
        get() = if (reduceMotion) snap() else tween(durationMedium)

    companion object {
        val Default = TimeGateMotion()
        val Reduced = TimeGateMotion(reduceMotion = true)
    }
}

val LocalTimeGateMotion = staticCompositionLocalOf { TimeGateMotion.Default }
