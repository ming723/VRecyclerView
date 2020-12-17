package com.abnerming.vlibrary.refresh.processor

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
interface IAnimRefresh {
    fun scrollHeadByMove(moveY: Float)
    fun scrollBottomByMove(moveY: Float)
    fun animHeadToRefresh()
    fun animHeadBack(isFinishRefresh: Boolean)
    fun animHeadHideByVy(vy: Int)
    fun animBottomToLoad()
    fun animBottomBack(isFinishRefresh: Boolean)
    fun animBottomHideByVy(vy: Int)
}