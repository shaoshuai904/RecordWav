package com.maple.recordwav.utils

import androidx.fragment.app.FragmentManager
import com.maple.recordwav.ui.BaseFragment
import java.util.*

/**
 * Fragment manager
 *
 * @author maple
 * @time 2019-07-05
 */
class FragmentChangeManager(
        private val mFragmentManager: FragmentManager,
        private val mContainerViewId: Int,
        private val mFragments: ArrayList<BaseFragment>
) {
    private var currentTab: Int = 0

    init {
        mFragmentManager.beginTransaction().apply {
            for (fragment in mFragments) {
                this.add(mContainerViewId, fragment).hide(fragment)
            }
        }.commit()
        setCurrentFragment(0)
    }

    fun setCurrentFragment(index: Int) {
        mFragmentManager.beginTransaction().apply {
            for (i in mFragments.indices) {
                val fragment = mFragments[i]
                if (i == index) {
                    this.show(fragment)
                } else {
                    this.hide(fragment)
                }
            }
        }.commit()
        currentTab = index
    }

    fun currentTab() = currentTab

    fun currentFragment() = mFragments[currentTab]

}