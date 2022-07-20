package com.maple.recorder.recording;

import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 录音数据 拉取 运输机
 * <p>
 * 此类表示「录音机」和「输出文件」之间的总线。
 * 基本上它只是从{@link AudioRecord}中提取数据，并将其传输到{@link OutputStream}，以写入输出文件。
 * 可以对每次音频数据拉取过程进行监听{@link OnAudioChunkPulledListener}和处理{@link OnSilenceListener}，
 *
 * @author maple
 * @time 2018/4/10.
 */
public interface PullTransport {
    /**
     * 是否开启拉取
     */
    void isEnableToBePulled(boolean enabledToBePulled);

    /**
     * 开始推送数据和写入文件
     */
    void startPoolingAndWriting(AudioRecord audioRecord, int pullSizeInBytes, OutputStream outputStream) throws IOException;

    /**
     * 设置【沉默】监听器
     */
    interface OnSilenceListener {
        /**
         * @param silenceTime 沉默时间
         * @param discardTime 丢弃时间
         */
        void onSilence(long silenceTime, long discardTime);
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

        AbstractPullTransport() {
        }

        @Override
        public void isEnableToBePulled(boolean enabledToBePulled) {
            this.pull = enabledToBePulled;
        }

        /**
         * 推送 音频原始数据块
         */
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
            super();
        }

        /**
         * 音频数据推送监听，不间断的回调录音数据。
         */
        public Default setOnAudioChunkPulledListener(OnAudioChunkPulledListener onAudioChunkPulledListener) {
            this.onAudioChunkPulledListener = onAudioChunkPulledListener;
            return this;
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
        private OnSilenceListener onSilenceListener;
        private long pushTimeThreshold = 500;// 忽略沉默时间的阀值。小于该值的沉默时间将不推送
        private long silenceTimeThreshold = 200;// 可容忍的沉默时间，该时间内正常记录
        private long startSilenceMoment = 0;// 首次沉默时间点
        private long silenceTime = 0;// 已沉默的时间
        private int writeCountAfterSilence = 0;// 噪音后，正常记录次数

        public Noise() {
            super();
        }

        /**
         * 音频数据推送监听，不间断的回调录音数据。
         */
        public Noise setOnAudioChunkPulledListener(OnAudioChunkPulledListener onAudioChunkPulledListener) {
            this.onAudioChunkPulledListener = onAudioChunkPulledListener;
            return this;
        }

        /**
         * 沉默监听，当沉默 >1s 时，回调已沉默的最大时间。
         */
        public Noise setOnSilenceListener(OnSilenceListener onSilenceListener) {
            this.onSilenceListener = onSilenceListener;
            return this;
        }

        /**
         * 设置可容忍的沉默时间。在可容忍时间内，仍然写入录音数据。
         */
        public Noise setSilenceTimeThreshold(long silenceTimeThreshold) {
            this.silenceTimeThreshold = silenceTimeThreshold;
            return this;
        }

        /**
         * 设置忽略沉默时间的阀值。已沉默时间小于该值时，不向UI推送
         */
        public Noise setPushTimeThreshold(long pushTimeThreshold) {
            this.pushTimeThreshold = pushTimeThreshold;
            return this;
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
                        writeCountAfterSilence++;
                        if (silenceTime > pushTimeThreshold) {
                            if (writeCountAfterSilence >= 3) {
                                // 超过1s的无声，且，之前正常记录3次。向UI推送静默时间
                                writeCountAfterSilence = 0;
                                postSilenceEvent(silenceTime, (silenceTime - silenceTimeThreshold));
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

        /**
         * 推送 沉默时间
         */
        private void postSilenceEvent(final long silenceTime, final long discardTime) {
            if (onSilenceListener != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onSilenceListener.onSilence(silenceTime, discardTime);
                    }
                });
            }
        }

    }

}
