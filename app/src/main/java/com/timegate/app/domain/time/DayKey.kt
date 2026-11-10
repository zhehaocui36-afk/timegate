package com.timegate.app.domain.time

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 时间口径：凌晨 04:00 换日（R-70 / R-71）。
 *
 * 规则：
 * - `day_key` 一律用「本地年月日」拼成 `YYYY-MM-DD`。
 * - 跨天以本地时间 04:00 为界：04:00 前归前一日，04:00 起归当日。
 * - 公式：`dayKey = 本地日期(ts − 4h)`（对 epoch millis 直接减 4 小时绝对时长，再转本地日期），
 *   与 Web 原型 `docs/archive/HANDOFF.md` E.2 及 `demo/index.html` 的实现保持一致。
 * - 时区取传入的 [zone]（默认系统默认时区），**不硬编码东八区**。
 *
 * 禁止：`toISOString().slice(0, 10)` 一类 UTC 截断——那会让东八区整体偏 8 小时（R-71）。
 *
 * 本类位于 `domain/time`，纯 Kotlin，零 `import android.*`，可在 JVM 单元测试中直接运行。
 */
object DayKey {

    /** 换日时刻：本地 04:00。 */
    const val RESET_HOUR: Int = 4

    /** 4 小时对应的毫秒数。 */
    private const val RESET_OFFSET_MILLIS: Long = RESET_HOUR * 60L * 60L * 1000L

    /** ISO-8601 本地日期格式：`uuuu-MM-dd`（`uuuu` 为 proleptic year，避免 year-of-era 的 0 年歧义）。 */
    private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd")

    /**
     * 计算某个时间戳（epoch millis）所属的 `day_key`。
     *
     * @param epochMillis 自 UTC 1970-01-01 起的毫秒数。
     * @param zone 用于拆解「本地年月日」的时区；默认系统默认时区。
     * @return `YYYY-MM-DD` 字符串。例：东八区 2026-11-10 03:50 → `"2026-11-09"`；04:10 → `"2026-11-10"`。
     */
    fun of(epochMillis: Long, zone: ZoneId = ZoneId.systemDefault()): String {
        val shifted = Instant.ofEpochMilli(epochMillis - RESET_OFFSET_MILLIS)
        return shifted.atZone(zone).toLocalDate().format(FORMATTER)
    }

    /**
     * 计算「现在」所属的 `day_key`。
     *
     * @param zone 时区；默认系统默认时区。
     * @param nowMillis 当前时刻，默认系统当前毫秒。测试可注入固定值。
     */
    fun today(zone: ZoneId = ZoneId.systemDefault(), nowMillis: Long = System.currentTimeMillis()): String =
        of(nowMillis, zone)
}
