package com.maple.recorder.recording;

import androidx.annotation.RequiresPermission;

import java.io.File;

/**
 * MsRecorder 基本API
 *
 * @author maple
 * @time 2018/4/10.
 */
public class MsRecorder {

    private MsRecorder() {
    }

    /**
     * 获取 pcm 格式的音频记录器
     *
     * @param file          保存录音的文件
     * @param config        录音参数配置
     * @param pullTransport 数据推送器
     * @return pcm格式的音频记录器
     */
    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    public static Recorder pcm(File file, AudioRecordConfig config, PullTransport pullTransport) throws IllegalArgumentException {
        return new PcmRecorder(file, config, pullTransport);
    }

    /**
     * 获取 wav 格式的音频记录器
     *
     * @param file          保存录音的文件
     * @param config        录音参数配置
     * @param pullTransport 数据推送器
     * @return wav格式的音频记录器
     */
    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    public static Recorder wav(File file, AudioRecordConfig config, PullTransport pullTransport) throws IllegalArgumentException {
        return new WavRecorder(file, config, pullTransport);
    }
}
