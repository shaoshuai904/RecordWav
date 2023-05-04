package com.maple.recordwav.ui;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.maple.recorder.recording.AudioChunk;
import com.maple.recorder.recording.AudioRecordConfig;
import com.maple.recorder.recording.MsRecorder;
import com.maple.recorder.recording.PullTransport;
import com.maple.recorder.recording.Recorder;
import com.maple.recordwav.R;
import com.maple.recordwav.WavApp;
import com.maple.recordwav.base.BaseFragment;
import com.maple.recordwav.utils.DateUtils;
import com.maple.recordwav.utils.T;

import java.io.File;

/**
 * 录制 WavRecorder 界面
 *
 * @author maple
 * @time 16/4/18 下午2:53
 */
public class RecordPage extends BaseFragment {
    ImageView iv_voice_img;
    Chronometer com_voice_time;
    Button bt_start;
    Button bt_stop;
    Button bt_pause_resume;
    CheckBox skipSilence;

    Recorder recorder;
    boolean isRecording = false;
    long curBase = 0;
    String voicePath = WavApp.rootPath + "/voice.wav";

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_record, null);
        iv_voice_img = view.findViewById(R.id.iv_voice_img);
        com_voice_time = view.findViewById(R.id.com_voice_time);
        bt_start = view.findViewById(R.id.bt_start);
        bt_stop = view.findViewById(R.id.bt_stop);
        bt_pause_resume = view.findViewById(R.id.bt_pause_resume);
        skipSilence = view.findViewById(R.id.skipSilence);

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        String name = "wav-" + DateUtils.date2Str("yyyy-MM-dd-HH-mm-ss");
        voicePath = WavApp.rootPath + name + ".wav";

        setupRecorder();
        bt_start.setText(getString(R.string.record));
        bt_pause_resume.setText(getString(R.string.pause));
        bt_stop.setText(getString(R.string.stop));
        bt_pause_resume.setEnabled(false);
        bt_stop.setEnabled(false);
    }

    @Override
    public void initListener() {
        skipSilence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    setupNoiseRecorder();
                } else {
                    setupRecorder();
                }
            }
        });
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recorder.startRecording();

                isRecording = true;
                skipSilence.setEnabled(false);
                bt_start.setEnabled(false);
                bt_pause_resume.setEnabled(true);
                bt_stop.setEnabled(true);
                iv_voice_img.setImageResource(R.drawable.mic_selected);
                com_voice_time.setBase(SystemClock.elapsedRealtime());
                com_voice_time.start();
            }
        });
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recorder.stopRecording();

                isRecording = false;
                skipSilence.setEnabled(true);
                bt_start.setEnabled(true);
                bt_pause_resume.setEnabled(false);
                bt_stop.setEnabled(false);
                bt_pause_resume.setText(getString(R.string.pause));
                iv_voice_img.setImageResource(R.drawable.mic_default);
                com_voice_time.stop();
                curBase = 0;
                bt_stop.post(new Runnable() {
                    @Override
                    public void run() {
                        animateVoice(0);
                    }
                });
            }
        });
        bt_pause_resume.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isRecording) {
                    recorder.pauseRecording();

                    isRecording = false;
                    bt_pause_resume.setText(getString(R.string.resume));
                    curBase = SystemClock.elapsedRealtime() - com_voice_time.getBase();
                    com_voice_time.stop();
                    iv_voice_img.setImageResource(R.drawable.mic_default);
                    bt_pause_resume.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateVoice(0);
                        }
                    }, 100);
                } else {
                    recorder.resumeRecording();

                    isRecording = true;
                    bt_pause_resume.setText(getString(R.string.pause));
                    com_voice_time.setBase(SystemClock.elapsedRealtime() - curBase);
                    com_voice_time.start();
                    iv_voice_img.setImageResource(R.drawable.mic_selected);
                }
            }
        });

    }

    private void setupRecorder() {
        recorder = MsRecorder.wav(
                new File(voicePath),
                new AudioRecordConfig.Default(),
                new PullTransport.Default()
                        .setOnAudioChunkPulledListener(new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                Log.e("max  ", "amplitude: " + audioChunk.maxAmplitude());
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                            }
                        })

        );
    }

    private void setupNoiseRecorder() {
        recorder = MsRecorder.wav(
                new File(voicePath),
                new AudioRecordConfig.Default(),
                new PullTransport.Noise()
                        .setOnAudioChunkPulledListener(new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                            }
                        })
                        .setOnSilenceListener(new PullTransport.OnSilenceListener() {
                            @Override
                            public void onSilence(long silenceTime, long discardTime) {
                                String message = "沉默时间：" + String.valueOf(silenceTime) +
                                        " ,丢弃时间：" + String.valueOf(discardTime);
                                Log.e("silenceTime", message);
                                T.showShort(mContext, message);
                            }
                        })


        );
    }


    private void animateVoice(float maxPeak) {
        if (maxPeak > 0.5f) {
            return;
        }
        iv_voice_img.animate()
                .scaleX(1 + maxPeak)
                .scaleY(1 + maxPeak)
                .setDuration(10)
                .start();
    }


}