package com.maple.recordwav.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.maple.recordwav.R
import com.maple.recordwav.base.BaseFragment
import com.maple.recordwav.databinding.FragmentAboutBinding

/**
 * about
 *
 * @author maple
 * @time 2016/5/20
 */
class AboutPage : BaseFragment() {
    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()

    }

    private fun initView() {
        binding.apply {
            tvInfo.text = "WAV 解析界面！"
        }
    }
}