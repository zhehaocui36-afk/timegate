package com.timegate.app.data.local

import androidx.room.TypeConverter

/**
 * Room 类型转换器。
 *
 * 仅用于枚举 [GateOutcome] ↔ 存库字符串。日期一律 `day_key TEXT`（无 `Date`），
 * 时间戳一律 `Long`，无需其他转换。
 */
class Converters {

    @TypeConverter
    fun gateOutcomeToString(outcome: GateOutcome): String = outcome.name

    @TypeConverter
    fun stringToGateOutcome(value: String): GateOutcome = GateOutcome.valueOf(value)
}
