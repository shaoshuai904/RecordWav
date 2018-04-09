package com.maple.recordwav.ui;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;

import com.maple.recorder.AudioChunk;
import com.maple.recorder.AudioRecordConfig;
import com.maple.recorder.OmRecorder;
import com.maple.recorder.PullTransport;
import com.maple.recorder.PullableSource;
import com.maple.recorder.Recorder;
import com.maple.recorder.WriteAction;
import com.maple.recordwav.R;
import com.maple.recordwav.WavApp;
import com.maple.recordwav.base.BaseFragment;
import com.maple.recordwav.utils.DateUtils;
import com.maple.recordwav.utils.T;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 录制 Wav 界面
 *
 * @author maple
 * @time 16/4/18 下午2:53
 */
public class RecordWavPage extends BaseFragment {
    @BindView(R.id.com_voice_time) Chronometer com_voice_time;
    @BindView(R.id.recordButton) Button bt_start;
    @BindView(R.id.pauseResumeButton) Button pauseResumeButton;
    @BindView(R.id.skipSilence) CheckBox skipSilence;

    Recorder recorder;
    boolean isRecording = false;
    long curBase;
    String voicePath = WavApp.rootPath + "/voice.wav";

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_record_wav, null);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        String name = "wav-" + DateUtils.date2Str("yyyy-MM-dd-HH-mm-ss");
        voicePath = WavApp.rootPath + name + ".wav";

        setupRecorder();
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
                if (!isRecording) {
                    isRecording = true;
                    recorder.startRecording();
                    skipSilence.setEnabled(false);
                    bt_start.setText(getString(R.string.stop));
                    Log.e("time", "  --  " + SystemClock.elapsedRealtime());
                    com_voice_time.setBase(SystemClock.elapsedRealtime());
                    com_voice_time.start();
                } else {
                    try {
                        isRecording = false;
                        recorder.stopRecording();
                        skipSilence.setEnabled(true);
                        bt_start.setText(getString(R.string.record));
                        com_voice_time.stop();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    bt_start.post(new Runnable() {
                        @Override
                        public void run() {
                            animateVoice(0);
                        }
                    });
                }

            }
        });

        pauseResumeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isRecording) {
                    isRecording = false;
                    recorder.pauseRecording();
                    curBase = SystemClock.elapsedRealtime() - com_voice_time.getBase();
                    Log.e("time", "  -curBase-  " + curBase);
                    com_voice_time.stop();
                    pauseResumeButton.setText(getString(R.string.resume_recording));
                    pauseResumeButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateVoice(0);
                        }
                    }, 100);
                } else {
                    isRecording = true;
                    recorder.resumeRecording();
                    com_voice_time.setBase(SystemClock.elapsedRealtime() - curBase);
                    com_voice_time.start();
                    pauseResumeButton.setText(getString(R.string.pause_recording));
                }
            }
        });

    }

    private void setupRecorder() {
        recorder = OmRecorder.wav(
                new PullTransport.Default(
                        mic(),
                        new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                            }
                        }
                ),
                new File(voicePath)
        );
    }

    private void setupNoiseRecorder() {
        recorder = OmRecorder.wav(
                new PullTransport.Noise(
                        mic(),
                        new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                            }
                        },
                        new WriteAction.Default(),
                        new Recorder.OnSilenceListener() {
                            @Override
                            public void onSilence(long silenceTime) {
                                Log.e("silenceTime", String.valueOf(silenceTime));
                                T.showShort(mContext, "silence of " + silenceTime + " detected");
                            }
                        },
                        200
                ),
                new File(voicePath)
        );
    }


    private void animateVoice(float maxPeak) {
        bt_start.animate()
                .scaleX(1 + maxPeak)
                .scaleY(1 + maxPeak)
                .setDuration(10)
                .start();
    }

    private PullableSource mic() {
        return new PullableSource.Default(
                new AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC,
                        AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO,
                        44100
                )
        );
    }


}