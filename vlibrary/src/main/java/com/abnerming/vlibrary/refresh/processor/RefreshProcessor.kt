package com.abnerming.vlibrary.refresh.processor

import android.view.MotionEvent
import android.view.ViewConfiguration
import com.abnerming.vlibrary.refresh.TwinklingRefreshLayout.CoContext
import com.abnerming.vlibrary.refresh.utils.ScrollingUtil.isViewToBottom
import com.abnerming.vlibrary.refresh.utils.ScrollingUtil.isViewToTop

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
class RefreshProcessor(processor: CoContext?) : IDecorator {
    protected var cp: CoContext
    private var mTouchX = 0f
    private var mTouchY = 0f
    private var intercepted = false
    private var willAnimHead = false
    private var willAnimBottom = false
    private var downEventSent = false
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> {
                downEventSent = false
                intercepted = false
                mTouchX = ev.x
                mTouchY = ev.y
                if (cp.isEnableKeepIView) {
                    if (!cp.isRefreshing) {
                        cp.isPrepareFinishRefresh = false
                    }
                    if (!cp.isLoadingMore) {
                        cp.isPrepareFinishLoadMore = false
                    }
                }
                cp.dispatchTouchEventSuper(ev)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                mLastMoveEvent = ev
                val dx = ev.x - mTouchX
                var dy = ev.y - mTouchY
                if (!intercepted && Math.abs(dx) <= Math.abs(dy) && Math.abs(
                        dy
                    ) > cp.touchSlop
                ) { //滑动允许最大角度为45度
                    if (dy > 0 && isViewToTop(
                            cp.targetView,
                            cp.touchSlop
                        ) && cp.allowPullDown()
                    ) {
                        cp.setStatePTD()
                        mTouchX = ev.x
                        mTouchY = ev.y
                        sendCancelEvent()
                        intercepted = true
                        return true
                    } else if (dy < 0 && isViewToBottom(
                            cp.targetView,
                            cp.touchSlop
                        ) && cp.allowPullUp()
                    ) {
                        cp.setStatePBU()
                        mTouchX = ev.x
                        mTouchY = ev.y
                        intercepted = true
                        sendCancelEvent()
                        return true
                    }
                }
                if (intercepted) {
                    if (cp.isRefreshVisible || cp.isLoadingVisible) {
                        return cp.dispatchTouchEventSuper(ev)
                    }
                    if (!cp.isPrepareFinishRefresh && cp.isStatePTD) {
                        if (dy < -cp.touchSlop || !isViewToTop(
                                cp.targetView,
                                cp.touchSlop
                            )
                        ) {
                            cp.dispatchTouchEventSuper(ev)
                        }
                        dy = Math.min(cp.maxHeadHeight * 2, dy)
                        dy = Math.max(0f, dy)
                        cp.animProcessor.scrollHeadByMove(dy)
                    } else if (!cp.isPrepareFinishLoadMore && cp.isStatePBU) {
                        //加载更多的动作
                        if (dy > cp.touchSlop || !isViewToBottom(
                                cp.targetView,
                                cp.touchSlop
                            )
                        ) {
                            cp.dispatchTouchEventSuper(ev)
                        }
                        dy = Math.max(-cp.maxBottomHeight * 2.toFloat(), dy)
                        dy = Math.min(0f, dy)
                        cp.animProcessor.scrollBottomByMove(Math.abs(dy))
                    }
                    if (dy == 0f && !downEventSent) {
                        downEventSent = true
                        sendDownEvent()
                    }
                    return true
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> if (intercepted) {
                if (cp.isStatePTD) {
                    willAnimHead = true
                } else if (cp.isStatePBU) {
                    willAnimBottom = true
                }
                intercepted = false
                return true
            }
        }
        return cp.dispatchTouchEventSuper(ev)
    }

    private var mLastMoveEvent: MotionEvent? = null

    //发送cancel事件解决selection问题
    private fun sendCancelEvent() {
        if (mLastMoveEvent == null) {
            return
        }
        val last: MotionEvent = mLastMoveEvent as MotionEvent
        val e = MotionEvent.obtain(
            last.downTime,
            last.eventTime + ViewConfiguration.getLongPressTimeout(),
            MotionEvent.ACTION_CANCEL,
            last.x,
            last.y,
            last.metaState
        )
        cp.dispatchTouchEventSuper(e)
    }

    private fun sendDownEvent() {
        val last = mLastMoveEvent
        val e = MotionEvent.obtain(
            last!!.downTime,
            last.eventTime,
            MotionEvent.ACTION_DOWN,
            last.x,
            last.y,
            last.metaState
        )
        cp.dispatchTouchEventSuper(e)
    }

    override fun interceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun dealTouchEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onFingerDown(ev: MotionEvent?) {}
    override fun onFingerUp(ev: MotionEvent?, isFling: Boolean) {
        if (!isFling && willAnimHead) {
            cp.animProcessor.dealPullDownRelease()
        }
        if (!isFling && willAnimBottom) {
            cp.animProcessor.dealPullUpRelease()
        }
        willAnimHead = false
        willAnimBottom = false
    }

    override fun onFingerScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float,
        velocityX: Float,
        velocityY: Float
    ) {
        //手指在屏幕上滚动，如果此时正处在刷新状态，可隐藏
        val mTouchSlop = cp.touchSlop
        if (cp.isRefreshVisible && distanceY >= mTouchSlop && !cp.isOpenFloatRefresh) {
            cp.animProcessor.animHeadHideByVy(velocityY.toInt())
        }
        if (cp.isLoadingVisible && distanceY <= -mTouchSlop) {
            cp.animProcessor.animBottomHideByVy(velocityY.toInt())
        }
    }

    override fun onFingerFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ) {
    }

    init {
        if (processor == null) throw NullPointerException("The coprocessor can not be null.")
        cp = processor
    }
}