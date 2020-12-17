package com.abnerming.vlibrary.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.*
import com.abnerming.vlibrary.R
import com.abnerming.vlibrary.adapter.VAdapter
import com.abnerming.vlibrary.listener.VOnClickListener
import com.abnerming.vlibrary.listener.VRefreshLoadMoreListener
import com.abnerming.vlibrary.refresh.Footer.BallPulseView
import com.abnerming.vlibrary.refresh.Footer.LoadingView
import com.abnerming.vlibrary.refresh.RefreshListenerAdapter
import com.abnerming.vlibrary.refresh.TwinklingRefreshLayout
import com.abnerming.vlibrary.refresh.header.GoogleDotView
import com.abnerming.vlibrary.refresh.header.SinaRefreshView
import com.abnerming.vlibrary.utils.NetworkUtils


/**
 *AUTHOR:AbnerMing
 *DATA:2020/12/16
 *INTRODUCE:主要功能有：
 * 1、集成了下拉刷新和上拉加载
 * 2、动态设置数据
 * 3、动态展示不同数据下视图
 */
class VRecyclerView(context: Context?, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private var mShowNetErrorIcon: Drawable?
    private var mShowNetErrorContent: String?
    private var mShowDataNullIcon: Drawable?
    private var mShowDataNullContent: String?
    private var mShowNetError: Boolean
    private var mShowDataNull: Boolean
    private var mDownHeight: Float
    private var mIsDivider: Boolean
    private var mDivider: Drawable?
    private var mDefaultLoadingStyle: Int
    private var mHeaderHeight: Float
    private var mLoadmore: Boolean
    private var mRefresh: Boolean
    private var mVrefresh: TwinklingRefreshLayout?
    private var mStaggeredManager: Int
    private var mSpanCount: Int
    private var mManagerType: Int
    private var mRecyclerView: RecyclerView?
    private var mVOnClickListener: VOnClickListener? = null
    private var mVRefreshLoadMoreListener: VRefreshLoadMoreListener? = null
    private var mDefaultLoadmore: Boolean
    private var mDefaultRefresh: Boolean
    private var mDefaultOverScroll: Boolean
    private var mIsRefresh: Boolean = true//是否处于下拉,默认为true
    private var mDataNullUi: View? = null
    private var mNetErrorUi: View? = null
    private var mActionBarHeight: Int = 0

    init {
        val view = View.inflate(context, R.layout.layout_recyclerview, null)
        mRecyclerView = view.findViewById(R.id.vrecyclerview)
        mVrefresh = view.findViewById(R.id.vrefresh)
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.VRecyclerView)
        //获取管理器类型
        mManagerType = typedArray.getInteger(R.styleable.VRecyclerView_layoutManagerType, 0)
        //网格布局管理器和流式布局的数量
        mSpanCount = typedArray.getInteger(R.styleable.VRecyclerView_spanCount, 0)
        //流失布局管理器方向 0 横 1 竖
        mStaggeredManager = typedArray.getInteger(R.styleable.VRecyclerView_staggeredManager, 1)
        //获取是否需要下拉刷新和上拉加载
        mRefresh = typedArray.getBoolean(R.styleable.VRecyclerView_refresh, false)
        mLoadmore = typedArray.getBoolean(R.styleable.VRecyclerView_loadmore, false)
        //进来是否主动进行下拉或上拉
        mDefaultRefresh = typedArray.getBoolean(R.styleable.VRecyclerView_defaultRefresh, false)
        mDefaultLoadmore = typedArray.getBoolean(R.styleable.VRecyclerView_defaultLoadmore, false)
        //是否开启回弹
        mDefaultOverScroll =
            typedArray.getBoolean(R.styleable.VRecyclerView_defaultOverScroll, false)
        //设置头view的高度
        mHeaderHeight =
            typedArray.getDimensionPixelSize(R.styleable.VRecyclerView_headerHeight, 0).toFloat()
        //设置下拉高度
        mDownHeight =
            typedArray.getDimensionPixelSize(R.styleable.VRecyclerView_downHeight, 0).toFloat()
        //上拉刷新和上拉加载样式
        mDefaultLoadingStyle =
            typedArray.getInteger(R.styleable.VRecyclerView_defaultLoadingStyle, 0)
        //是否需要分割线
        mIsDivider = typedArray.getBoolean(R.styleable.VRecyclerView_isdivider, false)
        //分割线的样式
        mDivider = typedArray.getDrawable(R.styleable.VRecyclerView_divider)
        //是否要展示无数据的UI
        mShowDataNull = typedArray.getBoolean(R.styleable.VRecyclerView_show_data_null, false)
        //是否要展示无网络的UI
        mShowNetError = typedArray.getBoolean(R.styleable.VRecyclerView_show_net_error, false)
        //获取无数据展示的内容及图片
        mShowDataNullContent = typedArray.getString(R.styleable.VRecyclerView_show_data_content)
        mShowDataNullIcon = typedArray.getDrawable(R.styleable.VRecyclerView_show_data_icon)
        //获取无网展示的内容及图片
        mShowNetErrorContent = typedArray.getString(R.styleable.VRecyclerView_show_net_content)
        mShowNetErrorIcon = typedArray.getDrawable(R.styleable.VRecyclerView_show_net_icon)

        initManager()
        initData()
        addView(view)
    }

    //设置管理器
    private fun initManager() {
        when (mManagerType) {
            0 -> {//默认 垂直的LinearLayoutManager
                val mLinearLayoutManager = LinearLayoutManager(context)
                mLinearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                mRecyclerView!!.layoutManager = mLinearLayoutManager
            }
            1 -> {//横向的LinearLayoutManager
                val mLinearLayoutManager = LinearLayoutManager(context)
                mLinearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                mRecyclerView!!.layoutManager = mLinearLayoutManager
            }
            2 -> {//网格
                val mGridLayoutManager = GridLayoutManager(context, mSpanCount)
                mRecyclerView!!.layoutManager = mGridLayoutManager
            }
            3 -> {//流式布局
                val mStaggeredGridLayoutManager =
                    StaggeredGridLayoutManager(mSpanCount, mStaggeredManager)
                mRecyclerView!!.layoutManager = mStaggeredGridLayoutManager
            }
        }

        if (mIsDivider) {
            if (mDivider != null) {
                setDividerItemLine()
            } else {
                mRecyclerView!!.addItemDecoration(
                    DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
        }


    }


    //设置分割线样式
    private fun setDividerItemLine() {
        val divider =
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(mDivider!!)
        mRecyclerView!!.addItemDecoration(divider)
    }

    private fun initData() {
        //是否进来需要执行下拉或上拉
        if (mDefaultRefresh) {
            mVrefresh!!.startRefresh()
        }

        if (mDefaultLoadmore) {
            mVrefresh!!.startLoadMore()
        }

        //不为0时设置高度
        if (mHeaderHeight != 0f) {
            mVrefresh!!.setHeaderHeight(mHeaderHeight)
        }

        //不为0时进行设置下拉的高度
        if (mDownHeight != 0f) {
            mVrefresh!!.setMaxHeadHeight(mHeaderHeight)
        }

        //回弹效果
        mVrefresh!!.setEnableOverScroll(mDefaultOverScroll)

        mVrefresh!!.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                super.onRefresh(refreshLayout)
                //下拉刷新
                mIsRefresh = true
                mVRefreshLoadMoreListener?.let {
                    mVRefreshLoadMoreListener!!.refresh()
                }
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                super.onLoadMore(refreshLayout)
                //上拉加载
                mIsRefresh = false
                mVRefreshLoadMoreListener?.let {
                    mVRefreshLoadMoreListener!!.loadMore()
                }
            }
        })

        //默认的刷新样式
        if (mDefaultLoadingStyle == 0) {
            mVrefresh!!.setHeaderView(SinaRefreshView(context))
            mVrefresh!!.setBottomView(LoadingView(context))
        } else if (mDefaultLoadingStyle == 1) {
            mVrefresh!!.setHeaderView(GoogleDotView(context))
            mVrefresh!!.setBottomView(BallPulseView(context))
        }

        //设置是否需要下拉和上拉，默认，不需要
        mVrefresh!!.setEnableRefresh(mRefresh)
        mVrefresh!!.setEnableLoadmore(mLoadmore)

    }

    //添加View视图
    private fun layoutAddView(desc: String, ids: Int) {


    }

    //传递适配器
    fun <T> setAdapter(mVAdapter: VAdapter<T>) {
        mRecyclerView!!.adapter = mVAdapter
        mVAdapter.setVOnClickListener(object : VOnClickListener {
            override fun itemClick(position: Int) {
                mVOnClickListener?.let {
                    mVOnClickListener!!.itemClick(position)
                }
            }
        })
        mVAdapter.setNetErrorOrDataNull(mShowNetError, mShowDataNull, mIsRefresh)
    }

    //设置是否需要下拉和上拉
    fun setRefreshAndLoadmore(mRefresh: Boolean, mLoadmore: Boolean) {
        //设置是否需要下拉和上拉，默认，不需要
        mVrefresh!!.setEnableRefresh(mRefresh)
        mVrefresh!!.setEnableLoadmore(mLoadmore)
    }

    //设置数据
    fun <T> setData(list: List<T>) {
        val adapter = mRecyclerView!!.adapter as VAdapter<T>

        if (mDataNullUi == null) {
            val viewDataNull = View.inflate(context, R.layout.layout_normal, null)
            val tvNormalViewDataNull = viewDataNull.findViewById<TextView>(R.id.tv_normal_text)
            val ivNormalViewDataNull = viewDataNull.findViewById<ImageView>(R.id.iv_normal)
            val screenHeight = context.resources.displayMetrics.heightPixels
            val layoutRect = viewDataNull.findViewById<LinearLayout>(R.id.layout_rect)
            val params = layoutRect.layoutParams
            params.height =
                screenHeight - mActionBarHeight - getStatusBarHeight(context)//高度 这里需要减去 你那个ActionBar
            layoutRect.layoutParams = params
            if (TextUtils.isEmpty(mShowDataNullContent)) {
                mShowDataNullContent = "数据暂时为空"
            }
            if (mShowDataNullIcon == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mShowDataNullIcon = context.getDrawable(R.drawable.data_null)
                }
            }
            ivNormalViewDataNull.setImageDrawable(mShowDataNullIcon)
            tvNormalViewDataNull.text = mShowDataNullContent
            mDataNullUi = viewDataNull
        }
        if (mNetErrorUi == null) {
            val viewNetError = View.inflate(context, R.layout.layout_normal, null)
            val tvNetError = viewNetError.findViewById<TextView>(R.id.tv_normal_text)
            val ivNetError = viewNetError.findViewById<ImageView>(R.id.iv_normal)
            val screenHeightNetError = context.resources.displayMetrics.heightPixels
            val layoutRectError = viewNetError.findViewById<LinearLayout>(R.id.layout_rect)
            val paramsError = layoutRectError.layoutParams
            paramsError.height =
                screenHeightNetError - mActionBarHeight - getStatusBarHeight(context)//高度 这里需要减去 你那个ActionBar
            layoutRectError.layoutParams = paramsError

            if (TextUtils.isEmpty(mShowNetErrorContent)) {
                mShowNetErrorContent = "网络开小差了~"
            }
            if (mShowNetErrorIcon == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mShowNetErrorIcon = context.getDrawable(R.drawable.net_error)
                }
            }
            ivNetError.setImageDrawable(mShowNetErrorIcon)
            tvNetError.text = mShowNetErrorContent

            mNetErrorUi = viewNetError
        }
        adapter.setNetErrorOrDataNullUi(mDataNullUi, mNetErrorUi)
        //只要包含了一个
        if (mIsRefresh && (mShowDataNull || mShowNetError) && (list.isEmpty())) {
            adapter.setListNull()
        } else {
            if (NetworkUtils.isConnected(context)) {
                adapter.setList(list)
            } else {

                adapter.setListNull()
            }
        }
        //设置完数据后关闭
        mVrefresh!!.finishRefreshing()
        mVrefresh!!.finishLoadmore()

    }

    //条目点击
    fun setVOnClickListener(mVOnClickListener: VOnClickListener) {
        this.mVOnClickListener = mVOnClickListener
    }

    //上下拉刷新
    fun setVRefreshLoadMoreListener(mVRefreshLoadMoreListener: VRefreshLoadMoreListener) {
        this.mVRefreshLoadMoreListener = mVRefreshLoadMoreListener
    }

    //切换下拉刷新View
    fun changeHeaderView(headerView: VHeaderView) {
        mVrefresh!!.setHeaderView(headerView)
    }

    //切换上拉加载View
    fun changeFooterView(bottomView: VBottomView) {
        mVrefresh!!.setBottomView(bottomView)
    }


    //Actionbar的高度 也就是 你页面的title 默认为0
    fun setActionBarHeight(mActionBarHeight: Int) {
        this.mActionBarHeight = mActionBarHeight
    }

    //自己设置数据为空时的UI
    fun setDataNull(view: View) {
        mDataNullUi = view
    }

    //自己设置无网时的UI
    fun setNetError(view: View) {
        mNetErrorUi = view
    }


    //设置数据为空时的文字及图片展示
    fun setDataNullContent(mShowDataNullContent: String, mShowDataNullIcon: Drawable) {
        this.mShowDataNullContent = mShowDataNullContent
        this.mShowDataNullIcon = mShowDataNullIcon
    }

    //设置无网络时的文字及图片展示
    fun setNetErrorContent(mShowNetErrorContent: String, mShowNetErrorIcon: Drawable) {
        this.mShowNetErrorContent = mShowNetErrorContent
        this.mShowNetErrorIcon = mShowNetErrorIcon
    }

    //获取状态栏高度
    private fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
}