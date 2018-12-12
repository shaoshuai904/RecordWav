package com.maple.recorder.recording;

import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class represents a bus between { PullableSource} and  {@link OutputStream}.
 * Basically it just pulls the data from { PullableSource} and transport it to
 * {@link OutputStream}
 *
 * @author maple
 * @time 2018/4/10.
 */
public interface PullTransport {
    // 是否开启拉取
    void isEnableToBePulled(boolean enabledToBePulled);

    // 开始推送数据和写入文件
    void startPoolingAndWriting(AudioRecord audioRecord, int pullSizeInBytes, OutputStream outputStream) throws IOException;

    /**
     * 设置【沉默】监听器
     */
    interface OnSilenceListener {
        /**
         * @param silenceTime 沉默时间
         */
        void onSilence(long silenceTime);
    }

    /**
     * 设置【音频数据块】拉取监听器
     */
    interface OnAudioChunkPulledListener {
        /**
         * 拉取 音频原始数据
         *
         * @param audioChunk 音频数据块
         */
        void onAudioChunkPulled(AudioChunk audioChunk);
    }

    abstract class AbstractPullTransport implements PullTransport {
        volatile boolean pull;
        OnAudioChunkPulledListener onAudioChunkPulledListener;
        Handler handler = new Handler(Looper.getMainLooper());

        AbstractPullTransport(OnAudioChunkPulledListener onAudioChunkPulledListener) {
            this.onAudioChunkPulledListener = onAudioChunkPulledListener;
        }

        @Override
        public void isEnableToBePulled(boolean enabledToBePulled) {
            this.pull = enabledToBePulled;
        }

        // 推送 沉默时间
        void postSilenceEvent(final OnSilenceListener onSilenceListener, final long silenceTime) {
            if (onSilenceListener != null) {
                handler.post((new Runnable() {
                    @Override
                    public void run() {
                        onSilenceListener.onSilence(silenceTime);
                    }
                }));
            }
        }

        // 推送 音频原始数据块
        void postPullEvent(final AudioChunk audioChunk) {
            if (onAudioChunkPulledListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onAudioChunkPulledListener.onAudioChunkPulled(audioChunk);
                    }
                });
            }
        }
    }

    class Default extends AbstractPullTransport {

        public Default() {
            this(null);
        }

        public Default(OnAudioChunkPulledListener onAudioChunkPulledListener) {
            super(onAudioChunkPulledListener);
        }

        @Override
        public void startPoolingAndWriting(AudioRecord audioRecord, int pullSizeInBytes, OutputStream outputStream) throws IOException {
            AudioChunk audioChunk = new AudioChunk.Bytes(new byte[pullSizeInBytes]);
            while (pull) {
                int count = audioRecord.read(audioChunk.toBytes(), 0, pullSizeInBytes);
                if (AudioRecord.ERROR_INVALID_OPERATION != count && AudioRecord.ERROR_BAD_VALUE != count) {
                    postPullEvent(audioChunk);// 推送原始音频数据块
                    outputStream.write(audioChunk.toBytes());// 将数据写入文件
                }
            }
        }
    }

    /**
     * 降噪模式（只记录有声音的部分）
     */
    class Noise extends AbstractPullTransport {
        private OnSilenceListener silenceListener;
        private long silenceTimeThreshold = 200;// 沉默时间临界值
        private long startSilenceMoment = 0;// 首次沉默时间
        private long silenceTime = 0;// 已沉默的时间
        private int noiseRecordedAfterFirstSilenceThreshold = 0;

        public Noise() {
            this(null, null, 200);
        }

        public Noise(OnAudioChunkPulledListener onAudioChunkPulledListener) {
            this(onAudioChunkPulledListener, null, 200);
        }

        public Noise(OnAudioChunkPulledListener onAudioChunkPulledListener, OnSilenceListener silenceListener) {
            this(onAudioChunkPulledListener, silenceListener, 200);
        }

        /**
         * @param onAudioChunkPulledListener 音频数据推送监听
         * @param silenceListener            沉默监听
         * @param silenceTimeThreshold       可容忍的沉默时间
         */
        public Noise(OnAudioChunkPulledListener onAudioChunkPulledListener, OnSilenceListener silenceListener, long silenceTimeThreshold) {
            super(onAudioChunkPulledListener);
            this.silenceListener = silenceListener;
            this.silenceTimeThreshold = silenceTimeThreshold;
        }


        @Override
        public void startPoolingAndWriting(AudioRecord audioRecord, int pullSizeInBytes, OutputStream outputStream) throws IOException {
            AudioChunk.Shorts audioChunk = new AudioChunk.Shorts(new short[pullSizeInBytes]);
            while (pull) {
                int count = audioRecord.read(audioChunk.toShorts(), 0, pullSizeInBytes);
                if (AudioRecord.ERROR_INVALID_OPERATION != count && AudioRecord.ERROR_BAD_VALUE != count) {
                    postPullEvent(audioChunk);// 推送原始音频数据块

                    if (audioChunk.isOverSilence()) {// 是否超过沉默阀值
                        outputStream.write(audioChunk.toBytes());
                        noiseRecordedAfterFirstSilenceThreshold++;
                        if (silenceTime > 1000) {
                            if (noiseRecordedAfterFirstSilenceThreshold >= 3) {
                                // 超过1s的无声，且，之前正常记录3次。向UI推送静默时间
                                noiseRecordedAfterFirstSilenceThreshold = 0;
                                postSilenceEvent(silenceListener, silenceTime);
                            }
                        }
                        startSilenceMoment = 0;
                        silenceTime = 0;
                    } else {
                        if (startSilenceMoment == 0) {// 开始沉默
                            startSilenceMoment = System.currentTimeMillis();
                        }
                        silenceTime = System.currentTimeMillis() - startSilenceMoment;// 已沉默时间
                        if (silenceTime < silenceTimeThreshold) {
                            // 200ms 以内的无声区域仍然记录。
                            outputStream.write(audioChunk.toBytes());
                        }
                    }

                }
            }
        }

    }

}
