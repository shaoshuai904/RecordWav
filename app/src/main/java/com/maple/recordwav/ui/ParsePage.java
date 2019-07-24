package com.maple.recordwav.ui;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.maple.recorder.parse.WaveFileReader;
import com.maple.recordwav.R;
import com.maple.recordwav.WavApp;
import com.maple.recordwav.databinding.FragmentParseBinding;
import com.maple.recordwav.utils.LoadingDialog;
import com.maple.recordwav.utils.SearchFileUtils;
import com.maple.recordwav.utils.T;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取wav文件的信息
 *
 * @author maple
 * @time 2016/5/20
 */
public class ParsePage extends BaseFragment {
    FragmentParseBinding binding;
    private List<String> wavFilelist = new ArrayList<String>();
    private LoadingDialog loadingDialog;


    Handler updateProHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 200) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (wavFilelist.size() > 0) {
                    binding.tvInfo.setText("点击条目，获取wav文件的信息 ！");
                } else {
                    binding.tvInfo.setText("没有找到文件，请去录制 ！");
                }
                ((ArrayAdapter) binding.lvParse.getAdapter()).notifyDataSetChanged();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_parse, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    public void initView() {
        binding.tvInfo.setText("WAV 解析界面！");

        loadingDialog = new LoadingDialog(getActivity());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, wavFilelist);
        binding.lvParse.setAdapter(adapter);
        binding.lvParse.setOnItemClickListener((parent, v, position, id) -> {
            String filePath = wavFilelist.get(position);
            if (new File(filePath).exists()) {
                getWavInfo(filePath);
            } else {
                T.showShort(mContext, "选择的文件不存在");
            }
        });

        searchFile();
    }

    private void searchFile() {
        loadingDialog.show("搜索中...");
        new Thread(() -> {
            List<File> fileArr = SearchFileUtils.search(new File(WavApp.rootPath), new String[]{".wav"});
            wavFilelist.clear();
            for (int i = 0; i < fileArr.size(); i++) {
                wavFilelist.add(fileArr.get(i).getAbsolutePath());
            }
            updateProHandler.sendEmptyMessage(200);
        }).start();
    }

    public void getWavInfo(String filename) {
        WaveFileReader reader = new WaveFileReader(filename);
        if (reader.isSuccess()) {
            binding.tvInfo.setText("读取wav文件信息：" + filename
                    + "\n采样率：" + reader.getSampleRate()
                    + "\n声道数：" + reader.getNumChannels()
                    + "\n编码长度：" + reader.getBitPerSample()
                    + "\n数据长度：" + reader.getDataLen());
        } else {
            T.showShort(mContext, filename + "不是一个正常的wav文件");
        }
    }
}