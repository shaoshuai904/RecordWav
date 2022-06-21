package com.maple.recordwav.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.maple.popups.lib.MsNormalPopup;
import com.maple.popups.lib.MsPopup;
import com.maple.popups.utils.DensityUtils;
import com.maple.recorder.recording.AudioChunk;
import com.maple.recorder.recording.AudioRecordConfig;
import com.maple.recorder.recording.MsRecorder;
import com.maple.recorder.recording.PullTransport;
import com.maple.recorder.recording.Recorder;
import com.maple.recordwav.R;
import com.maple.recordwav.WavApp;
import com.maple.recordwav.databinding.FragmentRecordBinding;
import com.maple.recordwav.utils.DateUtils;
import com.maple.recordwav.utils.RecordConfigView;
import com.maple.recordwav.utils.T;

import java.io.File;

/**
 * 录制 WavRecorder 界面 (Java版)
 */
public class RecordPageJava extends BaseFragment {
    private FragmentRecordBinding binding;
    private Recorder recorder;
    private AudioRecordConfig recordConfig = new AudioRecordConfig();// 参数配置
    private boolean isRecording = false;
    private long curBase = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createRecorder();
        updateRecordStatusUI(RecordStatus.NoStart);

        binding.btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.startRecording();
                updateRecordStatusUI(RecordStatus.Recording);
            }
        });
        binding.btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stopRecording();
                updateRecordStatusUI(RecordStatus.Stop);
            }
        });
        binding.btPauseResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    recorder.pauseRecording();
                    updateRecordStatusUI(RecordStatus.Pause);
                } else {
                    recorder.resumeRecording();
                    updateRecordStatusUI(RecordStatus.Resume);
                }
            }
        });
        binding.skipSilence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                createRecorder(isChecked);
            }
        });
        binding.tvRecordConfig.setText(recordConfig.toString());
        binding.tvRecordConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfigWindow(v);
            }
        });
    }

    enum RecordStatus {
        NoStart,
        Recording,
        Pause,
        Resume,
        Stop
    }

    // update UI
    private void updateRecordStatusUI(RecordStatus status) {
        switch (status) {
            case NoStart:// 未开始
            case Stop:// 停止
                isRecording = false;
                binding.skipSilence.setEnabled(true);
                binding.btStart.setEnabled(true);
                binding.btPauseResume.setEnabled(false);
                binding.btStop.setEnabled(false);
                binding.btPauseResume.setText(getString(R.string.pause));
                binding.ivVoiceImg.setImageResource(R.drawable.mic_default);
                // time
                curBase = 0;
                binding.comVoiceTime.stop();
                animateVoice(0f);
                break;
            case Pause:// 暂停
                isRecording = false;
                binding.skipSilence.setEnabled(false);
                binding.btStart.setEnabled(false);
                binding.btPauseResume.setEnabled(true);
                binding.btStop.setEnabled(true);
                binding.btPauseResume.setText(getString(R.string.resume));
                binding.ivVoiceImg.setImageResource(R.drawable.mic_default);
                // time
                curBase = SystemClock.elapsedRealtime() - binding.comVoiceTime.getBase();
                binding.comVoiceTime.stop();
                animateVoice(0f);
                break;
            case Recording:// 录制ing
            case Resume:// 重新开始
                isRecording = true;
                binding.skipSilence.setEnabled(false);
                binding.btStart.setEnabled(false);
                binding.btPauseResume.setEnabled(true);
                binding.btStop.setEnabled(true);
                binding.btPauseResume.setText(getString(R.string.pause));
                binding.ivVoiceImg.setImageResource(R.drawable.mic_selected);
                // time
                binding.comVoiceTime.setBase(SystemClock.elapsedRealtime() - curBase);
                binding.comVoiceTime.start();
                break;
        }
    }

    private void createRecorder() {
        createRecorder(binding.skipSilence.isChecked());
    }

    private void createRecorder(Boolean skipSilence) {
        if (skipSilence) {
            recorder = getNoiseRecorder();
        } else {
            recorder = getRecorder();
        }
    }

    // 获取普通录音机
    private Recorder getRecorder() {
        return MsRecorder.wav(
                new File(getVoicePath()),
                recordConfig,
                // new AudioRecordConfig(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT, AudioFormat.CHANNEL_IN_MONO, 44100),
                // 普通录音模式
                new PullTransport.Default()
                        .setOnAudioChunkPulledListener(new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                Log.d("数据监听", "最大值 : ${audioChunk.maxAmplitude()} ");
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                            }
                        })
        );
    }

    // 获取降噪录音机，跳过沉默区，只录"有声音"的部分
    private Recorder getNoiseRecorder() {
        return MsRecorder.wav(
                new File(getVoicePath()),
                recordConfig,
                new PullTransport.Noise()// 跳过静音区
                        .setOnAudioChunkPulledListener(new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                Log.d("数据监听", "最大值: " + audioChunk.maxAmplitude());
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                            }
                        })
                        .setOnSilenceListener(new PullTransport.OnSilenceListener() {
                            @Override
                            public void onSilence(long silenceTime, long discardTime) {
                                String message = "沉默时间：" + silenceTime + " ,丢弃时间：" + discardTime;
                                Log.d("降噪模式", message);
                                T.showShort(mContext, message);
                            }
                        })
        );
    }

    // 录音文件存储名称
    private String getVoicePath() {
        String name = "wav-" + DateUtils.date2Str("yyyy-MM-dd-HH-mm-ss");
        String filePath = WavApp.saveFile.getAbsolutePath() + "/" + name + ".wav";
        Log.d("maple_log", "filePath: " + filePath);
        return filePath;
    }

    // 做点缩放动画
    private void animateVoice(float maxPeak) {
        if (maxPeak < 0f || maxPeak > 0.5f) {
            return;
        }
        binding.ivVoiceImg.animate()
                .scaleX(1 + maxPeak)
                .scaleY(1 + maxPeak)
                .setDuration(10)
                .start();
    }

    // 显示录音参数配置窗口
    private void showConfigWindow(View view) {
        if (!binding.btStart.isEnabled()) {
            // 正在录制中，不可用
            return;
        }
        RecordConfigView configView = new RecordConfigView(mContext);
        configView.updateConfig(recordConfig);
        configView.setOnConfigChangeListener(new RecordConfigView.OnRecordConfigChangeListener() {
            @Override
            public void onConfigChange(@NonNull AudioRecordConfig config) {
                recordConfig = config;
                binding.tvRecordConfig.setText(recordConfig.toString());
                createRecorder();
            }
        });
        new MsPopup(mContext, ViewGroup.LayoutParams.MATCH_PARENT)
                .setContextView(configView)
                .arrow(true)
                .shadow(true)
                .borderColor(Color.BLACK)
                .borderWidth(1)
                .dimAmount(0.3f)
                .edgeProtection(DensityUtils.dp2px(mContext, 10f))
                .preferredDirection(MsNormalPopup.Direction.TOP)
                .show(view);
    }
}
