package com.timegate.app.domain.time

import java.time.ZoneId

/** 生产环境时钟：读系统当前时间与系统默认时区。 */
class SystemClock : Clock {
    override fun nowMillis(): Long = System.currentTimeMillis()
    override fun zone(): ZoneId = ZoneId.systemDefault()
}
