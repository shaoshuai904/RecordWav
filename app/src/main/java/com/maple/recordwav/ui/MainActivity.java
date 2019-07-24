package com.maple.recordwav.ui;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.maple.recordwav.R;
import com.maple.recordwav.databinding.ActivityMainBinding;
import com.maple.recordwav.utils.T;
import com.maple.recordwav.utils.permission.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author maple
 * @time 2018/4/8.
 */
public class MainActivity extends FragmentActivity {
    ActivityMainBinding binding;
    // Fragment界面
    private Class[] fragmentArray = {RecordPage.class, PlayPage.class, ParsePage.class};
    // 选项卡图片
    private int[] mImageViewArray = {R.drawable.tab_record_icon, R.drawable.tab_play_icon, R.drawable.tab_parse_icon};
    // 选项卡文字
    String[] mTextViewArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mTextViewArray = getResources().getStringArray(R.array.tab_fun_array);

        initView();
        requestPermission();
    }

    private void initView() {
        binding.tabhost.setup(this, getSupportFragmentManager(), R.id.fl_content);
        for (int i = 0; i < fragmentArray.length; i++) {
            TabHost.TabSpec tabSpec = binding.tabhost.newTabSpec(mTextViewArray[i]).setIndicator(getTabItemView(i));
            binding.tabhost.addTab(tabSpec, fragmentArray[i], null);
            binding.tabhost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }

        binding.tabhost.setOnTabChangedListener(tag -> binding.tvTitle.setText(tag));
        binding.tabhost.setCurrentTab(0);
        binding.tvTitle.setText(mTextViewArray[0]);
    }

    private View getTabItemView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_view, null);

        ImageView imageView = view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = view.findViewById(R.id.textview);
        textView.setText(mTextViewArray[index]);

        return view;
    }

    private void requestPermission() {
        new RxPermissions(this)
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                )
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        T.showShort(MainActivity.this, "不同意将无法使用");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}