package com.abnerming.vlibrary.refresh.utils

import android.util.Log

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：
 */
object LogUtil {
    private const val DEBUG = false
    @JvmStatic
    fun i(msg: String?) {
        if (!DEBUG) return
        Log.i("TwinklingRefreshLayout", msg!!)
    }
}