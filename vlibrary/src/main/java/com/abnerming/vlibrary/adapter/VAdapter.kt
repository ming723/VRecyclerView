package com.abnerming.vlibrary.adapter

import android.content.Context
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.abnerming.vlibrary.R
import com.abnerming.vlibrary.listener.VOnClickListener
import com.abnerming.vlibrary.utils.NetworkUtils

/**
 * AUTHOR:AbnerMing
 * 微信公众号：Android干货铺
 * 个人网站:http://www.vipandroid.cn
 * INTRODUCE：RecyclerView的适配器的父类
 */
abstract class VAdapter<T> : RecyclerView.Adapter<VAdapter.VViewHolder> {
    open var mContext: Context? = null
    private var list: List<T> = ArrayList()
    private var mVOnClickListener: VOnClickListener? = null
    private var mShowNetError: Boolean = false
    private var mShowDataNull: Boolean = false
    private var mIsRefresh: Boolean = false
    private var mDataNullUi: View? = null
    private var mNetErrorUi: View? = null
    private var mListDataNull: Boolean = false

    constructor(mContext: Context) {
        this.mContext = mContext
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): VViewHolder {
        val viewParent = View.inflate(mContext, R.layout.layout_recyclerview_item, null)
        val view = View.inflate(mContext, getLayoutId(), null)
        (viewParent as RelativeLayout).addView(view)
        return VViewHolder(viewParent)
    }

    override fun onBindViewHolder(@NonNull holder: VViewHolder, i: Int) {
        val pView = holder.itemView
        if (!mListDataNull) {
            (pView as RelativeLayout).removeView(mDataNullUi!!)
            pView.removeView(mNetErrorUi!!)

            bindViewDataPosition(holder, list[i], i)
            holder.itemView.setOnClickListener {
                //条目点击
                mVOnClickListener?.let {
                    mVOnClickListener!!.itemClick(i)
                }

            }
        } else {
            //需要展示为空和无网UI
            holder.itemView.isClickable=false
            if (mIsRefresh && mShowNetError) {
                //证明需要展示无网的UI
                if (NetworkUtils.isConnected(mContext!!)) {//有网
                    if (mShowDataNull && list.isEmpty()) {//判断数据是否为空
                        (pView as RelativeLayout).removeView(mDataNullUi!!)
                        pView.addView(mDataNullUi!!)
                    }
                } else {
                    //直接展示无网的UI
                    (pView as RelativeLayout).removeView(mNetErrorUi!!)
                    pView.addView(mNetErrorUi!!)
                }
            }
            //这种情况是只判断了数据是否为空，没有判断网络
            if (mIsRefresh && !mShowNetError && mShowDataNull && list.isEmpty()) {
                (pView as RelativeLayout).removeView(mDataNullUi!!)
                pView.addView(mDataNullUi!!)
            }
        }


        Log.e("Adapter数据", "onBindViewHolder")
    }


    class VViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sparseArray = SparseArray<View>()

        //用于获取控件的方法
        operator fun get(id: Int): View? {
            var view: View? = sparseArray.get(id)
            if (view == null) {
                view = itemView.findViewById(id)
                sparseArray.put(id, view)
            }
            return view
        }

        //给TextView 赋值
        fun setText(id: Int, content: String) {
            val textView = get(id) as TextView?
            textView!!.text = content
        }

        fun setPic(id: Int, url: String) {
            //这里写加载图片的逻辑
            //Glide  Fresco  pissca
        }
    }

    override fun getItemCount(): Int {
        if (mListDataNull) {//如果为true
            return 1
        }
        return list.size
    }

    //传递数据
    fun setList(list: List<T>) {
        Log.e("Adapter数据", "setList==" + list.size)
        this.list = list
        mListDataNull = false
        notifyDataSetChanged()
    }

    //为空
    fun setListNull() {
        Log.e("Adapter数据", "setListNull")
        mListDataNull = true
        notifyDataSetChanged()
    }

    //是否需要展示默认的UI图
    fun setNetErrorOrDataNull(mShowNetError: Boolean, mShowDataNull: Boolean, mIsRefresh: Boolean) {
        this.mShowNetError = mShowNetError
        this.mShowDataNull = mShowDataNull
        this.mIsRefresh = mIsRefresh
    }

    //传递的Ui视图
    fun setNetErrorOrDataNullUi(mDataNullUi: View?, mNetErrorUi: View?) {
        this.mDataNullUi = mDataNullUi
        this.mNetErrorUi = mNetErrorUi
    }

    //获取数据
    fun getList(): List<T> {
        return list
    }

    //子类向父类传递的layout
    abstract fun getLayoutId(): Int

    //子类初始化数据
    abstract fun bindViewDataPosition(baseViewHolder: VViewHolder, t: T, i: Int)

    //条目点击
    fun setVOnClickListener(mVOnClickListener: VOnClickListener) {
        this.mVOnClickListener = mVOnClickListener
    }
}