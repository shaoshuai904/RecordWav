package com.maple.recordwav.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.maple.recordwav.R


fun ComponentActivity.setContentViewAndSetWindowInsets(rootView: View, topBarView: View?) {
    enableEdgeToEdge(
//            statusBarStyle = SystemBarStyle.light(Color.RED, Color.GREEN),
//            navigationBarStyle = SystemBarStyle.light(Color.RED, Color.GREEN)
    )
    // 设置状态栏 背景 + 字体颜色
    WindowInsetsControllerCompat(window, window.decorView).run {
        isAppearanceLightStatusBars = false
        isAppearanceLightNavigationBars = true
    }
    setContentView(rootView)
    topBarView?.setPaddingTopWithStatusBar()
    rootView.updateWindowInsets()
}

//inline
// 兼容 Android 15 系统栏内边距
fun View.updateWindowInsets() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            // topMargin = insets.top
            leftMargin = insets.left
            bottomMargin = insets.bottom
            rightMargin = insets.right
        }
        WindowInsetsCompat.CONSUMED
    }
}

// 设置 View 的 paddingTop 为 状态栏高度
fun View.setPaddingTopWithStatusBar() {
    setPadding(
        paddingLeft,
        getStatusBarHeight(context),
        paddingRight,
        paddingBottom
    )
}

fun getStatusBarHeight(context: Context): Int {
    // 获得状态栏高度
    var resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId <= 0) {
        resourceId = R.dimen.status_bar_height
    }
    return context.resources.getDimensionPixelSize(resourceId)
}