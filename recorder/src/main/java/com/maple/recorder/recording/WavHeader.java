package com.maple.recorder.recording;

import android.media.AudioFormat;

/**
 * A Header to be appended to the end of WavRecorder audio file
 *
 * @author maple
 * @time 2018/4/10.
 */
final class WavHeader {
    private final AudioRecordConfig config;
    private final long totalAudioLength;

    WavHeader(AudioRecordConfig config, long totalAudioLength) {
        this.config = config;
        this.totalAudioLength = totalAudioLength;
    }

    /**
     * Returns the {@code WavHeader} in bytes.
     */
    public byte[] toBytes() {
        long sampleRateInHz = config.frequency();
        int channels = (config.channelPositionMask() == AudioFormat.CHANNEL_IN_MONO ? 1 : 2);
        byte bitsPerSample = config.bitsPerSample();
        return wavFileHeader(
                totalAudioLength - 44,
                totalAudioLength - 44 + 36,
                sampleRateInHz,
                channels,
                bitsPerSample * sampleRateInHz * channels / 8,
                bitsPerSample
        );
    }

    /**
     * 获取wav文件头
     *
     * @param totalAudioLen  -
     * @param totalDataLen   -
     * @param longSampleRate - 采样率
     * @param channels       - 通道数
     * @param byteRate       -
     * @param bitsPerSample  - 16/8 bit
     * @return
     */
    private byte[] wavFileHeader(long totalAudioLen, long totalDataLen, long longSampleRate,
                                 int channels, long byteRate, byte bitsPerSample) {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * (bitsPerSample / 8)); //
        // block align
        header[33] = 0;
        header[34] = bitsPerSample; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        return header;
    }
}
