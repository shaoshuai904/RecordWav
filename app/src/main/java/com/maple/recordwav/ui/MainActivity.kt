package com.maple.recordwav.ui

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.maple.recordwav.R
import com.maple.recordwav.utils.FragmentChangeManager
import com.maple.recordwav.utils.permission.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author maple
 * @time 2018/4/8.
 */
class MainActivity : FragmentActivity() {
    lateinit var fgManager: FragmentChangeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        requestPermission()
    }

    private fun initView() {
        val fragmentArray = arrayListOf(RecordPage(), PlayPage(), ParsePage())
        fgManager = FragmentChangeManager(supportFragmentManager, R.id.fl_content, fragmentArray)

        nav_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nvg_home -> {
                    fgManager.setCurrentFragment(0)
                    true
                }
                R.id.nvg_play -> {
                    fgManager.setCurrentFragment(1)
                    true
                }
                R.id.nvg_info -> {
                    fgManager.setCurrentFragment(2)
                    true
                }
                else -> false
            }
        }
    }

    private fun requestPermission() {
        RxPermissions(this).request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        ).subscribe()
    }
}