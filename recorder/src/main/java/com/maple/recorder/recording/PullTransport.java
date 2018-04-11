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

    void isEnableToBePulled(boolean enabledToBePulled);

    void startPoolingAndWriting(AudioRecord audioRecord, int pullSizeInBytes, OutputStream outputStream) throws IOException;

    /**
     * Interface definition for a callback to be invoked when a silence is measured.
     */
    interface OnSilenceListener {
        /**
         * Called when a silence measured
         *
         * @param silenceTime The silence measured
         */
        void onSilence(long silenceTime);
    }

    /**
     * Interface definition for a callback to be invoked when a chunk of audio is pulled from
     * { PullableSource}.
     */
    interface OnAudioChunkPulledListener {
        /**
         * Called when { PullableSource} is pulled and returned{@link AudioChunk}.
         */
        void onAudioChunkPulled(AudioChunk audioChunk);
    }

    abstract class AbstractPullTransport implements PullTransport {
        volatile boolean pull;
        final OnAudioChunkPulledListener onAudioChunkPulledListener;
        private final Handler handler = new Handler(Looper.getMainLooper());

        AbstractPullTransport(OnAudioChunkPulledListener onAudioChunkPulledListener) {
            this.onAudioChunkPulledListener = onAudioChunkPulledListener;
        }

        @Override
        public void startPoolingAndWriting(AudioRecord audioRecord, int pullSizeInBytes, OutputStream outputStream) throws IOException {

        }

        @Override
        public void isEnableToBePulled(boolean enabledToBePulled) {
            this.pull = enabledToBePulled;
        }

        void postSilenceEvent(final OnSilenceListener onSilenceListener, final long silenceTime) {
            handler.post((new Runnable() {
                @Override
                public void run() {
                    onSilenceListener.onSilence(silenceTime);
                }
            }));
        }

        void postPullEvent(final AudioChunk audioChunk) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onAudioChunkPulledListener.onAudioChunkPulled(audioChunk);
                }
            });
        }
    }

    final class Default extends AbstractPullTransport {

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
                audioChunk.readCount(audioRecord.read(audioChunk.toBytes(), 0, pullSizeInBytes));
                if (AudioRecord.ERROR_INVALID_OPERATION != audioChunk.readCount()
                        && AudioRecord.ERROR_BAD_VALUE != audioChunk.readCount()) {
                    if (onAudioChunkPulledListener != null) {
                        postPullEvent(audioChunk);
                    }
                    outputStream.write(audioChunk.toBytes());
                }
            }
        }
    }

    final class Noise extends AbstractPullTransport {
        private final long silenceTimeThreshold;
        private final OnSilenceListener silenceListener;
        private long firstSilenceMoment = 0;
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

        public Noise(OnAudioChunkPulledListener onAudioChunkPulledListener, OnSilenceListener silenceListener, long silenceTimeThreshold) {
            super(onAudioChunkPulledListener);
            this.silenceListener = silenceListener;
            this.silenceTimeThreshold = silenceTimeThreshold;
        }

        @Override
        public void startPoolingAndWriting(AudioRecord audioRecord, int pullSizeInBytes, OutputStream outputStream) throws IOException {
            final AudioChunk.Shorts audioChunk = new AudioChunk.Shorts(new short[pullSizeInBytes]);
            while (pull) {
                short[] shorts = audioChunk.toShorts();
                audioChunk.readCount(audioRecord.read(shorts, 0, shorts.length));
                if (AudioRecord.ERROR_INVALID_OPERATION != audioChunk.readCount()
                        && AudioRecord.ERROR_BAD_VALUE != audioChunk.readCount()) {
                    if (onAudioChunkPulledListener != null) {
                        postPullEvent(audioChunk);
                    }
                    if (audioChunk.peakIndex() > -1) {
                        outputStream.write(audioChunk.toBytes());
                        firstSilenceMoment = 0;
                        noiseRecordedAfterFirstSilenceThreshold++;
                    } else {
                        if (firstSilenceMoment == 0) {
                            firstSilenceMoment = System.currentTimeMillis();
                        }
                        final long silenceTime = System.currentTimeMillis() - firstSilenceMoment;
                        if (firstSilenceMoment != 0 && silenceTime > this.silenceTimeThreshold) {
                            if (silenceTime > 1000) {
                                if (noiseRecordedAfterFirstSilenceThreshold >= 3) {
                                    noiseRecordedAfterFirstSilenceThreshold = 0;
                                    if (silenceListener != null) {
                                        postSilenceEvent(silenceListener, silenceTime);
                                    }
                                }
                            }
                        } else {
                            outputStream.write(audioChunk.toBytes());
                        }
                    }
                }
            }
        }

    }

}
