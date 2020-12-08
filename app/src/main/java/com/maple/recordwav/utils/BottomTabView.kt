package com.maple.recordwav.utils

import android.content.Context
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.maple.recordwav.R
import com.maple.recordwav.databinding.ItemButtomTabViewBinding

/**
 * App主页面底部tab view
 *
 * @author : shaoshuai27
 * @date ：2019/10/22
 */
class BottomTabView : FrameLayout {
    private lateinit var binding: ItemButtomTabViewBinding
    private var iconRes: Int? = null
    private var animatorRes: Int? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_buttom_tab_view, this, true
        )
    }

    fun setIcon(@DrawableRes selectedRes: Int, @DrawableRes animatorRes: Int?) {
        this.iconRes = selectedRes
        this.animatorRes = animatorRes
        binding.ivIcon.setImageResource(selectedRes)
    }

    fun setTitle(title: String?) {
        binding.tvTitle.text = title
    }

    fun setSelectStatus(select: Boolean) {
        if (select && animatorRes != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val drawable = ContextCompat.getDrawable(context, animatorRes!!)
                binding.ivIcon.setImageDrawable(drawable)
                if (drawable is AnimatedVectorDrawable) {
                    drawable.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                        override fun onAnimationStart(drawable: Drawable) {}
                        override fun onAnimationEnd(drawable: Drawable) {
                            resetIconRes()
                        }
                    })
                    drawable.start()
                }
            } catch (e: Exception) {
                resetIconRes()
            }
        } else {
            resetIconRes()
        }
    }

    // 重置图标状态
    private fun resetIconRes() {
        if (iconRes != null) {
            binding.ivIcon.setImageResource(iconRes!!)
        }
    }
}