package com.abnerming.vlibrary.view

import android.view.View
import com.abnerming.vlibrary.refresh.IHeaderView
import com.abnerming.vlibrary.refresh.OnAnimEndListener

/**
 *AUTHOR:AbnerMing
 *DATA:2020/12/16
 *INTRODUCE:下拉刷新View
 */
abstract class VHeaderView : IHeaderView {
    override fun onFinish(animEndListener: OnAnimEndListener?) {
    }

    override fun onPullingDown(fraction: Float, maxHeadHeight: Float, headHeight: Float) {
    }

    override fun onPullReleasing(fraction: Float, maxHeadHeight: Float, headHeight: Float) {
    }

    override fun reset() {
    }

    override fun startAnim(maxHeadHeight: Float, headHeight: Float) {
    }
}