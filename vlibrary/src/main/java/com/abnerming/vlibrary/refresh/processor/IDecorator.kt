package com.abnerming.vlibrary.refresh.processor

import android.view.MotionEvent

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
interface IDecorator {
    fun dispatchTouchEvent(ev: MotionEvent?): Boolean
    fun interceptTouchEvent(ev: MotionEvent?): Boolean
    fun dealTouchEvent(ev: MotionEvent?): Boolean
    fun onFingerDown(ev: MotionEvent?)
    fun onFingerUp(ev: MotionEvent?, isFling: Boolean)
    fun onFingerScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float,
        velocityX: Float,
        velocityY: Float
    )

    fun onFingerFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    )
}