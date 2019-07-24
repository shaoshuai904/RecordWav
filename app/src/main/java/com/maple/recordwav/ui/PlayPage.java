package com.maple.recordwav.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.maple.recorder.player.PlayDialog;
import com.maple.recorder.player.PlayUtils;
import com.maple.recordwav.R;
import com.maple.recordwav.WavApp;
import com.maple.recordwav.databinding.FragmentPlayBinding;
import com.maple.recordwav.utils.LoadingDialog;
import com.maple.recordwav.utils.SearchFileUtils;
import com.maple.recordwav.utils.T;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * wav 文件播放
 *
 * @author maple
 * @time 2016/5/20
 */
public class PlayPage extends BaseFragment {
    public static final int SEARCH_MESSAGE_CODE = 200;

    FragmentPlayBinding binding;
    private List<String> wavFileList = new ArrayList<>();
    private LoadingDialog loadingDialog;
    PlayUtils playUtils;

    Handler updateProHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == SEARCH_MESSAGE_CODE) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (wavFileList.size() > 0) {
                    binding.tvDes.setText("点击条目，播放wav文件！");
                } else {
                    binding.tvDes.setText("没有找到文件，请去录制 ！");
                }
                ((ArrayAdapter) binding.lvWav.getAdapter()).notifyDataSetChanged();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_play, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    public void initView() {
        loadingDialog = new LoadingDialog(getActivity());
        binding.tvDes.setText("WAV 播放界面！");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, wavFileList);
        binding.lvWav.setAdapter(adapter);
        binding.lvWav.setOnItemClickListener((parent, view, position, id) -> {
            String filePath = wavFileList.get(position);
            File file = new File(filePath);
            if (file.exists()) {
                dialogPlay(file);
            } else {
                T.showShort(mContext, "选择的文件不存在");
            }
        });

        searchFile();
    }

    private void searchFile() {
        new Thread(() -> {
            List<File> fileArr = SearchFileUtils.search(new File(WavApp.rootPath), new String[]{".wav"});
            wavFileList.clear();
            for (int i = 0; i < fileArr.size(); i++) {
                wavFileList.add(fileArr.get(i).getAbsolutePath());
            }
            updateProHandler.sendEmptyMessage(SEARCH_MESSAGE_CODE);
        }).start();
        loadingDialog.show("搜索中...");
    }

    // 系统播放
    private void systemPlay(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "audio/MP3");
        startActivity(intent);
    }

    private void dialogPlay(File file) {
        new PlayDialog(getActivity())
                .addWavFile(file)
                .showDialog();
    }

    private void uitlsPlay(File file) {
        if (playUtils == null) {
            playUtils = new PlayUtils();
            playUtils.setPlayStateChangeListener(isPlay -> {
                if (isPlay) {
                    // startTimer
//                    com_voice_time.setBase(SystemClock.elapsedRealtime());
//                    com_voice_time.start();
//                    bt_preview.setText(getResources().getString(R.string.stop));
//                    iv_voice_img.setImageResource(R.drawable.mic_selected);
                } else {
//                    com_voice_time.stop();
//                    com_voice_time.setBase(SystemClock.elapsedRealtime());
//                    bt_preview.setText(getResources().getString(R.string.preview));
//                    iv_voice_img.setImageResource(R.drawable.mic_default);
                }
            });
        }
        if (playUtils.isPlaying()) {
            playUtils.stopPlaying();
        } else {
            playUtils.startPlaying(file.getPath());
        }
    }


}