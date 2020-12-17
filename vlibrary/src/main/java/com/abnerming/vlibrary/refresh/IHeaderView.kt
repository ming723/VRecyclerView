package com.abnerming.vlibrary.refresh

import android.view.View

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
interface IHeaderView {
    fun getView(): View?

    /**
     * 下拉准备刷新动作
     *
     * @param fraction      当前下拉高度与总高度的比
     * @param maxHeadHeight
     * @param headHeight
     */
    fun onPullingDown(
        fraction: Float,
        maxHeadHeight: Float,
        headHeight: Float
    )

    /**
     * 下拉释放过程
     *
     * @param fraction
     * @param maxHeadHeight
     * @param headHeight
     */
    fun onPullReleasing(
        fraction: Float,
        maxHeadHeight: Float,
        headHeight: Float
    )

    fun startAnim(maxHeadHeight: Float, headHeight: Float)
    fun onFinish(animEndListener: OnAnimEndListener?)

    /**
     * 用于在必要情况下复位View，清除动画
     */
    fun reset()
}