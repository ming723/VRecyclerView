package com.abnerming.vlibrary.refresh.processor

import android.os.Handler
import android.os.Message
import android.view.MotionEvent
import com.abnerming.vlibrary.refresh.TwinklingRefreshLayout.CoContext
import com.abnerming.vlibrary.refresh.utils.ScrollingUtil.isViewToBottom
import com.abnerming.vlibrary.refresh.utils.ScrollingUtil.isViewToTop

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
class OverScrollDecorator(processor: CoContext?, decorator1: IDecorator?) :
    Decorator(processor, decorator1) {
    //主要为了监测Fling的动作,实现越界回弹
    private var mVelocityY = 0f
    private var cur_delay_times = 0 //当前计算次数
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return decorator != null && decorator.dispatchTouchEvent(ev)
    }

    override fun interceptTouchEvent(ev: MotionEvent?): Boolean {
        return decorator != null && decorator.interceptTouchEvent(ev)
    }

    override fun dealTouchEvent(e: MotionEvent?): Boolean {
        return decorator != null && decorator.dealTouchEvent(e)
    }

    private var preventTopOverScroll = false
    private var preventBottomOverScroll = false
    private var checkOverScroll = false
    override fun onFingerDown(ev: MotionEvent?) {
        if (decorator != null) decorator.onFingerDown(ev)
        preventTopOverScroll = isViewToTop(cp.targetView, cp.touchSlop)
        preventBottomOverScroll =
            isViewToBottom(cp.targetView, cp.touchSlop)
    }

    override fun onFingerUp(ev: MotionEvent?, isFling: Boolean) {
        if (decorator != null) {
            decorator.onFingerUp(ev, checkOverScroll && isFling)
        }
        checkOverScroll = false
    }

    override fun onFingerScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float,
        velocityX: Float,
        velocityY: Float
    ) {
        if (decorator != null) decorator.onFingerScroll(
            e1,
            e2,
            distanceX,
            distanceY,
            velocityX,
            velocityY
        )
    }

    override fun onFingerFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ) {
        if (decorator != null) decorator.onFingerFling(e1, e2, velocityX, velocityY)
        //fling时才触发OverScroll，获取速度并采用演示策略估算View是否滚动到边界
        if (!cp.enableOverScroll()) return
        val dy = (e2!!.y - e1!!.y).toInt()
        if (dy < -cp.touchSlop && preventBottomOverScroll) return  //控件滚动在底部且向上fling
        if (dy > cp.touchSlop && preventTopOverScroll) return  //控件滚动在顶部且向下fling
        mVelocityY = velocityY
        if (Math.abs(mVelocityY) >= OVER_SCROLL_MIN_VX) {
            mHandler.sendEmptyMessage(MSG_START_COMPUTE_SCROLL)
            checkOverScroll = true
        } else {
            mVelocityY = 0f
            cur_delay_times = ALL_DELAY_TIMES
        }
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val mTouchSlop = cp.touchSlop
            when (msg.what) {
                MSG_START_COMPUTE_SCROLL -> {
                    cur_delay_times = -1 //这里没有break,写作-1方便计数
                    cur_delay_times++
                    val mChildView = cp.targetView
                    if (cp.allowOverScroll()) {
                        if (mVelocityY >= OVER_SCROLL_MIN_VX) {
                            if (isViewToTop(mChildView, mTouchSlop)) {
                                cp.animProcessor.animOverScrollTop(mVelocityY, cur_delay_times)
                                mVelocityY = 0f
                                cur_delay_times = ALL_DELAY_TIMES
                            }
                        } else if (mVelocityY <= -OVER_SCROLL_MIN_VX) {
                            if (isViewToBottom(mChildView, mTouchSlop)) {
                                cp.animProcessor
                                    .animOverScrollBottom(mVelocityY, cur_delay_times)
                                mVelocityY = 0f
                                cur_delay_times = ALL_DELAY_TIMES
                            }
                        }
                    }

                    //计算未超时，继续发送消息并循环计算
                    if (cur_delay_times < ALL_DELAY_TIMES) sendEmptyMessageDelayed(
                        MSG_CONTINUE_COMPUTE_SCROLL,
                        10
                    )
                }
                MSG_CONTINUE_COMPUTE_SCROLL -> {
                    cur_delay_times++
                    val mChildView = cp.targetView
                    if (cp.allowOverScroll()) {
                        if (mVelocityY >= OVER_SCROLL_MIN_VX) {
                            if (isViewToTop(mChildView, mTouchSlop)) {
                                cp.animProcessor.animOverScrollTop(mVelocityY, cur_delay_times)
                                mVelocityY = 0f
                                cur_delay_times = ALL_DELAY_TIMES
                            }
                        } else if (mVelocityY <= -OVER_SCROLL_MIN_VX) {
                            if (isViewToBottom(mChildView, mTouchSlop)) {
                                cp.animProcessor
                                    .animOverScrollBottom(mVelocityY, cur_delay_times)
                                mVelocityY = 0f
                                cur_delay_times = ALL_DELAY_TIMES
                            }
                        }
                    }
                    if (cur_delay_times < ALL_DELAY_TIMES) sendEmptyMessageDelayed(
                        MSG_CONTINUE_COMPUTE_SCROLL,
                        10
                    )
                }
                MSG_STOP_COMPUTE_SCROLL -> cur_delay_times =
                    ALL_DELAY_TIMES
            }
        }
    }

    companion object {
        //满足越界的手势的最低速度(默认3000)
        private const val OVER_SCROLL_MIN_VX = 3000

        //针对View的延时策略
        private const val MSG_START_COMPUTE_SCROLL = 0 //开始计算
        private const val MSG_CONTINUE_COMPUTE_SCROLL = 1 //继续计算
        private const val MSG_STOP_COMPUTE_SCROLL = 2 //停止计算
        private const val ALL_DELAY_TIMES = 60 //10ms计算一次,总共计算20次
    }
}