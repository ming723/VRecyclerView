package com.abnerming.vlibrary.refresh

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
interface PullListener {
    /**
     * 下拉中
     *
     * @param refreshLayout
     * @param fraction
     */
    fun onPullingDown(
        refreshLayout: TwinklingRefreshLayout?,
        fraction: Float
    )

    /**
     * 上拉
     */
    fun onPullingUp(
        refreshLayout: TwinklingRefreshLayout?,
        fraction: Float
    )

    /**
     * 下拉松开
     *
     * @param refreshLayout
     * @param fraction
     */
    fun onPullDownReleasing(
        refreshLayout: TwinklingRefreshLayout?,
        fraction: Float
    )

    /**
     * 上拉松开
     */
    fun onPullUpReleasing(
        refreshLayout: TwinklingRefreshLayout?,
        fraction: Float
    )

    /**
     * 刷新中。。。
     */
    fun onRefresh(refreshLayout: TwinklingRefreshLayout?)

    /**
     * 加载更多中
     */
    fun onLoadMore(refreshLayout: TwinklingRefreshLayout?)

    /**
     * 手动调用finishRefresh或者finishLoadmore之后的回调
     */
    fun onFinishRefresh()
    fun onFinishLoadMore()

    /**
     * 正在刷新时向上滑动屏幕，刷新被取消
     */
    fun onRefreshCanceled()

    /**
     * 正在加载更多时向下滑动屏幕，加载更多被取消
     */
    fun onLoadmoreCanceled()
}