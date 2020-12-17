package com.abnerming.vlibrary.refresh

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
abstract class RefreshListenerAdapter : PullListener {
    override fun onPullingDown(
        refreshLayout: TwinklingRefreshLayout?,
        fraction: Float
    ) {
    }

    override fun onPullingUp(
        refreshLayout: TwinklingRefreshLayout?,
        fraction: Float
    ) {
    }

    override fun onPullDownReleasing(
        refreshLayout: TwinklingRefreshLayout?,
        fraction: Float
    ) {
    }

    override fun onPullUpReleasing(
        refreshLayout: TwinklingRefreshLayout?,
        fraction: Float
    ) {
    }

    override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {}
    override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {}
    override fun onFinishRefresh() {}
    override fun onFinishLoadMore() {}
    override fun onRefreshCanceled() {}
    override fun onLoadmoreCanceled() {}
}