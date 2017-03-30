package com.maple.recordwav.record;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.maple.recordwav.R;
import com.maple.recordwav.WavApp;
import com.maple.recordwav.base.BaseFragment;
import com.maple.recordwav.utils.DateUtils;
import com.maple.recordwav.utils.T;
import com.maple.recordwav.utils.permission.PermissionFragment;
import com.maple.recordwav.utils.permission.PermissionListener;

import java.io.File;

/**
 * 录制界面
 *
 * @author maple
 * @time 16/4/18 下午2:53
 */
public class RecordPage extends BaseFragment implements View.OnClickListener {
    @ViewInject(R.id.com_voice_time)
    private Chronometer com_voice_time;
    @ViewInject(R.id.iv_voice_img)
    private ImageView iv_voice_img;

    @ViewInject(R.id.bt_record)
    private Button bt_record;
    @ViewInject(R.id.bt_preview)
    private Button bt_preview;


    MapleAudioRecord extAudioRecorder = null;
    long timeWhenPaused = 0; // 已经记录的时间
    boolean isRecording = false;// 是否正在记录
    String voicePath = WavApp.rootPath + "/voice.wav";

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_record, null);
        ViewUtils.inject(this, view);

        bt_record.setText(getResources().getString(R.string.record));
        bt_preview.setText(getResources().getString(R.string.preview));

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        String name = "maple-" + DateUtils.date2Str("yyyy-MM-dd-HH-mm-ss");
        voicePath = WavApp.rootPath + name + ".wav";

        timeWhenPaused = 0;
        isRecording = false;

        bt_record.setEnabled(true);
        bt_preview.setEnabled(false);
    }

    @Override
    public void initListener() {

        bt_record.setOnClickListener(this);
        bt_preview.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_record:
                String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                PermissionFragment.getPermissionFragment(getActivity()).setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (isRecording) {
                            stopRecord();
                        } else {
                            startRecord();
                        }
                    }

                    @Override
                    public void onPermissionDenied(String[] deniedPermissions) {
                        T.showShort(mContext, "请打开内存读写权限");
                    }
                }).checkPermissions(permissions);
                break;
            case R.id.bt_preview:
                systemPlay(new File(voicePath));
                break;
        }
    }


    private void startRecord() {
        isRecording = true;
        com_voice_time.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
        com_voice_time.start();

        iv_voice_img.setImageResource(R.drawable.mic_selected);
        bt_record.setText(getResources().getString(R.string.stop));
        bt_record.setEnabled(true);
        bt_preview.setEnabled(false);
        // start
        extAudioRecorder = new MapleAudioRecord(voicePath);
        extAudioRecorder.start();
    }


    private void stopRecord() {
        isRecording = false;
        com_voice_time.stop();
        timeWhenPaused = 0;

        iv_voice_img.setImageResource(R.drawable.mic_default);
        bt_record.setText(getResources().getString(R.string.rerecord));
        bt_record.setEnabled(true);
        bt_preview.setEnabled(true);
        // stop
        extAudioRecorder.stop();
        extAudioRecorder.release();
        extAudioRecorder = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (extAudioRecorder != null) {
            extAudioRecorder.release();
            extAudioRecorder = null;
        }
    }

    // 系统播放
    private void systemPlay(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "audio/MP3");
        startActivity(intent);
    }
}