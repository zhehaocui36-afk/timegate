package com.timegate.app.domain.time

import java.time.ZoneId

/**
 * 时间源抽象（供 S09 计时、S08 检测使用）。
 *
 * 所有「现在」与「时区」都必须经 [Clock] 取得，禁止在业务代码里直接调用
 * `System.currentTimeMillis()` / `ZoneId.systemDefault()`——这样 S09 的剩余时间计算、
 * S12 的单元测试都能注入 Fake 时钟，不依赖真实墙钟。
 */
interface Clock {

    /** 当前时刻，epoch millis（毫秒）。 */
    fun nowMillis(): Long

    /** 当前时区（系统默认，不硬编码）。 */
    fun zone(): ZoneId
}
