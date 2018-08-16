package com.maple.recordwav.ui;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.maple.recorder.parse.WaveFileReader;
import com.maple.recordwav.R;
import com.maple.recordwav.WavApp;
import com.maple.recordwav.base.BaseFragment;
import com.maple.recordwav.utils.LoadingDialog;
import com.maple.recordwav.utils.SearchFileUtils;
import com.maple.recordwav.utils.T;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 获取wav文件的信息
 *
 * @author maple
 * @time 16/5/20 下午6:40
 */
public class ParsePage extends BaseFragment {
    @BindView(R.id.tv_info) TextView tv_info;
    @BindView(R.id.lv_parse) ListView lv_parse;

    ArrayAdapter<String> adapter;
    private List<String> wavFilelist;
    private LoadingDialog loadingDialog;


    Handler updateProHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 200) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (wavFilelist.size() > 0) {
                    tv_info.setText("点击条目，获取wav文件的信息 ！");
                } else {
                    tv_info.setText("没有找到文件，请去录制 ！");
                }
                adapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_parse, null);
        ButterKnife.bind(this, view);

        loadingDialog = new LoadingDialog(getActivity());
        tv_info.setText("WAV 解析界面！");
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        wavFilelist = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, wavFilelist);
        lv_parse.setAdapter(adapter);

        new Thread(searchSong).start();
        loadingDialog.show("搜索中...");
    }

    @Override
    public void initListener() {
        lv_parse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filePath = wavFilelist.get(position);
                if (new File(filePath).exists()) {
                    getWavInfo(filePath);
                } else {
                    T.showShort(mContext, "选择的文件不存在");
                }
            }
        });
    }

    Runnable searchSong = new Runnable() {
        @Override
        public void run() {
            List<File> fileArr = SearchFileUtils.search(new File(WavApp.rootPath), new String[]{".wav"});
            wavFilelist.clear();
            for (int i = 0; i < fileArr.size(); i++) {
                wavFilelist.add(fileArr.get(i).getAbsolutePath());
            }
            updateProHandler.sendEmptyMessage(200);
        }
    };


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