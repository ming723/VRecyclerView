/**
 * Copyright 2015 bingoogolapple
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abnerming.vlibrary.refresh.utils

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.ScrollView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
object ScrollingUtil {
    /**
     * 用来判断是否可以下拉
     * 手指在屏幕上该方法才有效
     */
    fun canChildScrollUp(mChildView: View?): Boolean {
        if (mChildView == null) {
            return false
        }
        return if (Build.VERSION.SDK_INT < 14) {
            if (mChildView is AbsListView) {
                val absListView = mChildView
                (absListView.childCount > 0
                        && (absListView.firstVisiblePosition > 0 || absListView.getChildAt(0)
                    .top < absListView.paddingTop))
            } else {
                ViewCompat.canScrollVertically(mChildView, -1) || mChildView.scrollY > 0
            }
        } else {
            ViewCompat.canScrollVertically(mChildView, -1)
        }
    }

    /**
     * Whether it is possible for the child view of this layout to scroll down. Override this if the child view is a custom view.
     * 判断是否可以上拉
     */
    fun canChildScrollDown(mChildView: View): Boolean {
        return if (Build.VERSION.SDK_INT < 14) {
            if (mChildView is AbsListView) {
                val absListView = mChildView
                (absListView.childCount > 0
                        && (absListView.lastVisiblePosition < absListView.childCount - 1
                        || absListView.getChildAt(absListView.childCount - 1)
                    .bottom > absListView.paddingBottom))
            } else {
                ViewCompat.canScrollVertically(mChildView, 1) || mChildView.scrollY < 0
            }
        } else {
            ViewCompat.canScrollVertically(mChildView, 1)
        }
    }

    fun isScrollViewOrWebViewToTop(view: View?): Boolean {
        return view != null && view.scrollY == 0
    }

    @JvmStatic
    fun isViewToTop(view: View?, mTouchSlop: Int): Boolean {
        if (view is AbsListView) return isAbsListViewToTop(view as AbsListView?)
        return if (view is RecyclerView) isRecyclerViewToTop(view as RecyclerView?) else view != null && Math.abs(
            view.scrollY
        ) <= 2 * mTouchSlop
    }

    @JvmStatic
    fun isViewToBottom(view: View?, mTouchSlop: Int): Boolean {
        if (view is AbsListView) return isAbsListViewToBottom(view as AbsListView?)
        if (view is RecyclerView) return isRecyclerViewToBottom(view as RecyclerView?)
        if (view is WebView) return isWebViewToBottom(view as WebView?, mTouchSlop)
        return if (view is ViewGroup) isViewGroupToBottom(view) else false
    }

    fun isAbsListViewToTop(absListView: AbsListView?): Boolean {
        if (absListView != null) {
            var firstChildTop = 0
            if (absListView.childCount > 0) {
                // 如果AdapterView的子控件数量不为0，获取第一个子控件的top
                firstChildTop = absListView.getChildAt(0).top - absListView.paddingTop
            }
            if (absListView.firstVisiblePosition == 0 && firstChildTop == 0) {
                return true
            }
        }
        return false
    }

    fun isRecyclerViewToTop(recyclerView: RecyclerView?): Boolean {
        if (recyclerView != null) {
            val manager = recyclerView.layoutManager ?: return true
            if (manager.itemCount == 0) {
                return true
            }
            var firstChildTop = 0
            if (recyclerView.childCount > 0) {
                // 处理item高度超过一屏幕时的情况
                val firstVisibleChild = recyclerView.getChildAt(0)
                if (firstVisibleChild != null && firstVisibleChild.measuredHeight >= recyclerView.measuredHeight) {
                    return if (Build.VERSION.SDK_INT < 14) {
                        !(ViewCompat.canScrollVertically(
                            recyclerView,
                            -1
                        ) || recyclerView.scrollY > 0)
                    } else {
                        !ViewCompat.canScrollVertically(recyclerView, -1)
                    }
                }

                // 如果RecyclerView的子控件数量不为0，获取第一个子控件的top

                // 解决item的topMargin不为0时不能触发下拉刷新
                val firstChild = recyclerView.getChildAt(0)
                val layoutParams =
                    firstChild.layoutParams as RecyclerView.LayoutParams
                firstChildTop =
                    firstChild.top - layoutParams.topMargin - getRecyclerViewItemTopInset(
                        layoutParams
                    ) - recyclerView.paddingTop
            }
            if (manager is LinearLayoutManager) {
                if (manager.findFirstCompletelyVisibleItemPosition() < 1 && firstChildTop == 0) {
                    return true
                }
            } else if (manager is StaggeredGridLayoutManager) {
                val out =
                    manager.findFirstCompletelyVisibleItemPositions(null)
                if (out[0] < 1 && firstChildTop == 0) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 通过反射获取RecyclerView的item的topInset
     *
     * @param layoutParams
     * @return
     */
    private fun getRecyclerViewItemTopInset(layoutParams: RecyclerView.LayoutParams): Int {
        try {
            val field =
                RecyclerView.LayoutParams::class.java.getDeclaredField("mDecorInsets")
            field.isAccessible = true
            // 开发者自定义的滚动监听器
            val decorInsets =
                field[layoutParams] as Rect
            return decorInsets.top
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun isWebViewToBottom(webview: WebView?, mTouchSlop: Int): Boolean {
        return webview != null && webview.contentHeight * webview.scale - (webview.height + webview.scrollY) <= 2 * mTouchSlop
    }

    fun isViewGroupToBottom(viewGroup: ViewGroup): Boolean {
        val subChildView = viewGroup.getChildAt(0)
        return subChildView != null && subChildView.measuredHeight <= viewGroup.scrollY + viewGroup.height
    }

    fun isScrollViewToBottom(scrollView: ScrollView?): Boolean {
        if (scrollView != null) {
            val scrollContentHeight =
                scrollView.scrollY + scrollView.measuredHeight - scrollView.paddingTop - scrollView.paddingBottom
            val realContentHeight = scrollView.getChildAt(0).measuredHeight
            if (scrollContentHeight == realContentHeight) {
                return true
            }
        }
        return false
    }

    fun isAbsListViewToBottom(absListView: AbsListView?): Boolean {
        if (absListView != null && absListView.adapter != null && absListView.childCount > 0 && absListView.lastVisiblePosition == absListView.adapter
                .count - 1
        ) {
            val lastChild =
                absListView.getChildAt(absListView.childCount - 1)
            return lastChild.bottom <= absListView.measuredHeight
        }
        return false
    }

    fun isRecyclerViewToBottom(recyclerView: RecyclerView?): Boolean {
        if (recyclerView != null) {
            val manager = recyclerView.layoutManager
            if (manager == null || manager.itemCount == 0) {
                return false
            }
            if (manager is LinearLayoutManager) {
                // 处理item高度超过一屏幕时的情况
                val lastVisibleChild =
                    recyclerView.getChildAt(recyclerView.childCount - 1)
                if (lastVisibleChild != null && lastVisibleChild.measuredHeight >= recyclerView.measuredHeight) {
                    return if (Build.VERSION.SDK_INT < 14) {
                        !(ViewCompat.canScrollVertically(
                            recyclerView,
                            1
                        ) || recyclerView.scrollY < 0)
                    } else {
                        !ViewCompat.canScrollVertically(recyclerView, 1)
                    }
                }
                val layoutManager = manager
                if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.itemCount - 1) {
                    return true
                }
            } else if (manager is StaggeredGridLayoutManager) {
                val layoutManager =
                    manager
                val out =
                    layoutManager.findLastCompletelyVisibleItemPositions(null)
                val lastPosition = layoutManager.itemCount - 1
                for (position in out) {
                    if (position == lastPosition) {
                        return true
                    }
                }
            }
        }
        return false
    }

    @JvmStatic
    fun scrollAViewBy(view: View, height: Int) {
        if (view is RecyclerView) view.scrollBy(
            0,
            height
        ) else if (view is ScrollView) view.smoothScrollBy(
            0,
            height
        ) else if (view is AbsListView) view.smoothScrollBy(height, 0) else {
            try {
                val method = view.javaClass.getDeclaredMethod(
                    "smoothScrollBy",
                    Int::class.java,
                    Int::class.java
                )
                method.invoke(view, 0, height)
            } catch (e: Exception) {
                view.scrollBy(0, height)
            }
        }
    }

    fun scrollToBottom(scrollView: ScrollView?) {
        scrollView?.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    fun scrollToBottom(absListView: AbsListView?) {
        if (absListView != null) {
            if (absListView.adapter != null && absListView.adapter.count > 0) {
                absListView.post(Runnable { absListView.setSelection(absListView.adapter.count - 1) })
            }
        }
    }

    fun scrollToBottom(recyclerView: RecyclerView?) {
        if (recyclerView != null) {
            if (recyclerView.adapter != null && recyclerView.adapter!!.itemCount > 0) {
                recyclerView.post(Runnable {
                    recyclerView.smoothScrollToPosition(
                        recyclerView.adapter!!.itemCount - 1
                    )
                })
            }
        }
    }

    fun scrollToBottom(view: View?) {
        if (view is RecyclerView) scrollToBottom(view as RecyclerView?)
        if (view is AbsListView) scrollToBottom(view as AbsListView?)
        if (view is ScrollView) scrollToBottom(view as ScrollView?)
    }

    fun getScreenHeight(context: Context): Int {
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        return dm.heightPixels
    }
}