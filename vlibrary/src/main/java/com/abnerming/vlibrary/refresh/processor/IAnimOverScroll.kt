package com.abnerming.vlibrary.refresh.processor

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
interface IAnimOverScroll {
    fun animOverScrollTop(vy: Float, computeTimes: Int)
    fun animOverScrollBottom(vy: Float, computeTimes: Int)
}