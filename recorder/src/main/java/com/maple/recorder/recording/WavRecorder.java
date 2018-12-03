package com.maple.recorder.recording;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Wav格式的音频记录器
 *
 * @author maple
 * @time 2018/4/10.
 */
final class WavRecorder extends BaseDataRecorder {

    public WavRecorder(File file, AudioRecordConfig config, PullTransport pullTransport) {
        super(file, config, pullTransport);
    }

    @Override
    public void stopRecording() {
        try {
            super.stopRecording();
            writeWavHeader();
        } catch (IOException e) {
            throw new RuntimeException("Error in applying wav header", e);
        }
    }

    private void writeWavHeader() throws IOException {
        RandomAccessFile wavFile = randomAccessFile(file);
        wavFile.seek(0); // to the beginning
        wavFile.write(new WavHeader(config, file.length()).toBytes());
        wavFile.close();
    }

    private RandomAccessFile randomAccessFile(File file) {
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return randomAccessFile;
    }
}