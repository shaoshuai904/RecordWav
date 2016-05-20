package com.maple.recordwav.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.maple.recordwav.R;
import com.maple.recordwav.base.BaseFragment;

/**
 * 获取wav文件的信息
 *
 * @author maple
 * @time 16/5/20 下午6:40
 */
public class GetInfoPage extends BaseFragment {


    @Override
    public View initView(LayoutInflater inflater) {

        view = inflater.inflate(R.layout.fragment_get_info, null);
        ViewUtils.inject(this, view);

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public void initListener() {

    }
}