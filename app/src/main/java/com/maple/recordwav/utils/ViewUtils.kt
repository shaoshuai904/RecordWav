package com.maple.recordwav.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.maple.recordwav.R


object ViewUtils {

    /**
     * 设置 paddingTop 为 状态栏高度
     */
    @JvmStatic
    fun setPaddingTopWithStatusBar(view: View?) {
        if (view == null) {
            return
        }
        view.updatePadding(top = getStatusBarHeight(view.context))
    }

    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        // 获得状态栏高度
        var resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId <= 0) {
            resourceId = R.dimen.status_bar_height
        }
        return context.resources.getDimensionPixelSize(resourceId)
    }

    /**
     * 兼容 Android 15 系统栏内边距
     */
    @JvmStatic
    fun updateWindowInsetsMargin(view: View?) {
        updateWindowInsetsMargin(view, 0)
    }

    @JvmStatic
    fun updateWindowInsetsMargin(view: View?, btmPlus: Int) {
        if (view == null) {
            return
        }
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            Log.e("and15", "updateWindowInsetsMargin ${v.tag} : $insets")
            // androidx.viewpager.widget.ViewPager$LayoutParams cannot be cast to android.view.ViewGroup$MarginLayoutParams
//        if (v.layoutParams is ViewGroup.MarginLayoutParams) {
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                // topMargin = insets.top
                // leftMargin = insets.left
                bottomMargin = insets.bottom + btmPlus
                // rightMargin = insets.right
            }
//        } else {
//            Log.e("and15", "updateWindowInsetsMargin ${v.tag} :  ${v.layoutParams} 不是 MarginLayoutParams")
//        }
            windowInsets
        }
    }

    @JvmStatic
    fun updateWindowInsetsPadding(view: View?) {
        updateWindowInsetsPadding(view, 0)
    }

    @JvmStatic
    fun updateWindowInsetsPadding(view: View?, btmPlus: Int) {
        if (view == null) {
            return
        }
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            Log.e("and15", "updateWindowInsets Padding ${v.tag} : $insets")
            v.updatePadding(
                left = insets.left,
                // top = insets.top,
                right = insets.right,
                bottom = insets.bottom + btmPlus
            )
            windowInsets
        }
    }

}
