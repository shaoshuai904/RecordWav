package com.maple.recordwav;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.maple.recordwav.ui.fragment.GetInfoPage;
import com.maple.recordwav.ui.fragment.PlayPage;
import com.maple.recordwav.ui.fragment.VoicePage;

public class MainActivity extends FragmentActivity {
    @ViewInject(R.id.tv_title)
    private TextView mTitle;
    @ViewInject(R.id.tabhost)
    private FragmentTabHost mTabHost;

    // Fragment界面
    private Class[] fragmentArray = {VoicePage.class, PlayPage.class, GetInfoPage.class};
    // 选项卡图片
    private int[] mImageViewArray = {R.drawable.tab_home_btn, R.drawable.tab_message_btn, R.drawable.tab_square_btn};
    // 选项卡文字
    @ResInject(id = R.array.tab_fun_array, type = ResType.StringArray)
    private String[] mTextViewArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);

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