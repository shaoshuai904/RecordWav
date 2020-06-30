package com.maple.recorder.recording;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * 录音参数配置
 *
 * @author maple
 * @time 2018/4/10.
 */
public interface AudioRecordConfig {

    /**
     * 音频通道（声道数）
     * {@link AudioFormat#CHANNEL_IN_MONO} 单声道
     * {@link AudioFormat#CHANNEL_IN_STEREO} 立体声，所有设备可用
     */
    int channelConfig();

    /**
     * 音频源，详见 {@link MediaRecorder.AudioSource}
     */
    int audioSource();

    /**
     * 采样率 赫兹
     * 44100Hz 所有设备均可用
     * 22050Hz  16000Hz  11025Hz
     */
    int sampleRateInHz();

    /**
     * 音频数据格式
     * {@link AudioFormat#ENCODING_PCM_8BIT},每个样本8位
     * {@link AudioFormat#ENCODING_PCM_16BIT},每个样本16位，保证所有设备支持
     * {@link AudioFormat#ENCODING_PCM_FLOAT},每个样本 单精度Float
     */
    int audioFormat(); // audioFormat

    byte bitsPerSample();

    /**
     * Application should use this default implementation of {@link AudioRecordConfig} to configure
     * the Audio Record Source.
     */
    class Default implements AudioRecordConfig {
        private final int audioSource;
        private final int channelConfig;
        private final int sampleRateInHz;
        private final int audioFormat;

        public Default() {
            this(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT, AudioFormat.CHANNEL_IN_MONO, 44100);
        }

        public Default(int audioSource, int audioFormat, int channelConfig, int sampleRateInHz) {
            this.audioSource = audioSource;
            this.audioFormat = audioFormat;
            this.channelConfig = channelConfig;
            this.sampleRateInHz = sampleRateInHz;
        }

        @Override
        public int channelConfig() {
            return channelConfig;
        }

        @Override
        public int audioSource() {
            return audioSource;
        }

        @Override
        public int sampleRateInHz() {
            return sampleRateInHz;
        }

        @Override
        public int audioFormat() {
            return audioFormat;
        }

        @Override
        public byte bitsPerSample() {
            if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
                return 16;
            } else if (audioFormat == AudioFormat.ENCODING_PCM_8BIT) {
                return 8;
            } else {
                return 16;
            }
        }
    }
}
