package com.maple.recordwav.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.maple.msdialog.adapter.BaseQuickAdapter;
import com.maple.recorder.parse.WaveFileReader;
import com.maple.recorder.player.PlayDialog;
import com.maple.recorder.player.PlayUtils;
import com.maple.recordwav.WavApp;
import com.maple.recordwav.databinding.FragmentAudioListBinding;
import com.maple.recordwav.utils.SearchFileUtils;
import com.maple.recordwav.utils.T;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * wav文件 播放/解析 (Java版)
 */
public class PlayParsePageJava extends BaseFragment {
    private FragmentAudioListBinding binding;
    private AudioAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAudioListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        searchFile();
    }

    private void initView() {
        mAdapter = new AudioAdapter(mContext);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener<File>() {
            @Override
            public void onItemClick(File item, int position) {
                dialogPlay(item);
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onLongClick(View v, int position) {
                getWavInfo(mAdapter.getItem(position));
                return true;
            }
        });

        binding.tvInfo.setText("WAV 播放界面！");
        binding.rvVideo.setAdapter(mAdapter);
        binding.srlRefreshLayout.setRefreshHeader(new ClassicsHeader(mContext))
                .setOnRefreshListener(new OnRefreshListener() {
                    @Override
                    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                        searchFile();
                    }
                })
                .setEnableLoadMore(false);

    }

    @SuppressLint("CheckResult")
    private void searchFile() {
        Observable.just(WavApp.saveFile)
                .map(new Function<File, List<File>>() {
                    @Override
                    public List<File> apply(@NonNull File file) throws Exception {
                        List<File> files = SearchFileUtils.search(file, new String[]{".wav"});
                        Collections.sort(files, new Comparator<File>() {
                            @Override
                            public int compare(File o1, File o2) {
                                return o2.getName().compareTo(o1.getName());
                            }
                        });
                        return files;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<File>>() {
                    @Override
                    public void accept(List<File> files) throws Exception {
                        updateVideoData(files);
                    }
                });

    }

    private void updateVideoData(List<File> files) {
        if (files != null && files.size() > 0) {
            binding.tvInfo.setText("点击条目播放wav文件, \n长按条目获取文件详细信息！");
        } else {
            binding.tvInfo.setText("没有找到文件，请去录制 ！");
        }
        binding.srlRefreshLayout.finishRefresh();
        mAdapter.refreshData(files);
    }

    //----------------------------------播放----------------------------------------
    // 1：系统播放
    private void systemPlay(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "audio/MP3");
        startActivity(intent);
    }

    // 2：dialog播放
    private void dialogPlay(File file) {
        new PlayDialog(getActivity())
                .addWavFile(file)
                .showDialog();
    }

    private PlayUtils playUtils;

    // 3：工具类播放
    private void utilPlay(File file) {
        if (playUtils == null) {
            playUtils = new PlayUtils();
            playUtils.setPlayStateChangeListener(new PlayUtils.PlayStateChangeListener() {
                @Override
                public void onPlayStateChange(boolean isPlay) {
                    if (isPlay) {
//                        // startTimer
//                        com_voice_time.setBase(SystemClock.elapsedRealtime());
//                        com_voice_time.start();
//                        bt_preview.setText(getResources().getString(R.string.stop));
//                        iv_voice_img.setImageResource(R.drawable.mic_selected);
                    } else {
//                        com_voice_time.stop();
//                        com_voice_time.setBase(SystemClock.elapsedRealtime());
//                        bt_preview.setText(getResources().getString(R.string.preview));
//                        iv_voice_img.setImageResource(R.drawable.mic_default);
                    }
                }
            });
        }
        if (playUtils.isPlaying()) {
            playUtils.stopPlaying();
        } else {
            playUtils.startPlaying(file.getPath());
        }
    }

    //----------------------------------解析----------------------------------------

    private void getWavInfo(File file) {
        String filename = file.getAbsolutePath();
        WaveFileReader reader = new WaveFileReader(filename);
        if (reader.isSuccess()) {
            String des = ("读取wav文件信息：" + filename
                    + "\n采样率：" + reader.getSampleRate()
                    + "\n声道数：" + reader.getNumChannels()
                    + "\n编码长度：" + reader.getBitPerSample()
                    + "\n数据长度：" + reader.getDataLen());
            binding.tvInfo.setText(des);

        } else {
            T.showShort(mContext, filename + "不是一个正常的wav文件");
        }
    }

}
