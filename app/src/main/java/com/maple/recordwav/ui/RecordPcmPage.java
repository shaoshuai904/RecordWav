package com.maple.recordwav.ui;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

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
 * 录制 Pcm 界面
 *
 * @author maple
 * @time 16/4/18 下午2:53
 */
public class RecordPcmPage extends BaseFragment {
    @BindView(R.id.recordButton) ImageView recordButton;
    @BindView(R.id.stopButton) ImageView stopButton;
    @BindView(R.id.skipSilence) CheckBox skipSilence;
    @BindView(R.id.pauseResumeButton) Button pauseResumeButton;

    Recorder recorder;
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
                recorder.startRecording();
                skipSilence.setEnabled(false);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    recorder.stopRecording();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                animateVoice(0);
                skipSilence.setEnabled(true);
            }
        });
        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            boolean isPaused = false;

            @Override
            public void onClick(View view) {
                if (recorder == null) {
                    T.showShort(mContext, "Please start recording first!");
                    return;
                }
                if (!isPaused) {
                    pauseResumeButton.setText(getString(R.string.resume_recording));
                    recorder.pauseRecording();
                    pauseResumeButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateVoice(0);
                        }
                    }, 100);
                } else {
                    pauseResumeButton.setText(getString(R.string.pause_recording));
                    recorder.resumeRecording();
                }
                isPaused = !isPaused;
            }
        });

    }


    private void setupRecorder() {
        recorder = OmRecorder.pcm(
                new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override
                    public void onAudioChunkPulled(AudioChunk audioChunk) {
                        animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                    }
                }), new File(voicePath));
    }

    private void setupNoiseRecorder() {
        recorder = OmRecorder.pcm(
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
                ), new File(voicePath)
        );
    }

    private void animateVoice(final float maxPeak) {
        recordButton.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
    }

    private PullableSource mic() {
        return new PullableSource.Default(
                new AudioRecordConfig.Default(
                        MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                        AudioFormat.CHANNEL_IN_MONO, 44100
                )
        );
    }


}