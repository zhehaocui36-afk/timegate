package com.timegate.app.domain.time

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * UT-02：时间戳计时补算（R-52 / R-54）。
 *
 * 判定标准：回前台后剩余 = 承诺 − 实际经过，误差 ≤ 1 秒。
 */
class RemainingTimeTest {

    @Test
    fun `剩余等于承诺减经过`() {
        // 承诺 300s，开始 0ms，现在 100_000ms（=100s）→ 剩余 200s
        assertEquals(200L, RemainingTime.remainingSeconds(300L, 0L, 100_000L))
    }

    @Test
    fun `毫秒向下取整_误差小于1秒`() {
        // 经过 100_999ms，向下取整为 100s，剩余 200s；被丢弃的 999ms < 1s
        assertEquals(200L, RemainingTime.remainingSeconds(300L, 0L, 100_999L))
    }

    @Test
    fun `误差严格小于1秒`() {
        val planned = 300L
        val started = 0L
        val now = 100_999L
        val remaining = RemainingTime.remainingSeconds(planned, started, now)
        // 精确剩余 = 300 - 100.999 = 199.001；向下取整 199 会差 1，向上 200 差 0.999。
        // 校验：无论取整方向，误差绝对值 < 1 秒。
        val preciseRemainingMillis = planned * 1000 - (now - started)
        val errorMillis = kotlin.math.abs(remaining * 1000 - preciseRemainingMillis)
        assertTrue("误差应 < 1000ms，实际 $errorMillis", errorMillis < 1000)
    }

    @Test
    fun `到点归零_不为负`() {
        // 承诺 60s，经过 120s → 剩余 0，不为负
        assertEquals(0L, RemainingTime.remainingSeconds(60L, 0L, 120_000L))
        assertEquals(0L, RemainingTime.remainingSeconds(60L, 0L, 60_000L)) // 恰好到点
    }

    @Test
    fun `息屏10分钟_回来剩余少10分钟`() {
        // 承诺 20 分钟（1200s），开始 0ms，息屏 10 分钟后（600_000ms）回前台 → 剩余 600s
        assertEquals(600L, RemainingTime.remainingSeconds(1200L, 0L, 600_000L))
    }

    @Test
    fun `elapsedSeconds_向下取整`() {
        assertEquals(100L, RemainingTime.elapsedSeconds(0L, 100_999L))
        assertEquals(0L, RemainingTime.elapsedSeconds(0L, 999L))
        assertEquals(1L, RemainingTime.elapsedSeconds(0L, 1000L))
    }

    @Test
    fun `elapsedSeconds_时钟回拨不为负`() {
        // now 早于 started（系统校时回拨）→ 经过时间不得为负
        assertEquals(0L, RemainingTime.elapsedSeconds(500_000L, 100_000L))
    }
}
