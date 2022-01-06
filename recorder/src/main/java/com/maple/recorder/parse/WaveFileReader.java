package com.maple.recorder.parse;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * wav文件 解析器
 *
 * @author maple
 * @time 2018/2/1 下午4:06
 */
public class WaveFileReader {
    private int[][] data = null;
    private int len = 0;
    private int numChannels = 0;
    private long sampleRate = 0;
    private int bitsPerSample = 0;
    private FileInputStream fis = null;
    private BufferedInputStream bis = null;
    private boolean isSuccess = false;

    public WaveFileReader(String filename) {
        this.initReader(filename);
    }

    /**
     * 判断是否创建wav读取器成功
     */
    public boolean isSuccess() {
        return isSuccess;
    }

    /**
     * 获取每个采样的编码长度，8bit或者16bit
     */
    public int getBitPerSample() {
        return this.bitsPerSample;
    }

    /**
     * 获取采样率
     */
    public long getSampleRate() {
        return this.sampleRate;
    }

    /**
     * 获取声道个数，1代表单声道 2代表立体声
     */
    public int getNumChannels() {
        return this.numChannels;
    }

    /**
     * 获取数据长度，也就是一共采样多少个
     */
    public int getDataLen() {
        return this.len;
    }

    /**
     * 获取数据
     * 数据是一个二维数组，[n][m]代表第n个声道的第m个采样值
     */
    public int[][] getData() {
        return this.data;
    }

    private void initReader(String filename) {
        try {
            fis = new FileInputStream(filename);
            bis = new BufferedInputStream(fis);

            // --- RIFF区块 ---
            String riffFlag = readString(4);
            if (!"RIFF".equals(riffFlag)) {
                throw new IllegalArgumentException("RIFF miss, " + filename + " is not a wave file.");
            }
            long chunkSize = readLong();// 文件数据长度: 该值 = fileSize - 8。
            String waveFlag = readString(4);
            if (!"WAVE".equals(waveFlag)) {
                throw new IllegalArgumentException("WAVE miss, " + filename + " is not a wave file.");
            }

            // --- FORMAT区块 ---
            String fmtFlag = readString(4);
            if (!"fmt ".equals(fmtFlag)) {
                throw new IllegalArgumentException("fmt miss, " + filename + " is not a wave file.");
            }
            long subChunk1Size = readLong();// 格式块长度: 取决于编码格式，可以是 16、18、20、40 等
            int audioFormat = readInt();// AudioFormat(音频格式): 常见的 PCM 音频数据的值为1。
            this.numChannels = readInt();// NumChannels(声道数): 1：单声道，2：双声道/立体声
            this.sampleRate = readLong();// SampleRate(采样率): 每个声道单位时间采样次数。常用的采样频率有 11025, 22050 和 44100 kHz。
            long byteRate = readLong();// ByteRate(数据传输速率): 每秒数据字节数，该数值为:声道数×采样频率×采样位数/8。
            int blockAlign = readInt();// BlockAlign(数据块对齐): 采样帧大小。该数值为:声道数×采样位数/8。
            this.bitsPerSample = readInt();// BitsPerSample(采样位数): 每个采样存储的bit数。常见的位数有 8、16

            // --- DATA区块 ---
            String dataFlag = readString(4);
            if (!"data".equals(dataFlag)) {
                throw new IllegalArgumentException("data miss, " + filename + " is not a wave file.");
            }
            long audioLength = readLong();// 音频数据长度: N = ByteRate * seconds

            // 读取数据
            this.len = (int) (audioLength / (this.bitsPerSample / 8) / this.numChannels);
            this.data = new int[this.numChannels][this.len];
            for (int i = 0; i < this.len; ++i) {
                for (int n = 0; n < this.numChannels; ++n) {
                    if (this.bitsPerSample == 8) {
                        this.data[n][i] = bis.read();
                    } else if (this.bitsPerSample == 16) {
                        this.data[n][i] = this.readInt();
                    }
                }
            }

            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private String readString(int len) {
        byte[] buf = new byte[len];
        try {
            if (bis.read(buf) != len) {
                throw new IOException("no more data!!!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buf);
    }

    private int readInt() {
        byte[] buf = new byte[2];
        int res = 0;
        try {
            if (bis.read(buf) != 2) {
                throw new IOException("no more data!!!");
            }
            res = (buf[0] & 0x000000FF) | (((int) buf[1]) << 8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private long readLong() {
        long res = 0;
        try {
            long[] l = new long[4];
            for (int i = 0; i < 4; ++i) {
                l[i] = bis.read();
                if (l[i] == -1) {
                    throw new IOException("no more data!!!");
                }
            }
            res = l[0] | (l[1] << 8) | (l[2] << 16) | (l[3] << 24);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private byte[] readBytes(int len) {
        byte[] buf = new byte[len];
        try {
            if (bis.read(buf) != len) {
                throw new IOException("no more data!!!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf;
    }

    public static int[] readSingleChannel(String filename) {
        if (filename == null || filename.length() == 0) {
            return null;
        }
        try {
            WaveFileReader reader = new WaveFileReader(filename);
            int[] res = reader.getData()[0];
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
