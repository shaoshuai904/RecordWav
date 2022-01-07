package com.maple.recordwav.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * Fragment manager
 *
 * @author maple
 * @time 2019-07-05
 */
class FragmentChangeManager(
        private val mFragmentManager: FragmentManager,
        private val mContainerViewId: Int,
        private val mFragments: List<Fragment>,
        private var currentTab: Int = 0
) {

    init {
        mFragmentManager.beginTransaction().apply {
            mFragments.forEachIndexed { index, fragment ->
                val tag = getFragmentTag(index)
                // mFragmentManager.findFragmentByTag(tag)?.let { remove(it) }
                if (!fragment.isAdded) {
                    add(mContainerViewId, fragment, tag).hide(fragment)
                }
            }
        }.commit()
        setCurrentFragment(currentTab)
    }

    companion object {
        // 获取指定索引 Fragment 的 Tag
        fun getFragmentTag(index: Int) = "tab_${index}"
    }

    fun setCurrentFragment(index: Int) {
        // tab相同 且 页面已添加，不再更新
        if (currentTab == index && mFragments[index].isVisible && mFragmentManager.fragments.size > 0)
            return
        mFragmentManager.beginTransaction().apply {
            mFragments.forEachIndexed { i, fragment ->
                if (i == index) {
                    show(fragment)
                } else {
                    hide(fragment)
                }
            }
        }.commit()
        currentTab = index
    }

    fun currentTab() = currentTab

    fun currentFragment() = mFragments[currentTab]

}