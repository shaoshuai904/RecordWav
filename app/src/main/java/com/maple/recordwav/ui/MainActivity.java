package com.maple.recordwav.ui;

import android.Manifest;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.maple.recordwav.R;
import com.maple.recordwav.utils.T;
import com.maple.recordwav.utils.permission.RxPermissions;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author maple
 * @time 2018/4/8.
 */
public class MainActivity extends FragmentActivity {
    @BindView(R.id.tv_title) TextView mTitle;
    @BindView(R.id.tabhost) FragmentTabHost mTabHost;

    // Fragment界面
    private Class[] fragmentArray = {RecordPage.class, PlayPage.class, ParsePage.class};
    // 选项卡图片
    private int[] mImageViewArray = {R.drawable.tab_record_icon, R.drawable.tab_play_icon, R.drawable.tab_parse_icon};
    // 选项卡文字
    @BindArray(R.array.tab_fun_array) String[] mTextViewArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();

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

    private void initView() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.fl_content);
        for (int i = 0; i < fragmentArray.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }

        mTabHost.setOnTabChangedListener(tag -> mTitle.setText(tag));
        mTabHost.setCurrentTab(0);
        mTitle.setText(mTextViewArray[0]);
    }


    private View getTabItemView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_view, null);

        ImageView imageView = view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = view.findViewById(R.id.textview);
        textView.setText(mTextViewArray[index]);

        return view;
    }
}