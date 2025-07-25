package com.maple.recordwav.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayout
import com.maple.msdialog.AlertDialog
import com.maple.recordwav.R
import com.maple.recordwav.databinding.ActivityMainBinding
import com.maple.recordwav.utils.BottomTabView
import com.maple.recordwav.utils.FragmentChangeManager
import com.maple.recordwav.utils.permission.RxPermissions
import com.maple.recordwav.utils.setContentViewAndSetWindowInsets


/**
 * Record Wav 示例（Kotlin版）
 *
 * @author maple
 * @time 2018/4/8.
 */
class MainActivity : FragmentActivity() {
    private lateinit var binding: ActivityMainBinding
    private var tabIndex: Int = 0 // 当前显示页面

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentViewAndSetWindowInsets(binding.root, binding.flTopBar)

        initTitleBar()
        initView()
        requestPermission()
    }

    private fun initTitleBar() {
        binding.tvTitle.text = "Wav 助手(Kotlin)"
        binding.tvSwitch.text = "转Java\n版本"
        binding.tvSwitch.setOnClickListener {
            val intent = Intent(this@MainActivity, MainActivityJava::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initView() {
        val fragmentList = arrayListOf(
            supportFragmentManager.findFragmentByTag(FragmentChangeManager.getFragmentTag(0)) ?: RecordPage(),
            supportFragmentManager.findFragmentByTag(FragmentChangeManager.getFragmentTag(1)) ?: PlayParsePage(),
            supportFragmentManager.findFragmentByTag(FragmentChangeManager.getFragmentTag(2)) ?: AboutPage()
        )
        val iconArr = arrayListOf(R.drawable.sel_tab_record_icon, R.drawable.sel_tab_play_icon, R.drawable.sel_tab_parse_icon)
        val titleArr = arrayListOf(getString(R.string.record), getString(R.string.play), getString(R.string.info))
        fragmentList.forEachIndexed { index, _ ->
            val tab: TabLayout.Tab = binding.tlTab.newTab()
            tab.customView = BottomTabView(this).apply {
                setIcon(iconArr[index], null)
                setTitle(titleArr[index])
            }
            binding.tlTab.addTab(tab)
        }
        val fgManager = FragmentChangeManager(supportFragmentManager, R.id.fl_content, fragmentList, tabIndex)
        binding.tlTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tabIndex = tab.position
                fgManager.setCurrentFragment(tabIndex)
                (tab.customView as BottomTabView?)?.setSelectStatus(true)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                (tab?.customView as BottomTabView?)?.setSelectStatus(false)
            }
        })
        binding.tlTab.getTabAt(tabIndex)?.select()
    }

    @SuppressLint("CheckResult")
    private fun requestPermission() {
        RxPermissions(this).request(
            Manifest.permission.RECORD_AUDIO
        ).subscribe { granted ->
            if (!granted) {
                AlertDialog(this).apply {
                    setCancelable(false)
                    setCanceledOnTouchOutside(false)
                    setTitle("权限不足！")
                    setMessage("录音必须要有“RECORD_AUDIO”权限哦，否则无法录音和存储。")
                    setLeftButton("退出") { this@MainActivity.finish() }
                    setRightButton("再选一次") { requestPermission() }
                }.show()
            }
        }
    }
}