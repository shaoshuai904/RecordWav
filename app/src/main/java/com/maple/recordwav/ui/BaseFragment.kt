package com.maple.recordwav.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * base fragment
 *
 * @author maple
 * @time 2018/12/4
 */
abstract class BaseFragment : Fragment() {
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    open fun onKeyBackPressed(): Boolean {
        // 是否消耗掉back事件
        return false
    }
}