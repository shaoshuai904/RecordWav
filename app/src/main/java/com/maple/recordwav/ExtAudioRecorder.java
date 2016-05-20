package com.maple.recordwav;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ExtAudioRecorder {
    private static final String TAG = ExtAudioRecorder.class.getName();
    // 采样率
    private final static int[] sampleRates = {44100, 22050, 11025, 8000};
    // 记录样本输出到文件的时间间隔
    private static final int TIMER_INTERVAL = 120;

    private int aSource; // 声音源
    private int sRate; // 采样率
    private int aFormat; // 编码长度
    private short bSamples; // 编码长度对应数字
    private short nChannels; // 声道数

    // 用于无损音质的记录器
    private AudioRecord audioRecorder = null;
    // File writer
    private RandomAccessFile randomAccessWriter;
    // 输出文件路径
    private String filePath = null;

    // Number of frames written to file on each output
    private int framePeriod;
    // Buffer for output
    private byte[] buffer;
    // 语音数据的长度
    private int payloadSize;
    // 最小缓冲区大小
    private int bufferSize;
    // 记录状态
    public State state;

    public enum State {
        INITIALIZING,// 初始化
        READY,// 已经初始化，但没有开始
        RECORDING,// 记录ing
        ERROR,// 需要重建
        STOPPED// 需要重置
    }


    private static ExtAudioRecorder eAudioRecorder = null;

    public static ExtAudioRecorder getInstance(String argPath) {
        if (eAudioRecorder == null) {
            eAudioRecorder = new ExtAudioRecorder(
                    argPath,
                    MediaRecorder.AudioSource.MIC,
                    sampleRates[3],
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
        }
        return eAudioRecorder;
    }

    /**
     * Instantiates a new recorder, in case of compressed recording the parameters can be left as 0.
     * In case of errors, no exception is thrown, but the state is set to ERROR
     *
     * @param argPath       文件路径
     * @param audioSource   声音源
     * @param sampleRate    采样率
     * @param channelConfig 声道数
     * @param audioFormat   编码长度
     */
    private ExtAudioRecorder(String argPath, int audioSource, int sampleRate, int channelConfig, int audioFormat) {
        try {
            aSource = audioSource;
            sRate = sampleRate;
            aFormat = audioFormat;

            bSamples = (short) ((audioFormat == AudioFormat.ENCODING_PCM_16BIT) ? 16 : 8);
            nChannels = (short) ((channelConfig == AudioFormat.CHANNEL_CONFIGURATION_MONO) ? 1 : 2);

            filePath = argPath;
            framePeriod = sRate * TIMER_INTERVAL / 1000;
            bufferSize = framePeriod * 2 * bSamples * nChannels / 8;
            if (bufferSize < AudioRecord.getMinBufferSize(sRate, channelConfig, aFormat)) {
                // Check to make sure buffer size is not smaller than the smallest allowed one
                bufferSize = AudioRecord.getMinBufferSize(sRate, channelConfig, aFormat);
                Log.e(TAG, "增加缓存大小到： " + Integer.toString(bufferSize));

                // Set frame period and timer interval accordingly
                framePeriod = bufferSize / (2 * bSamples * nChannels / 8);
            }
            audioRecorder = new AudioRecord(aSource, sRate, channelConfig, aFormat, bufferSize);
            if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED)
                throw new Exception("AudioRecord initialization failed");
            audioRecorder.setRecordPositionUpdateListener(updateListener);
            audioRecorder.setPositionNotificationPeriod(framePeriod);

            state = State.INITIALIZING;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            state = State.ERROR;
        }

    }

    /**
     * Method used for recording.
     */
    private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener() {
        public void onPeriodicNotification(AudioRecord recorder) {
            audioRecorder.read(buffer, 0, buffer.length); // Fill buffer
            try {
                randomAccessWriter.write(buffer); // Write buffer to file
                payloadSize += buffer.length;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        public void onMarkerReached(AudioRecord recorder) {
            // NOT USED
        }
    };


    /**
     * Prepares the recorder for recording, in case the recorder is not in the INITIALIZING state and
     * the file path was not set the recorder is set to the ERROR state, which makes a reconstruction necessary.
     * In case uncompressed recording is toggled, the header of the wave file is written.
     * In case of an exception, the state is changed to ERROR.
     */
    public void prepare() {
        try {
            if (state == State.INITIALIZING) {
                if ((audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) & (filePath != null)) {
                    // 写文件头
                    randomAccessWriter = new RandomAccessFile(filePath, "rw");
                    randomAccessWriter.setLength(0); // Set file length to 0, to prevent unexpected behavior in case the file already existed
                    randomAccessWriter.writeBytes("RIFF");
                    randomAccessWriter.writeInt(0); // Final file size not known yet, write 0
                    randomAccessWriter.writeBytes("WAVE");
                    randomAccessWriter.writeBytes("fmt ");
                    randomAccessWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
                    randomAccessWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
                    randomAccessWriter.writeShort(Short.reverseBytes(nChannels));// Number of channels, 1 for mono, 2 for stereo
                    randomAccessWriter.writeInt(Integer.reverseBytes(sRate)); // Sample rate
                    randomAccessWriter.writeInt(Integer.reverseBytes(sRate * bSamples * nChannels / 8)); // Byte rate, SampleRate*NumberOfChannels*BitsPerSample/8
                    randomAccessWriter.writeShort(Short.reverseBytes((short) (nChannels * bSamples / 8))); // Block align, NumberOfChannels*BitsPerSample/8
                    randomAccessWriter.writeShort(Short.reverseBytes(bSamples)); // Bits per sample
                    randomAccessWriter.writeBytes("data");
                    randomAccessWriter.writeInt(0); // Data chunk size not known yet, write 0

                    buffer = new byte[framePeriod * bSamples / 8 * nChannels];
                    state = State.READY;
                } else {
                    Log.e(TAG, "prepare() method called on uninitialized recorder");
                    state = State.ERROR;
                }
            } else {
                Log.e(TAG, "prepare() method called on illegal state");
                release();
                state = State.ERROR;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            state = State.ERROR;
        }
    }

    /**
     * Releases the resources associated with this class, and removes the unnecessary files, when necessary
     */
    public void release() {
        if (state == State.RECORDING) {
            stop();
        } else {
            if ((state == State.READY)) {
                try {
                    randomAccessWriter.close(); // Remove prepared file
                } catch (IOException e) {
                    Log.e(TAG, "I/O exception occured while closing output file");
                }
                (new File(filePath)).delete();
            }
        }
        if (audioRecorder != null) {
            audioRecorder.release();
        }
    }

    /**
     * Resets the recorder to the INITIALIZING state, as if it was just created.
     * In case the class was in RECORDING state, the recording is stopped.
     * In case of exceptions the class is set to the ERROR state.
     */
    public void reset() {
        try {
            if (state != State.ERROR) {
                release();
                filePath = null; // Reset file path
                audioRecorder = new AudioRecord(aSource, sRate, nChannels + 1, aFormat, bufferSize);
                state = State.INITIALIZING;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            state = State.ERROR;
        }
    }

    /**
     * Starts the recording, and sets the state to RECORDING. Call after prepare().
     */
    public void start() {
        if (state == State.READY) {
            payloadSize = 0;
            audioRecorder.startRecording();
            audioRecorder.read(buffer, 0, buffer.length);
            state = State.RECORDING;
        } else {
            Log.e(TAG, "start() called on illegal state");
            state = State.ERROR;
        }
    }

    /**
     * Stops the recording, and sets the state to STOPPED.
     * In case of further usage, a reset is needed.
     * Also finalizes the wave file in case of uncompressed recording.
     */
    public void stop() {
        if (state == State.RECORDING) {
            audioRecorder.stop();
            try {
                randomAccessWriter.seek(4); // Write size to RIFF header
                randomAccessWriter.writeInt(Integer.reverseBytes(36 + payloadSize)); //文件长度

                randomAccessWriter.seek(40); // Write size to Subchunk2Size field
                randomAccessWriter.writeInt(Integer.reverseBytes(payloadSize)); //语音数据的长度，比文件长度小36

                randomAccessWriter.close();
            } catch (IOException e) {
                Log.e(TAG, "I/O exception occured while closing output file");
                state = State.ERROR;
            }
            state = State.STOPPED;
        } else {
            Log.e(TAG, "stop() called on illegal state");
            state = State.ERROR;
        }
    }


}
