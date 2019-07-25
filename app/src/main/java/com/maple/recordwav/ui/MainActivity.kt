package com.maple.recordwav.ui

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.maple.recordwav.R
import com.maple.recordwav.databinding.ActivityMainBinding
import com.maple.recordwav.databinding.ItemTabViewBinding
import com.maple.recordwav.utils.T
import com.maple.recordwav.utils.permission.RxPermissions
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @author maple
 * @time 2018/4/8.
 */
class MainActivity : FragmentActivity() {
    lateinit var binding: ActivityMainBinding
    // Fragment界面
    private val fragmentArray = arrayOf(RecordPage::class.java, PlayPage::class.java, ParsePage::class.java)
    // 选项卡图片
    private val mImageViewArray = intArrayOf(R.drawable.tab_record_icon, R.drawable.tab_play_icon, R.drawable.tab_parse_icon)
    // 选项卡文字
    lateinit var mTextViewArray: Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mTextViewArray = resources.getStringArray(R.array.tab_fun_array)

        initView()
        requestPermission()
    }

    private fun initView() {
        binding.tabhost.setup(this, supportFragmentManager, R.id.fl_content)
        for (i in fragmentArray.indices) {
            val tabSpec = binding.tabhost.newTabSpec(mTextViewArray[i])
                    .setIndicator(getTabItemView(mImageViewArray[i], mTextViewArray[i]))
            binding.tabhost.addTab(tabSpec, fragmentArray[i], null)
            binding.tabhost.tabWidget.getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background)
        }

        binding.tabhost.setOnTabChangedListener { binding.tvTitle.text = it }
        binding.tabhost.currentTab = 0
        binding.tvTitle.text = mTextViewArray[0]
    }

    private fun getTabItemView(@DrawableRes resId: Int, title: String): View {
        val binding: ItemTabViewBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.item_tab_view, null, false)

        binding.apply {
            ivIcon.setImageResource(resId)
            tvTitle.text = title
        }

        return binding.root
    }

    private fun requestPermission() {
        RxPermissions(this)
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                )
                .subscribe(object : Observer<Boolean> {

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: Boolean) {
                    }

                    override fun onComplete() {
                    }

                    override fun onError(e: Throwable) {
                        T.showShort(this@MainActivity, "不同意将无法使用")
                    }

                })
    }
}