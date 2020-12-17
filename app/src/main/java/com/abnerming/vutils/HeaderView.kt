package com.abnerming.vutils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.abnerming.vlibrary.listener.VOnClickListener
import com.abnerming.vlibrary.view.VHeaderView
import java.util.ArrayList

/**
 *AUTHOR:AbnerMing
 *DATA:2020/12/16
 *INTRODUCE:
 */
class HeaderView : VHeaderView {
    var mContext: Context? = null

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    override fun getView(): View {
        val view = View.inflate(mContext, R.layout.layout_header, null)
        return view
    }
}