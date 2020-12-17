package com.abnerming.vlibrary.view

import android.view.View
import com.abnerming.vlibrary.refresh.IBottomView

/**
 *AUTHOR:AbnerMing
 *DATA:2020/12/16
 *INTRODUCE:上拉加载View
 */
abstract class VBottomView : IBottomView {
    override fun onFinish() {
    }

    abstract override fun getView(): View?

    override fun onPullingUp(fraction: Float, maxBottomHeight: Float, bottomHeight: Float) {
    }


    override fun onPullReleasing(fraction: Float, maxBottomHeight: Float, bottomHeight: Float) {
    }

    override fun reset() {
    }

    override fun startAnim(maxBottomHeight: Float, bottomHeight: Float) {
    }

}