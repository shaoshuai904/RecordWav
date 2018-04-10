package com.maple.recordwav.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.maple.recorder.AudioChunk;
import com.maple.recorder.AudioRecordConfig;
import com.maple.recorder.OmRecorder;
import com.maple.recorder.PullTransport;
import com.maple.recorder.Recorder;
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
 * 录制 Pcm 界面
 *
 * @author maple
 * @time 16/4/18 下午2:53
 */
public class RecordPcmPage extends BaseFragment {
    @BindView(R.id.recordButton) Button recordButton;
    @BindView(R.id.pauseResumeButton) Button pauseResumeButton;
    @BindView(R.id.skipSilence) CheckBox skipSilence;

    Recorder recorder;
    boolean isRecording = false;
    String voicePath = WavApp.rootPath + "/voice.wav";

    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_record_wav, null);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        String name = "maple-" + DateUtils.date2Str("yyyy-MM-dd-HH-mm-ss");
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

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecording) {
                    recorder.startRecording();
                    isRecording = true;
                    skipSilence.setEnabled(false);
                    recordButton.setText(getString(R.string.stop));
                } else {
                    try {
                        recorder.stopRecording();
                        isRecording = false;
                        animateVoice(0);
                        skipSilence.setEnabled(true);
                        recordButton.setText(getString(R.string.record));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    pauseResumeButton.setText(getString(R.string.resume_recording));
                    recorder.pauseRecording();
                    isRecording = false;
                    pauseResumeButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateVoice(0);
                        }
                    }, 100);
                } else {
                    pauseResumeButton.setText(getString(R.string.pause_recording));
                    recorder.resumeRecording();
                    isRecording = true;
                }
            }
        });

    }

    private void setupRecorder() {
        recorder = OmRecorder.pcm(
                new File(voicePath),
                new AudioRecordConfig.Default(),
                new PullTransport.Default(
                        new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                            }
                        }
                )
        );
    }

    private void setupNoiseRecorder() {
        recorder = OmRecorder.pcm(
                new File(voicePath),
                new AudioRecordConfig.Default(),
                new PullTransport.Noise(
                        new PullTransport.OnAudioChunkPulledListener() {
                            @Override
                            public void onAudioChunkPulled(AudioChunk audioChunk) {
                                animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                            }
                        },
                        new Recorder.OnSilenceListener() {
                            @Override
                            public void onSilence(long silenceTime) {
                                Log.e("silenceTime", String.valueOf(silenceTime));
                                T.showShort(mContext, "silence of " + silenceTime + " detected");
                            }
                        },
                        200
                )
        );
    }

    private void animateVoice(final float maxPeak) {
        recordButton.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
    }


}