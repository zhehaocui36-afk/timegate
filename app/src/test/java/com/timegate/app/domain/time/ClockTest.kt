package com.timegate.app.domain.time

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId

/**
 * Clock 抽象的可替换性：业务代码应面向 [Clock] 编程，测试可注入 Fake。
 */
class ClockTest {

    private class FakeClock(
        private val millis: Long,
        private val zone: ZoneId,
    ) : Clock {
        override fun nowMillis(): Long = millis
        override fun zone(): ZoneId = zone
    }

    @Test
    fun `Fake 时钟可注入固定时间与时区`() {
        val clock = FakeClock(millis = 1_000_000L, zone = ZoneId.of("Asia/Shanghai"))
        assertEquals(1_000_000L, clock.nowMillis())
        assertEquals(ZoneId.of("Asia/Shanghai"), clock.zone())
    }

    @Test
    fun `RemainingTime 可配合注入时钟计算`() {
        val startedAt = 1_000_000L
        val clock = FakeClock(millis = 1_300_000L, zone = ZoneId.of("Asia/Shanghai"))
        // 经过 300s，承诺 500s → 剩余 200s
        assertEquals(200L, RemainingTime.remainingSeconds(500L, startedAt, clock.nowMillis()))
    }
}
