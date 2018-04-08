package com.maple.recordwav.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.maple.recordwav.R;
import com.maple.recordwav.ui.parse.ParsePage;
import com.maple.recordwav.ui.play.PlayPage;
import com.maple.recordwav.ui.record.RecordPage;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

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
    private int[] mImageViewArray = {R.drawable.tab_home_btn, R.drawable.tab_message_btn, R.drawable.tab_square_btn};
    // 选项卡文字
    @BindArray(R.array.tab_fun_array) String[] mTextViewArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.fl_content);
        for (int i = 0; i < fragmentArray.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tag) {
                mTitle.setText(tag);
            }
        });
        mTabHost.setCurrentTab(0);
        mTitle.setText(mTextViewArray[0]);
    }


    private View getTabItemView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextViewArray[index]);

        return view;
    }
}