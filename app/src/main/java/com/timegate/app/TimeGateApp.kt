package com.timegate.app

import android.app.Application

/**
 * 应用入口占位（S04）。
 *
 * 当前为空实现，不做任何初始化、不注册任何组件。
 * 后续 S06（数据层）如需 Application 级依赖注入，由对应 owner 在此注册；
 * 若有跨层初始化需求，先按 OWNERSHIP.md §5 走变更请求。
 */
class TimeGateApp : Application()
