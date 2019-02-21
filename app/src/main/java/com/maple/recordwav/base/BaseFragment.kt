package com.maple.recordwav.base

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(getLayoutRes(), container, false)
        view.isClickable = true // prevent click penetration
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
    }

    open fun onKeyBackPressed(): Boolean {
        return false
    }

    abstract fun getLayoutRes(): Int

    abstract fun initView(view: View, savedInstanceState: Bundle?)
}