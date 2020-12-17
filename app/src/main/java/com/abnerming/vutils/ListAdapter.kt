package com.abnerming.vutils

import android.content.Context
import com.abnerming.vlibrary.adapter.VAdapter

/**
 *AUTHOR:AbnerMing
 *DATA:2020/12/16
 *INTRODUCE:
 */
class ListAdapter(mContext: Context) : VAdapter<String>(mContext) {
    override fun getLayoutId(): Int {
        return R.layout.layout_item
    }

    override fun bindViewDataPosition(holder: VViewHolder, t: String, i: Int) {
        holder.setText(R.id.tv_text, t)
    }
}