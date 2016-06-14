package com.maple.recordwav.parse;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.maple.recordwav.R;
import com.maple.recordwav.base.BaseFragment;
import com.maple.recordwav.utils.T;

/**
 * 获取wav文件的信息
 *
 * @author maple
 * @time 16/5/20 下午6:40
 */
public class ParsePage extends BaseFragment {
    @ViewInject(R.id.tv_info)
    private TextView tv_info;
    @ViewInject(R.id.lv_parse)
    private ListView lv_parse;

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_parse, null);
        ViewUtils.inject(this, view);

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        T.showShort(mContext, "play");
    }

    @Override
    public void initListener() {

    }


    public void getWavInfo(String filename) {
        WaveFileReader reader = new WaveFileReader(filename);
        if (reader.isSuccess()) {
            tv_info.setText("读取wav文件信息：" + filename
                    + "\n采样率：" + reader.getSampleRate()
                    + "\n声道数：" + reader.getNumChannels()
                    + "\n编码长度：" + reader.getBitPerSample()
                    + "\n数据长度：" + reader.getDataLen());
        } else {
            T.showShort(mContext, filename + "不是一个正常的wav文件");
        }
    }
}