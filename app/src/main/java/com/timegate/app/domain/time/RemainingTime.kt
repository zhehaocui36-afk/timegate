package com.timegate.app.domain.time

/**
 * 计时口径：只存时间戳，不存「剩余秒数」（R-52 / R-54）。
 *
 * 规则：
 * - 计时只记录开始 / 离开 / 结束三个时间戳（毫秒，Long）。
 * - 剩余时长每次按「承诺时长 − 实际经过时间」实时计算，数据层不存在「剩余秒数」字段。
 * - 息屏 / 切后台 / 进程被系统回收期间，倒计时**照常流逝**：回前台按时间戳补算，
 *   误差 ≤ 1 秒（用毫秒减法向下取整到秒，误差严格 < 1 秒）。
 * - 剩余不会为负，归零即「到时」。
 *
 * 本类位于 `domain/time`，纯 Kotlin，零 `import android.*`。
 */
object RemainingTime {

    /**
     * 计算已经过的秒数（向下取整）。
     *
     * @param startedAtMillis 开始时间戳（epoch millis）。
     * @param nowMillis 当前时刻（epoch millis）。
     * @return 经过的整秒数；`nowMillis` 早于开始时刻时返回 0。
     */
    fun elapsedSeconds(startedAtMillis: Long, nowMillis: Long): Long {
        val elapsedMillis = nowMillis - startedAtMillis
        return (elapsedMillis / 1000L).coerceAtLeast(0L)
    }

    /**
     * 计算剩余秒数。
     *
     * 剩余 = `plannedSeconds − elapsedSeconds`，向下取整到秒，误差 < 1 秒，不会为负。
     *
     * @param plannedSeconds 承诺总时长（秒）。
     * @param startedAtMillis 开始时间戳（epoch millis）。
     * @param nowMillis 当前时刻（epoch millis）。
     * @return 剩余秒数（0 表示已到时）。
     */
    fun remainingSeconds(plannedSeconds: Long, startedAtMillis: Long, nowMillis: Long): Long {
        val elapsed = elapsedSeconds(startedAtMillis, nowMillis)
        return (plannedSeconds - elapsed).coerceAtLeast(0L)
    }
}
