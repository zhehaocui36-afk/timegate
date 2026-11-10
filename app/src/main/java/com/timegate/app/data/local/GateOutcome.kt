package com.timegate.app.data.local

/**
 * 闸门结果（R-43 / R-44）。
 *
 * - [PASSED]：放行进入计时（「还是想打开」）。
 * - 其余三类为 abandoned（闸门前离开）：不扣时长、不产生 usage_session，
 *   只让 `gate_log.abandoned` 计数 +1。
 */
enum class GateOutcome {
    /** 放行进入计时。 */
    PASSED,

    /** 时长步「直接退出」。 */
    ABANDONED_DURATION,

    /** 意图步「直接退出」。 */
    ABANDONED_INTENT,

    /** 替代步「直接退出」或「我先去做」。 */
    ABANDONED_ALTERNATIVE,
}
