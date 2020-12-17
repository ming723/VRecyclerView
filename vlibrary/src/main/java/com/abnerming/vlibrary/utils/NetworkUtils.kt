package com.abnerming.vlibrary.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 *AUTHOR:AbnerMing
 *DATA:2020/12/16
 *INTRODUCE:
 */
class NetworkUtils {
    companion object {
        var networkMessage = "网络开小差了，请检查网络！"

        /**
         * 检查是否连接网络
         *
         * @param context
         * @return
         */

        fun isConnected(context: Context): Boolean {
            val connectMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            val wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (mobNetInfo != null && mobNetInfo.isConnected) {
                return true
            }
            return if (wifiNetInfo != null && wifiNetInfo.isConnected) {
                true
            } else false
        }
    }
}