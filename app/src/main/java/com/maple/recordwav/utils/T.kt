package com.maple.recordwav.utils

import android.content.Context
import android.widget.Toast

/**
 * Toast统一管理类
 *
 * @author shaoshuai
 */
object T {

    private var isShow = true

    /**
     * 短时间显示Toast
     */
    @JvmStatic
    fun showShort(context: Context, message: CharSequence) {
        if (isShow) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 短时间显示Toast
     */
    @JvmStatic
    fun showShort(context: Context, message: Int) {
        if (isShow) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 长时间显示Toast
     */
    @JvmStatic
    fun showLong(context: Context, message: CharSequence) {
        if (isShow) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 长时间显示Toast
     */
    @JvmStatic
    fun showLong(context: Context, message: Int) {
        if (isShow) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 自定义显示Toast时间
     */
    @JvmStatic
    fun show(context: Context, message: CharSequence, duration: Int) {
        if (isShow) {
            Toast.makeText(context, message, duration).show()
        }
    }

    /**
     * 自定义显示Toast时间
     */
    @JvmStatic
    fun show(context: Context, message: Int, duration: Int) {
        if (isShow) {
            Toast.makeText(context, message, duration).show()
        }
    }

}