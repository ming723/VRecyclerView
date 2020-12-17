package com.abnerming.vlibrary.refresh

import android.view.View

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
interface IBottomView {
    fun getView(): View?

    /**
     * 上拉准备加载更多的动作
     *
     * @param fraction      上拉高度与Bottom总高度之比
     * @param maxBottomHeight 底部部可拉伸最大高度
     * @param bottomHeight    底部高度
     */
    fun onPullingUp(
        fraction: Float,
        maxBottomHeight: Float,
        bottomHeight: Float
    )

    fun startAnim(maxBottomHeight: Float, bottomHeight: Float)

    /**
     * 上拉释放过程
     */
    fun onPullReleasing(
        fraction: Float,
        maxBottomHeight: Float,
        bottomHeight: Float
    )

    fun onFinish()
    fun reset()
}