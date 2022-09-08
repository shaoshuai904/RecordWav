package com.maple.recorder.recording;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * 录音参数配置
 *
 * @author maple
 * @time 2018/4/10.
 */
public class AudioRecordConfig {
    /**
     * 音频源，详见 {@link MediaRecorder.AudioSource}
     */
    private int audioSource = MediaRecorder.AudioSource.MIC;

    /**
     * 采样率 赫兹
     * - 44100Hz 所有设备均可用
     * - 22050Hz  16000Hz  11025Hz
     */
    private int sampleRateInHz = 44100;

    /**
     * 音频通道（声道数）
     * - {@link AudioFormat#CHANNEL_IN_MONO} 单声道
     * - {@link AudioFormat#CHANNEL_IN_STEREO} 立体声，所有设备可用
     */
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;

    /**
     * 音频数据格式
     * - {@link AudioFormat#ENCODING_PCM_8BIT},每个样本8位
     * - {@link AudioFormat#ENCODING_PCM_16BIT},每个样本16位，保证所有设备支持
     * - {@link AudioFormat#ENCODING_PCM_FLOAT},每个样本 单精度Float
     */
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;


    public AudioRecordConfig() {
    }

    public AudioRecordConfig(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat) {
        this.audioSource = audioSource;
        this.sampleRateInHz = sampleRateInHz;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;
    }

    public byte bitsPerSample() {
        if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
            return 16;
        } else if (audioFormat == AudioFormat.ENCODING_PCM_8BIT) {
            return 8;
        } else {
            return 16;
        }
    }

    // -------------------------- get/set ----------------------------------

    public int getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(int channelConfig) {
        this.channelConfig = channelConfig;
    }

    public int getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(int audioSource) {
        this.audioSource = audioSource;
    }

    public int getSampleRateInHz() {
        return sampleRateInHz;
    }

    public void setSampleRateInHz(int sampleRateInHz) {
        this.sampleRateInHz = sampleRateInHz;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }

    @Override
    public String toString() {
        return "录音参数配置: \n{" +
                "audioSource=" + audioSource +
                ", sampleRateInHz=" + sampleRateInHz +
                ", channelConfig=" + channelConfig +
                ", audioFormat=" + audioFormat +
                '}';
    }
}
