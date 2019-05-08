package com.maple.recorder.recording;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * An AudioChunk is a audio data wrapper.
 * 音频数据包装器
 *
 * @author maple
 * @time 2018/4/10.
 */
public interface AudioChunk {
    // 获取最大峰值(振幅)
    double maxAmplitude();

    // 获取byte类型数据
    byte[] toBytes();

    // 获取short类型数据
    short[] toShorts();


    abstract class AbstractAudioChunk implements AudioChunk {
        private static final double REFERENCE = 0.6;

        @Override
        public double maxAmplitude() {
            int nMaxAmp = 0;
            for (short sh : toShorts()) {
                if (sh > nMaxAmp) {
                    nMaxAmp = sh;
                }
            }
            if (nMaxAmp > 0) {
                return Math.abs(20 * Math.log10(nMaxAmp / REFERENCE));
            } else {
                return 0;
            }
        }
    }

    /**
     * byte类型数据包装器
     */
    class Bytes extends AbstractAudioChunk {
        private byte[] bytes;

        Bytes(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public byte[] toBytes() {
            return bytes;
        }

        @Override
        public short[] toShorts() {
            short[] shorts = new short[bytes.length / 2];
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
            return shorts;
        }
    }

    /**
     * short类型数据包装器
     */
    class Shorts extends AbstractAudioChunk {
        private static final short SILENCE_THRESHOLD = 2700;// 沉默阀值（低于该值的不记录）
        private short[] shorts;

        Shorts(short[] bytes) {
            this.shorts = bytes;
        }

        // 是否超过沉默值
        boolean isOverSilence() {
            for (short sh : shorts) {
                if (sh > SILENCE_THRESHOLD || sh < -SILENCE_THRESHOLD) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public byte[] toBytes() {
            byte[] buffer = new byte[shorts.length * 2];
            for (int i = 0; i < shorts.length; i++) {
                buffer[2 * i] = (byte) (shorts[i] & 0x00FF);
                buffer[2 * i + 1] = (byte) ((shorts[i] & 0xFF00) >> 8);
            }
            return buffer;
        }

        @Override
        public short[] toShorts() {
            return shorts;
        }

    }
}
