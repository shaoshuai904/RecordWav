package com.maple.recordwav.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maple.msdialog.AlertDialog
import com.maple.recordwav.base.BaseFragment
import com.maple.recordwav.databinding.FragmentAboutBinding
import com.maple.recordwav.utils.permission.RxPermissions

/**
 * about
 *
 * @author maple
 * @time 2016/5/20
 */
class AboutPage : BaseFragment() {
    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView() {
        requestPermission()
    }

    @SuppressLint("CheckResult")
    private fun requestPermission() {
        RxPermissions(this).request(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        ).subscribe { granted ->
            if (granted) {
                binding.wbWeb.loadUrl("https://github.com/shaoshuai904/RecordWav")
            } else {
                AlertDialog(mContext).apply {
                    setCancelable(false)
                    setCanceledOnTouchOutside(false)
                    setTitle("连个网？")
                    setLeftButton("拒绝")
                    setRightButton("再选一次") { requestPermission() }
                }.show()
            }
        }
    }
}