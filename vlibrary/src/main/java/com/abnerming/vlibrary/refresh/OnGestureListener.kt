package com.abnerming.vlibrary.refresh

import android.view.MotionEvent

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
interface OnGestureListener {
    fun onDown(ev: MotionEvent?)
    fun onScroll(
        downEvent: MotionEvent?,
        currentEvent: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    )

    fun onUp(ev: MotionEvent?, isFling: Boolean)
    fun onFling(
        downEvent: MotionEvent?,
        upEvent: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    )
}