package com.timegate.app.domain.time

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * UT-01：day_key 换日与日期拼装（R-70 / R-71）。
 *
 * 覆盖：03:50 归前日、04:10 归当日、03:59/04:00 边界、东八区 00:30 归当天、
 * 跨年、闰年、至少两个时区。
 */
class DayKeyTest {

    // 用 ZonedDateTime 构造「本地时间点 → epoch millis」，避免手算时间戳。
    private fun epoch(
        year: Int, month: Int, day: Int,
        hour: Int, minute: Int,
        zone: String,
    ): Long = ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneId.of(zone))
        .toInstant()
        .toEpochMilli()

    private val shanghai = ZoneId.of("Asia/Shanghai")

    // ---- R-70：04:00 换日 ----

    @Test
    fun `03_50 归前一日`() {
        val ts = epoch(2026, 11, 10, 3, 50, "Asia/Shanghai")
        assertEquals("2026-11-09", DayKey.of(ts, shanghai))
    }

    @Test
    fun `04_10 归当日`() {
        val ts = epoch(2026, 11, 10, 4, 10, "Asia/Shanghai")
        assertEquals("2026-11-10", DayKey.of(ts, shanghai))
    }

    @Test
    fun `03_59 归前一日_边界`() {
        val ts = epoch(2026, 11, 10, 3, 59, "Asia/Shanghai")
        assertEquals("2026-11-09", DayKey.of(ts, shanghai))
    }

    @Test
    fun `04_00 归当日_边界`() {
        val ts = epoch(2026, 11, 10, 4, 0, "Asia/Shanghai")
        assertEquals("2026-11-10", DayKey.of(ts, shanghai))
    }

    // ---- R-71：本地拼日期，禁止 UTC 截断 ----

    @Test
    fun `东八区 00_30 按4点换日归前一日`() {
        // ⚠️ 口径裁决：R-71 字面写「东八区 00:30 归当天」，与 R-70「04:00 前归前一日」冲突
        //（00:30 < 04:00，按 4:00 换日必须归前一日）。
        // 本实现以 HANDOFF E.2（唯一权威）+ demo `dayKey = 本地日期(ts − 4h)` + 任务约束 3 为准：
        // 东八区 00:30 减 4h = 前一日 20:30 → 归前一日。
        val ts = epoch(2026, 11, 10, 0, 30, "Asia/Shanghai")
        assertEquals("2026-11-09", DayKey.of(ts, shanghai))
    }

    @Test
    fun `本地日期拼装_不用UTC截断_东八区06点`() {
        // 东八区 06:00 = UTC 前一日 22:00。
        // 本地日期(06:00 − 4h = 02:00) = 当天 2026-11-10；
        // 若误用 UTC 截断（toISOString().slice(0,10)）会得到 2026-11-09（UTC 的昨天）。
        // 此用例真正体现「本地拼日期、禁止 UTC 截断」（R-71）。
        val ts = epoch(2026, 11, 10, 6, 0, "Asia/Shanghai")
        assertEquals("2026-11-10", DayKey.of(ts, shanghai))
    }

    @Test
    fun `跨年_1月1日凌晨03_50 归上一年最后一天`() {
        val ts = epoch(2027, 1, 1, 3, 50, "Asia/Shanghai")
        assertEquals("2026-12-31", DayKey.of(ts, shanghai))
    }

    @Test
    fun `跨年_1月1日凌晨04_00 归新年第一天`() {
        val ts = epoch(2027, 1, 1, 4, 0, "Asia/Shanghai")
        assertEquals("2027-01-01", DayKey.of(ts, shanghai))
    }

    // ---- 闰年 ----

    @Test
    fun `闰年_2月29日凌晨03_00 归 2月28日`() {
        val ts = epoch(2024, 2, 29, 3, 0, "Asia/Shanghai")
        assertEquals("2024-02-28", DayKey.of(ts, shanghai))
    }

    @Test
    fun `闰年_2月29日05_00 归 2月29日`() {
        val ts = epoch(2024, 2, 29, 5, 0, "Asia/Shanghai")
        assertEquals("2024-02-29", DayKey.of(ts, shanghai))
    }

    @Test
    fun `闰年_3月1日凌晨02_00 归 2月29日`() {
        val ts = epoch(2024, 3, 1, 2, 0, "Asia/Shanghai")
        assertEquals("2024-02-29", DayKey.of(ts, shanghai))
    }

    @Test
    fun `非闰年_3月1日凌晨02_00 归 2月28日`() {
        val ts = epoch(2023, 3, 1, 2, 0, "Asia/Shanghai")
        assertEquals("2023-02-28", DayKey.of(ts, shanghai))
    }

    // ---- 至少两个时区（不硬编码东八区）----

    @Test
    fun `时区_UTC 03_50 归前一日`() {
        val ts = epoch(2026, 11, 10, 3, 50, "UTC")
        assertEquals("2026-11-09", DayKey.of(ts, ZoneId.of("UTC")))
    }

    @Test
    fun `时区_纽约 03_50 归前一日`() {
        val ts = epoch(2026, 11, 10, 3, 50, "America/New_York")
        assertEquals("2026-11-09", DayKey.of(ts, ZoneId.of("America/New_York")))
    }

    @Test
    fun `时区_檀香山 23_30 仍归当日`() {
        // 檀香山（UTC-10）本地 23:30，减 4h 后是 19:30，仍属当日。
        val ts = epoch(2026, 11, 10, 23, 30, "Pacific/Honolulu")
        assertEquals("2026-11-10", DayKey.of(ts, ZoneId.of("Pacific/Honolulu")))
    }

    @Test
    fun `同一时刻_不同时区 day_key 可不同`() {
        // 东八区 12:00 = 纽约 23:00（前一晚）。同一 epoch 在两地产出不同 day_key，
        // 证明 day_key 是「本地」日期而非固定 UTC 日期。
        val ts = epoch(2026, 11, 10, 12, 0, "Asia/Shanghai")
        assertEquals("2026-11-10", DayKey.of(ts, shanghai))
        assertEquals("2026-11-09", DayKey.of(ts, ZoneId.of("America/New_York")))
    }
}
