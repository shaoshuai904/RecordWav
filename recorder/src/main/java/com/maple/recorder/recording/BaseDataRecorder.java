package com.maple.recorder.recording;

import android.media.AudioRecord;

import androidx.annotation.RequiresPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base Recorder (Only record the original audio data.)
 *
 * @author maple
 * @time 2018/4/10.
 */
public class BaseDataRecorder implements Recorder {
    protected File file;
    protected AudioRecordConfig config;
    protected PullTransport pullTransport;
    protected int bufferSizeInBytes;// 缓冲区大小
    private AudioRecord audioRecord;
    private OutputStream outputStream;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    protected BaseDataRecorder(File file, AudioRecordConfig config, PullTransport pullTransport) throws IllegalArgumentException {
        this.file = file;
        this.config = config;
        this.pullTransport = pullTransport;
        // 计算缓冲区大小
        this.bufferSizeInBytes = AudioRecord.getMinBufferSize(
                config.getSampleRateInHz(),
                config.getChannelConfig(),
                config.getAudioFormat()
        );
        if (audioRecord == null) {
            audioRecord = new AudioRecord(config.getAudioSource(), config.getSampleRateInHz(),
                    config.getChannelConfig(), config.getAudioFormat(), bufferSizeInBytes);
        }
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            throw new IllegalStateException("AudioRecord 初始化失败，请检查是否有RECORD_AUDIO权限，或者使用了系统APP才能用的配置项（MediaRecorder.AudioSource.REMOTE_SUBMIX 等）。");
        }
    }

    @Override
    public void startRecording() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                startRecord();
            }
        });
    }

    private void startRecord() {
        try {
            if (outputStream == null) {
                outputStream = new FileOutputStream(file);
            }
            audioRecord.startRecording();
            pullTransport.isEnableToBePulled(true);
            pullTransport.startPoolingAndWriting(audioRecord, bufferSizeInBytes, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pauseRecording() {
        pullTransport.isEnableToBePulled(false);
    }

    @Override
    public void resumeRecording() {
        startRecording();
    }

    @Override
    public void stopRecording() {
        pauseRecording();

        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        if (outputStream != null) {
            try {
                outputStream.flush();
                outputStream.close();
                outputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
