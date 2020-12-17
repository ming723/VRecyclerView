package com.abnerming.vutils

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abnerming.vlibrary.listener.VOnClickListener
import com.abnerming.vlibrary.listener.VRefreshLoadMoreListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = ListAdapter(this)
        recyclerview.setAdapter(adapter)

        val list = ArrayList<String>()
        for (i in 0..100) {
            list.add("我是测试数据===$i")
        }
        recyclerview.setData(list)

        recyclerview.setVOnClickListener(object : VOnClickListener {
            override fun itemClick(position: Int) {
                Toast.makeText(this@MainActivity, "====$position", Toast.LENGTH_LONG).show()
            }
        })

        recyclerview.setVRefreshLoadMoreListener(object : VRefreshLoadMoreListener {
            override fun refresh() {
                recyclerview.setData(list)
            }

            override fun loadMore() {

            }

        })

    }
}