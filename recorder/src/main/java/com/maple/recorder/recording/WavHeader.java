package com.maple.recorder.recording;

import android.media.AudioFormat;

/**
 * WAV文件头工具类
 *
 * @author maple
 * @time 2018/4/10.
 */
public class WavHeader {
    private final AudioRecordConfig config;// wav录音配置参数
    private final long totalAudioLength;// 音频数据总长度

    WavHeader(AudioRecordConfig config, long totalAudioLength) {
        this.config = config;
        this.totalAudioLength = totalAudioLength;
    }

    /**
     * 返回WAV文件头的byte数组
     */
    public byte[] toBytes() {
        long sampleRateInHz = config.getSampleRateInHz();
        int channels = (config.getChannelConfig() == AudioFormat.CHANNEL_IN_MONO ? 1 : 2);
        byte bitsPerSample = config.bitsPerSample();
        return wavFileHeader(
                totalAudioLength - 44,
                totalAudioLength - 8,
                sampleRateInHz,
                channels,
                bitsPerSample * sampleRateInHz * channels / 8,
                bitsPerSample
        );
    }

    /**
     * 获取wav文件头
     *
     * @param totalAudioLen  - 音频数据总长度
     * @param totalDataLen   - 文件总长度-8
     * @param longSampleRate - 采样率
     * @param channels       - 通道数
     * @param byteRate       - 每秒数据字节数
     * @param bitsPerSample  - 采样位数，16/8 bit
     * @return 文件头
     */
    private byte[] wavFileHeader(long totalAudioLen, long totalDataLen, long longSampleRate,
                                 int channels, long byteRate, byte bitsPerSample) {
        byte[] header = new byte[44];
        // --- RIFF区块 ---
        // 文档标识: 大写字符串"RIFF"，标明该文件为有效的 RIFF 格式文档。
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        // 文件数据长度: 从下一个字段首地址开始到文件末尾的总字节数。该值 = fileSize - 8。
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        // 文件格式类型: 所有 WAV 格式的文件此处为字符串"WAVE"，标明该文件是 WAV 格式文件。
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        // --- FORMAT区块 ---
        // 格式块标识: 小写字符串"fmt "。
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        // 格式块长度: 取决于编码格式，可以是 16、18、20、40 等
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        // AudioFormat(音频格式): 常见的 PCM 音频数据的值为1。
        header[20] = 1;
        header[21] = 0;
        // NumChannels(声道数): 1：单声道，2：双声道/立体声
        header[22] = (byte) channels;
        header[23] = 0;
        // SampleRate(采样率): 每个声道单位时间采样次数。常用的采样频率有 11025, 22050 和 44100 kHz。
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        // ByteRate(数据传输速率): 每秒数据字节数，该数值为:声道数×采样频率×采样位数/8。
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // BlockAlign(数据块对齐): 采样帧大小。该数值为:声道数×采样位数/8。
        header[32] = (byte) (channels * (bitsPerSample / 8));
        header[33] = 0;
        // BitsPerSample(采样位数): 每个采样存储的bit数。常见的位数有 8、16、32
        header[34] = bitsPerSample;
        header[35] = 0;

        // --- DATA区块 ---
        // 标识: 标示头结束，开始数据区域。
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        // 音频数据长度: N = ByteRate * seconds
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        return header;
    }
}
