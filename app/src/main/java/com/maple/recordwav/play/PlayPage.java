package com.maple.recordwav.play;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
 * wav 文件播放
 *
 * @author maple
 * @time 16/5/20 下午6:40
 */
public class PlayPage extends BaseFragment {
    public static final int SEARCH_MESSAGE_CODE = 200;
    @BindView(R.id.tv_des) TextView tv_des;
    @BindView(R.id.lv_wav) ListView lv_wav;

    ArrayAdapter<String> adapter;
    private List<String> wavFileList;
    private LoadingDialog loadingDialog;

    Handler updateProHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == SEARCH_MESSAGE_CODE) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (wavFileList.size() > 0) {
                    tv_des.setText("点击条目，播放wav文件！");
                } else {
                    tv_des.setText("没有找到文件，请去录制 ！");
                }
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_play, null);
        ButterKnife.bind(this, view);

        loadingDialog = new LoadingDialog(getActivity());
        tv_des.setText("WAV 播放界面！");
        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        wavFileList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, wavFileList);
        lv_wav.setAdapter(adapter);

        new Thread(searchSong).start();
        loadingDialog.show("搜索中...");
    }

    @Override
    public void initListener() {
        lv_wav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filePath = wavFileList.get(position);
                File file = new File(filePath);
                if (file.exists()) {
                    systemPlay(file);
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
            wavFileList.clear();
            for (int i = 0; i < fileArr.size(); i++) {
                wavFileList.add(fileArr.get(i).getAbsolutePath());
            }
            updateProHandler.sendEmptyMessage(SEARCH_MESSAGE_CODE);
        }
    };

    // 系统播放
    private void systemPlay(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "audio/MP3");
        startActivity(intent);
    }
}